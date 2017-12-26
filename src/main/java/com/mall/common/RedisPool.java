package com.mall.common;

import com.mall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by chuck Date:2017/12/17
 */
public class RedisPool {

    private static JedisPool pool;// jedis 连接池
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total", "20"));  //最大连接数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle", "10"));//在jedispool 中最大的idle(空闲)状态的jedis实例的个数
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle", "2"));//在jedispool 中最小的idle(空闲)状态的jedis实例的个数
    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow", "true"));//在borrow 一个jedis 实例的时候,是否需要进行验证操作,如赋值为true,则得到的jedis 肯定是可以用的.
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return", "true"));//在return 一个jedis 实例的时候,是否需要进行验证操作,如赋值为true,则放回jedispool 的jedis实例 肯定是可以用的.

    private static String redisIp = PropertiesUtil.getProperty("redis.ip");
    private static Integer redisPort = Integer.parseInt(PropertiesUtil.getProperty("redis.port"));

    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);

        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        config.setBlockWhenExhausted(true);// 连接耗尽的时候, 是否阻塞, false 会抛出异常,true 阻塞直到超时,默认为true.

        pool = new JedisPool(config, redisIp, redisPort, 1000 * 2);
    }

    static {
        initPool();
    }

    public static Jedis getJedis() {
        return pool.getResource();
    }

    public static void returnBrokenResource(Jedis jedis) {
        pool.returnBrokenResource(jedis);
    }


    public static void returnResource(Jedis jedis) {
        pool.returnResource(jedis);
    }


    public static void main(String[] args) {
        Jedis jedis = pool.getResource();
        jedis.set("chuckkey", "chuckvalue");
        returnResource(jedis);
        pool.destroy();// 临时调用  销魂连接池中的所有连接
        System.out.println("program is end");
    }

}
