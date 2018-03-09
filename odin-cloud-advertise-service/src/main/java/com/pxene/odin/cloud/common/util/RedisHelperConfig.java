package com.pxene.odin.cloud.common.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis的连接
 * @author lizhuoling
 *
 */
public class RedisHelperConfig {
	
	public static final String REDIS_PRIMARY = "redis.primary.";
	
	public static final int DEFAULT_TIMEOUT = 10000;
	
	public static final int DEFAULT_EXPIRE = 60000;
	
	public static Map<String, JedisPool> pools = new HashMap<String, JedisPool>();
	
	public static void init(Environment env) {
		if (!pools.containsKey(REDIS_PRIMARY)) {
			JedisPool primaryPool = getJedisPool(env, REDIS_PRIMARY);
			pools.put(REDIS_PRIMARY, primaryPool);
		}
	}
	
	private static JedisPool getJedisPool(Environment env, String prefix) {
		// 获取redis连接的参数值
		String ip = env.getProperty(prefix + "ip");
		int port = Integer.parseInt(env.getProperty(prefix + "port"));
		String password = env.getProperty(prefix + "password");
		int maxActive = Integer.parseInt(env.getProperty(prefix + "pool.maxActive"));
		int maxIdle = Integer.parseInt(env.getProperty(prefix + "pool.maxIdle"));
		long maxWait = Integer.parseInt(env.getProperty(prefix + "pool.maxWait"));
		boolean testOnBorrow = Boolean.parseBoolean(env.getProperty(prefix + "pool.testOnBorrow"));
		boolean testOnReturn = Boolean.parseBoolean(env.getProperty(prefix + "pool.testOnReturn"));
		
		JedisPoolConfig config = new JedisPoolConfig();
		// 为pool分配jedis实例个数
		config.setMaxTotal(maxActive);
		// 控制一个pool最多有几个状态为空闲的jedis实例
		config.setMaxIdle(maxIdle);
		config.setMaxWaitMillis(maxWait);
		// borrow一个jedis实例时是否提前进行validate操作，如果为ture则得到的实例都是可用的
		config.setTestOnBorrow(testOnBorrow);
		config.setTestOnReturn(testOnReturn);
		
		if (!StringUtils.isEmpty(password)) {
			return new JedisPool(config, ip, port, DEFAULT_TIMEOUT, password);
		} else {
			// 获取连接池10000毫秒延时
			return new JedisPool(config, ip, port, 10000);
		}
	}
}
