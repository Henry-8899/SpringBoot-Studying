package cn.henry.springbootlearning.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by weiliu on 2017/10/19.
 */
@Slf4j
public class RedisLockHelper {
    private RedisTemplate redisTemplate;
    /**
     * Lock key path.
     */
    private String lockKey;

    /**
     * 锁超时时间，防止线程在入锁以后，无限的执行等待
     */
    private int expireMsecs = 60 * 1000;

    /**
     * 锁等待时间，防止线程饥饿
     */
    private int timeoutMsecs = 10 * 1000;

    /**
     * 当前锁的到期时间字符串
     */
    private volatile String expiresStr = null;


    /**
     * Detailed constructor with default acquire timeout 10000 ms and lock expiration of 60000 ms.
     *
     * @param redisTemplate 注入一个 redisTemplate
     * @param lockKey       lock key (ex. account:1, ...)
     */
    public RedisLockHelper(RedisTemplate redisTemplate, String lockKey) {
        this.redisTemplate = redisTemplate;
        this.lockKey = "lock:".concat(lockKey).concat("_lock");
        log.info("准备锁 lock:{},{}", getLockKey());
    }

    /**
     * Detailed constructor with default lock expiration of 60000 ms.
     *
     * @param redisTemplate 注入一个 redisTemplate
     * @param lockKey       锁KEY  (ex. account:1, ...)
     * @param timeoutMsecs  锁等待时间 默认  10 * 1000 ms
     */
    public RedisLockHelper(RedisTemplate redisTemplate, String lockKey, int timeoutMsecs) {
        this(redisTemplate, lockKey);
        this.timeoutMsecs = timeoutMsecs;
    }

    /**
     * Detailed constructor.
     *
     * @param redisTemplate 注入一个 redisTemplate
     * @param lockKey       锁KEY  (ex. account:1, ...)
     * @param timeoutMsecs  锁等待时间 默认  10 * 1000 ms
     * @param expireMsecs   锁超时时间 默认 60 * 1000 ms
     */
    public RedisLockHelper(RedisTemplate redisTemplate, String lockKey, int timeoutMsecs, int expireMsecs) {
        this(redisTemplate, lockKey, timeoutMsecs);
        this.expireMsecs = expireMsecs;
    }

    public String getLockKey() {
        return lockKey;
    }

    /**
     * 获取key -> value
     *
     * @param key
     * @return
     */
    private String get(final String key){
        Object obj = null;
        try {
            obj = redisTemplate.execute((RedisCallback<Object>) connection -> {
                StringRedisSerializer serializer = new StringRedisSerializer();
                byte[] data = connection.get(serializer.serialize(key));
                connection.close();
                if (data == null) {
                    return null;
                }
                return serializer.deserialize(data);
            });
        } catch (Exception e) {
            log.error("get redis error, key " + key + ": {}", e);
            throw new InvalidDataAccessResourceUsageException(e.getMessage());
        }
        return obj != null ? obj.toString() : null;
    }

    /**
     * SET if Not exists
     *
     * @param key
     * @param value
     * @return
     */
    private boolean setNX(final String key, final String value) {
        Object obj = null;
        try {
            obj = redisTemplate.execute((RedisCallback<Object>) connection -> {
                StringRedisSerializer serializer = new StringRedisSerializer();
                Boolean success = connection.setNX(serializer.serialize(key), serializer.serialize(value));
                connection.close();
                return success;
            });
        } catch (Exception e) {
            log.error("setNX redis error, key “" + key + "”: {}", e);
        }
        return obj != null ? (Boolean) obj : false;
    }

    /**
     * getSet 命令在Redis键中设置指定的字符串值，并返回其旧值
     *
     * @param key
     * @param value
     * @return
     */
    private String getSet(final String key, final String value) {
        Object obj = null;
        try {
            obj = redisTemplate.execute((RedisCallback<Object>) connection -> {
                StringRedisSerializer serializer = new StringRedisSerializer();
                byte[] ret = connection.getSet(serializer.serialize(key), serializer.serialize(value));
                connection.close();
                return serializer.deserialize(ret);
            });
        } catch (Exception e) {
            log.error("getSet redis error, key : {}", key);
        }
        return obj != null ? (String) obj : null;
    }

