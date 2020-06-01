package com.clei.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * redis 工具类
 *
 * @author KIyA
 * @date 2020-05-27
 */
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 写入缓存
     * @param key
     * @param value
     * @return
     */
    public void set(String key, Object value){

        ValueOperations<Serializable,Object> valueOperations = redisTemplate.opsForValue();

        valueOperations.set(key,value);
    }

    /**
     * 写入缓存 指定失效秒数
     * @param key
     * @param value
     * @param expireSeconds
     * @return
     */
    public void set(String key, Object value, long expireSeconds){
        set(key, value, expireSeconds, TimeUnit.SECONDS);
    }

    /**
     * 写入缓存 指定失效时间
     * @param key
     * @param value
     * @param expireTime
     * @param timeUnit
     * @return
     */
    public void set(String key, Object value, long expireTime, TimeUnit timeUnit){

        ValueOperations<Serializable,Object> valueOperations = redisTemplate.opsForValue();

        valueOperations.set(key, value, expireTime, timeUnit);
    }

    /**
     * key是否存在
     * @param key
     * @return
     */
    public boolean exists(String key){
        return redisTemplate.hasKey(key);
    }

    /**
     * 获取值
     * @param key
     */
    public Object get(String key){

        ValueOperations<Serializable,Object> valueOperations = redisTemplate.opsForValue();

        return valueOperations.get(key);
    }

    /**
     * 删除键值
     * @param key
     */
    public boolean del(String key){
        return redisTemplate.delete(key);
    }

}
