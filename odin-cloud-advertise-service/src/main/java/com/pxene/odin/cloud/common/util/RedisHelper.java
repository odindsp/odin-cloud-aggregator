package com.pxene.odin.cloud.common.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * redis工具类
 * @author lizhuoling
 *
 */
@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RedisHelper {
	
	private JedisPool jedisPool;
	
	@Autowired
	private RedisHelper(Environment env){
		RedisHelperConfig.init(env);
	}
	
	/**
	 * 获取前缀
	 * @param prefix
	 */
	public void select(String prefix) {
		this.jedisPool = RedisHelperConfig.pools.get(prefix);
	}
	
	/**
	 * 从jedis连接池中获取jedis对象
	 * @return
	 */
	public Jedis getJedis() {
		// 一个pool可分配jedis实例个数
		return jedisPool.getResource();
	}
	
	/**
	 * 回收jedis
	 * @param jedis
	 */
	public void close(Jedis jedis) {
		jedis.close();
	}
	
	/**
	 * 设置过期时间
	 * @param key
	 * @param seconds
	 */
	public void expire(String key, int seconds) {
		if (seconds <= 0) {
			return;
		}
		Jedis jedis = getJedis();
		jedis.expire(key, seconds);
		jedis.close();
	}
	
	/**
	 * 设置默认过期时间
	 * @param key
	 */
	public void expire(String key) {
		expire(key, RedisHelperConfig.DEFAULT_EXPIRE);
	}
	
	/**
	 * 给redis数据库中名称为key的string赋值value
	 * @param key
	 * @param value String类型
	 */
	public void set(String key, String value) {
		if (isBlank(key)) {
			return;
		}
		Jedis jedis = getJedis();
		jedis.set(key, value);
		close(jedis);
	}
	/**
	 * 给redis数据库中名称为key的string复制value
	 * @param key
	 * @param value Object类型
	 */
	public void set(String key, Object value) {
		if (isBlank(key)) {
			return;
		}
		Jedis jedis = getJedis();
		jedis.set(key.getBytes(), SerializeUtils.serialize(value));
	}
	
	/**
	 * 给redis数据库中名称为key的string复制value
	 * @param key
	 * @param value int类型
	 */
	public void set(String key, int value) {
		if (isBlank(key)) {
			return;
		}
		set(key, String.valueOf(value));
	}
	
	/**
	 *  给redis数据库中名称为key的string复制value
	 * @param key
	 * @param value long类型
	 */
	public void set(String key, long value) {
		if (isBlank(key)) {
			return;
		}
		set(key, String.valueOf(value));
	}
	
	/**
	 * 给redis数据库中名称为key的string复制value
	 * @param key
	 * @param value float类型
	 */
	public void set(String key, float value) {
		if (isBlank(key)) {
			return;
		}
		set(key, String.valueOf(value));
	}
	/**
	 * 给redis数据库中名称为key的string复制value
	 * @param key
	 * @param value double类型
	 */
	public void set(String key, double value) {
		if (isBlank(key)) {
			return;
		}
		set(key, String.valueOf(value));
	}
	
	/**
	 * 如果不存在名称为key的string，则向库中添加string，名称为key，值为value
	 * @param key
	 * @param value
	 */
	public boolean setnx(String key, int value) {
		if (isBlank(key)) {
			return false;
		}
		Jedis jedis = getJedis();
		Long flag = jedis.setnx(key, String.valueOf(value));
		close(jedis);
		if (flag > 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * 获取名称为key的value
	 * @param key
	 * @return 返回float类型
	 */
	public Float getFolat(String key) {
		if (isBlank(key)) {
			return null;
		}
		return Float.valueOf(getStr(key));
	}
	
	/**
	 * 获取名称为key的value
	 * @param key 
	 * @return 返回double类型
	 */
	public Double getDouble(String key) {
		if (isBlank(key)) {
			return null;
		}
		return Double.valueOf(getStr(key));
	}
	
	/**
	 * 获取名称为key的value
	 * @param key 
	 * @return 返回Long类型
	 */
	public Long getLong(String key) {
		if (isBlank(key)) {
			return null;
		}			
		return Long.valueOf(getStr(key));
	}
	
	/**
	 * 获取名称为key的value
	 * @param key
	 * @return 返回Integer类型
	 */
	public Integer getInt(String key) {
		if (isBlank(key)) {
			return null;
		}
		return Integer.valueOf(getStr(key));
	}
	
	/**
	 * 获取名称为key的string的value
	 * @param key
	 * @return
	 */
	public String getStr(String key) {
		if (isBlank(key)) {
			return null;
		}
		Jedis jedis = getJedis();
		String value = jedis.get(key);
		close(jedis);
		return value;
	}
	
	/**
	 * 获取名称为key的value
	 * @param key
	 * @return 返回Object
	 */
	public Object getObj(String key) {
		if (isBlank(key)) {
			return null;
		}
		Jedis jedis = getJedis();
		if (jedis.get(key.getBytes()) == null) {
			return null;
		}
		byte[] bytes = jedis.get(key.getBytes());
		Object obj = SerializeUtils.unserialize(bytes);
		close(jedis);
		return obj;
	}
	
	/**
	 * 获取keys
	 * @param pattern
	 * @return
	 */
	public String[] getKeys(String pattern) {
		if (isBlank(pattern)) {
			return null;
		}
		Jedis jedis = getJedis();
		Set<String> keySet = jedis.keys(pattern);
		String[] keys = new String[keySet.size()];
		int index = 0;
		for (String key : keySet) {
			keys[index] = key;
			index++;
		}
		close(jedis);
		return keys;
	}
	
	/**
	 * 删除key
	 * @param key
	 */
	public void delete(String key) {
		Jedis jedis = getJedis();
		if (jedis.get(key) != null) {
			jedis.del(key);
		}
		close(jedis);
	}
	
	/**
	 * 批量删除
	 * @param pattern
	 */
	public void deleteByPattern(String pattern) {
		Jedis jedis = getJedis();
		String[] keys = getKeys(pattern);
		if (keys != null && keys.length != 0) {
			if (keys.length == 1) {
				// 如果只有一个key，删除第一个即可
				jedis.del(keys[0]);
			} else {
				jedis.del(keys);
			}
		}
		close(jedis);
	}
	
	/**
	 * 向名称为key的set中添加元素member
	 * @param key
	 * @param members
	 */
	public void sset(String key, String members) {
		Jedis jedis = getJedis();
		jedis.sadd(key, members);
		close(jedis);
	}
	
	/**
	 * 返回名称为key的set的所有元素
	 * @param key
	 * @return
	 */
	public Set<String> sget(String key) {
		Jedis jedis = getJedis();
		close(jedis);
		return jedis.smembers(key);
	}
	
	/**
	 * 测试member是否是名称为key的set的元素
	 * @param key
	 * @param member
	 * @return
	 */
	public boolean sismember(String key, String member) {
		Jedis jedis = getJedis();
		boolean sismember = jedis.sismember(key, member);
		close(jedis);
		return sismember;
	}
	
	/**
	 * 删除名称为key的set中的元素member
	 * @param key
	 * @param members
	 */
	public void sdelete(String key, String members) {
		Jedis jedis = getJedis();
		jedis.srem(key, members);
		close(jedis);
	}
	
	/**
	 * 向名称为key的hash中添加元素
	 * @param key
	 * @param value
	 */
	public void hmset(String key, Map<String, String> value) {
		Jedis jedis = getJedis();
		jedis.hmset(key, value);
		close(jedis);
	}
	
	/**
	 * 返回名称为key的hash中所有的键（field）及其对应的value
	 * @param key
	 * @return
	 */
	public Map<String, String> hgetAll(String key) {
		if (isBlank(key)) {
			return null;
		}
		Jedis jedis = getJedis();
		Map<String, String> hgetAll = jedis.hgetAll(key);
		close(jedis);
		return hgetAll;
	}
	
	/**
	 * 返回名称为key的hash中field对应的value
	 * @param key
	 * @param field
	 * @return
	 */
	public String hget(String key, String field) {
		if (isBlank(key)) {
			return null;
		}
		Jedis jedis = getJedis();
		String result = jedis.hget(key, field);
		close(jedis);
		return result;
	}
	
	/**
	 * 返回名称为key的hash中所有键
	 * @param key
	 * @return
	 */
	public Set<String> hkeys(String key) {
		if (isBlank(key)) {
			return null;
		}
		Jedis jedis = getJedis();
		Set<String> hkeys = jedis.hkeys(key);
		close(jedis);
		return hkeys;
	}
	
	/**
	 * 删除一个key
	 * @param key
	 */
	public void hdelete (String key) {
		Jedis jedis = getJedis();
		if (jedis.hgetAll(key) != null) {
			jedis.del(key);
		}
		close(jedis);
	}		
	
	/**
	 * 判断是否为空
	 * @param key
	 * @return
	 */
	public boolean isBlank(String key) {
		return key == null || "".equals(key.trim());
	}
	
	/**
	 * 确认一个key是否存在
	 * @param key
	 * @return
	 */
	public boolean isExists(String key) {
		Jedis jedis = getJedis();
		boolean isExists = jedis.exists(key);
		close(jedis);
		return isExists;
	}
	
	/**
	 * 向名称为key的set中添加元素member
	 * @param key
	 * @param values
	 */
	public void addKey(String key, List<String> values) {
		if (values != null && !values.isEmpty()) {
			Jedis jedis = getJedis();
			for (String value : values) {
				jedis.sadd(key, value);
			}
			close(jedis);
		}
	}
	
	public void checkAndSetDeadLoop(String key, String value) {
		if (isBlank(key)) {
			return;
		}
		Jedis jedis = getJedis();
		Transaction transaction = null;
		List<Object> list = null;
		
		while (true) {
			jedis.watch(key);
			// 开启事务
			transaction = jedis.multi();
			// 执行业务以及调用jedis提供的接口功能
			transaction.set(key, value);
			// 执行事务
			list = transaction.exec();
			
			if (list != null && !list.isEmpty() && list.get(0) != null && "OK".equalsIgnoreCase(list.get(0).toString())) {
				break;
			}
		}
		close(jedis);
	}
	
	public boolean checkAndSet(String key, String value) {
		if (isBlank(key)) {
			return false;
		}
		Jedis jedis = getJedis();
		jedis.watch(key);
		// 开启事务
		Transaction transaction = jedis.multi();
		// 执行业务以及调用jedis提供的接口功能
		transaction.set(key, value);
		// 执行事务
		List<Object> list = transaction.exec();
		
		close(jedis);
				
		return checkIfAllOK(list);
	}
	
	/**
	 * 事务
	 * @param jedis
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean doTransaction(Jedis jedis, String key, String value) {
		
		Transaction transaction = jedis.multi();
		transaction.set(key, value);
		
		List<Object> list = transaction.exec();
		
		close(jedis);
		
		return checkIfAllOK(list);
	}
	
	/**
	 * 检查Redis中一个事务中的全部操作是否都成功
	 * @param list 事务操作的全部返回值
	 * @return
	 */
	public boolean checkIfAllOK(List<Object> list) {
		if (list != null && !list.isEmpty()) {
			for (Object object : list) {
				if (!"OK".equalsIgnoreCase(object.toString())) {
					return false;
				}				
			}
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 为哈希表 key 中的域 field 加上浮点数增量 increment
	 * @param key 键名
	 * @param field  域
	 * @param increment  增量值，可正可负
	 */
	public void hincrbyFloat(String key, String field, float increment) {
		if (isBlank(key)) {
			throw new IllegalArgumentException();
		}
		Jedis jedis = getJedis();
		jedis.hincrByFloat(key, field, increment);
		close(jedis);
	}
	
	/**
	 * 名称为key的string增加integer
	 * @param key
	 * @param increment
	 */
	public void incrybyInt(String key, int increment) {
		if (isBlank(key)) {
			throw new IllegalArgumentException();
		}
		Jedis jedis = getJedis();
		jedis.incrBy(key, increment);
		close(jedis);
	}
	
	public void incryByDouble(String key, double increment) {
		if (isBlank(key)) {
			throw new IllegalArgumentException();
		}
		Jedis jedis = getJedis();
		jedis.incrByFloat(key, increment);
		close(jedis);
	}
}
