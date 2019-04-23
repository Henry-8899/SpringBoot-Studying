package cn.henry.springbootlearning.utils;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;

/**
 * Created by tums on 2015/11/30.
 */
@Slf4j
public final class MD5Util {

    public static String build(String content) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest
                    .getInstance("md5");
        } catch (Exception e) {
            log.error("[exception][MD5Util_build]md5签名出错，error={}", e);
            return null;
        }
        messageDigest.update(content.getBytes());
        byte[] domain = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < domain.length; i++) {
            if (Integer.toHexString(0xFF & domain[i]).length() == 1) {
                md5StrBuff.append("0").append(
                        Integer.toHexString(0xFF & domain[i]));
            } else {
                md5StrBuff.append(Integer.toHexString(0xFF & domain[i]));
            }
        }
        return md5StrBuff.toString();
    }
}
