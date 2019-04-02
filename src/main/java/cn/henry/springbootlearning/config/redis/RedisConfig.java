package cn.henry.springbootlearning.config.redis;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Miraculous on 15/7/4.
 */
@Configuration
public class RedisConfig {
    /**
     * 设置默认过期时间
     */
    @Value("${pandaloan.redis.master.host}")
    private String masterHost;
    @Value("${pandaloan.redis.master.port}")
    private int masterPort;
    @Value("${pandaloan.redis.master.name}")
    private String masterName;
    @Value("${pandaloan.redis.sentinel1.host}")
    private String sentinel1Host;
    @Value("${pandaloan.redis.sentinel1.port}")
    private int sentinel1port;
    @Value("${pandaloan.redis.sentinel2.host}")
    private String sentinel2Host;
    @Value("${pandaloan.redis.sentinel2.port}")
    private int sentinel2port;
    @Value("${pandaloan.redis.sentinel3.host}")
    private String sentinel3Host;
    @Value("${pandaloan.redis.sentinel3.port}")
    private int sentinel3port;
    @Value("${pandaloan.redis.database}")
    private Integer database;

    private RedisConnectionFactory generateDevConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(masterHost);
        factory.setPort(masterPort);
        factory.setUsePool(true);
        factory.setDatabase(database == null ? 0:database);
        factory.setConvertPipelineAndTxResults(true);
        JedisPoolConfig poolConfig = generatePoolConfig();
        factory.setPoolConfig(poolConfig);
        factory.afterPropertiesSet();
        return factory;
    }

    private RedisConnectionFactory generateReleaseConnectionFactory() {
        RedisSentinelConfiguration sentinelConfiguration = new RedisSentinelConfiguration();
        Set<RedisNode> sentinels = new HashSet<>();
        RedisNode sentinel1 = new RedisNode(sentinel1Host, sentinel1port);
        RedisNode sentinel2 = new RedisNode(sentinel2Host, sentinel2port);
        RedisNode sentinel3 = new RedisNode(sentinel3Host, sentinel3port);
        sentinels.add(sentinel1);
        sentinels.add(sentinel2);
        sentinels.add(sentinel3);
        sentinelConfiguration.setMaster(masterName);
        sentinelConfiguration.setSentinels(sentinels);
        JedisPoolConfig poolConfig = generatePoolConfig();
        JedisConnectionFactory factory = new JedisConnectionFactory(sentinelConfiguration, poolConfig);
        factory.setTimeout(15000);
        factory.setUsePool(true);
        factory.setDatabase(database == null ? 0: database);
        factory.setConvertPipelineAndTxResults(true);
        factory.afterPropertiesSet();
        return factory;
    }

    private JedisPoolConfig generatePoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMinIdle(20);
        poolConfig.setMaxTotal(500);
        poolConfig.setMaxWaitMillis(10000);
        poolConfig.setTestOnBorrow(true);
        return poolConfig;
    }

    @Primary
    @Bean(name = "redisConnectionFactory")
    RedisConnectionFactory factory() {
        if (StringUtils.isBlank(masterName)) {
            return generateDevConnectionFactory();
        } else {
            return generateReleaseConnectionFactory();
        }
    }

    @Primary
    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory factory) {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
        template.setEnableTransactionSupport(false);
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jdkSerializationRedisSerializer);
        template.setDefaultSerializer(jdkSerializationRedisSerializer);
        template.setConnectionFactory(factory);
        return template;
    }

    @Primary
    @Bean(name = "stringRedisTemplate")
    public RedisTemplate<String, String> stringRedisTemplate(
            RedisConnectionFactory factory) {
        final RedisTemplate<String, String> template = new RedisTemplate<>();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setEnableTransactionSupport(false);
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setValueSerializer(stringRedisSerializer);
        template.setDefaultSerializer(stringRedisSerializer);
        template.setConnectionFactory(factory);
        return template;
    }

}