    /**
     * lock
     *
     * @return
     * @throws InterruptedException
     */
    public synchronized boolean lock() throws InterruptedException {
        int timeout = timeoutMsecs;
        int retryCount = 0;
        while (timeout >= 0) {
            long expires = System.currentTimeMillis() + expireMsecs + 1;
            String expiresStr = RandomStringUtils.randomAlphanumeric(5) + String.valueOf(expires);
            if (this.setNX(lockKey, expiresStr)) {
                // lock acquired
                this.expiresStr = expiresStr;
                log.info("获取锁成功 lock:{}", getLockKey());
                return true;
            }

            String currentValueStr; //redis里的时间
            try {
                if ((currentValueStr = this.get(lockKey)) == null) {
                    continue;
                }
                if (Long.parseLong(currentValueStr.substring(5)) < System.currentTimeMillis()) {
                    //判断是否为空，不为空的情况下，如果被其他线程设置了值，则第二个条件判断是过不去的
                    // lock is expired
                    String oldValueStr = this.getSet(lockKey, expiresStr);
                    //获取上一个锁到期时间，并设置现在的锁到期时间，
                    //只有一个线程才能获取上一个线上的设置时间，因为redis.getSet是同步的
                    if (currentValueStr.equals(oldValueStr)) {
                        //防止误删（覆盖，因为key是相同的）了他人的锁——这里达不到效果，这里值会被覆盖，但是因为什么相差了很少的时间，所以可以接受
                        //[分布式的情况下]:如过这个时候，多个线程恰好都到了这里，但是只有一个线程的设置值和当前值相同，他才有权利获取锁
                        // lock acquired
                        this.expiresStr = expiresStr;
                        log.info("获取锁成功（前一个锁超时） lock:{}", getLockKey());
                        return true;
                    }
                }
                int sleepTime = ThreadLocalRandom.current().nextInt(10) * 10;
                sleepTime += 100; //保证随机等待时间为 100-200ms
                timeout -= sleepTime;
                /*
                    延迟 随机的等待时间 毫秒, 防止饥饿进程的出现,即,当同时到达多个进程,
                    只会有一个进程获得锁,其他的都用随机的频率进行尝试,后面有来了一些进行,也以同样的频率申请锁,这将可能导致前面来的锁得不到满足.
                 */
                Thread.sleep(sleepTime);
            } catch (InvalidDataAccessResourceUsageException e) {
                if (retryCount > 3) {
                    log.error("redis有可能出现问题不能获取lock：{}", lockKey);
                    log.error("redis lock 3次重试仍异常， {} 进行降级服务....", lockKey);
                    return true;
                }
                retryCount++;
                if (timeout > 100) {
                    timeout -= 100;
                } else {
                    timeout = 1;
                }
                Thread.sleep(100);
            }
        }
        log.error("锁失败 lock:{}", getLockKey());
        return false;
    }

    /**
     * 解锁
     */
    public synchronized void unlock() {
        if (this.expiresStr != null) {
            /*如果存在超时字符串则 判断是否是自己的超时字符串*/
            try {
                String expStr = null;
                try {
                    expStr = this.get(lockKey);
                } catch (InvalidDataAccessResourceUsageException e) {
                    log.warn("redis unlock 异常， {} 进行降级服务....", lockKey);
                }
                if (expStr == null || this.expiresStr.equals(expStr)) {
                    redisTemplate.delete(lockKey);
                   // log.info("解锁成功 lock:{}", getLockKey());
                    return;
                }
            } catch (Exception e) {
                log.error("redis unlock 异常 lockKey "+lockKey+"：{} ", e);
            }
        }
        log.warn("没有锁或者因非本对象产生的锁，不能进行解锁，因为是不安全的");

    }

}
