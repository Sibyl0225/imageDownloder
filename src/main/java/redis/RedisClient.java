package redis;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

public class RedisClient {

    public Jedis jedis;//非切片额客户端连接
    public JedisPool jedisPool;//非切片连接池
    public ShardedJedis shardedJedis;//切片额客户端连接
    public ShardedJedisPool shardedJedisPool;//切片连接池
    public ResourceBundle resource = ResourceBundle.getBundle("redis");
    
    public RedisClient() 
    { 
        initialPool(); 
        initialShardedPool(); 
        shardedJedis = shardedJedisPool.getResource(); 
        jedis = jedisPool.getResource(); 
               
    } 
    
    public void getRedisProperties(){
    	
    	//redis.name redisClient
    	//redis.password sion8940
    	//redis.host 127.0.0.1
    	//redis.port 6379,
    	//redis.ssh_port 22,
    	//redis.timeout_connect 60000,
    	//redis.timeout_execute 60000
    	
    	ResourceBundle resource = ResourceBundle.getBundle("redis");
    	//resource.getString("redis.name");
    	String password = resource.getString("redis.password");
    	String host = resource.getString("redis.host");
    	String port = resource.getString("redis.port");
    	String timeout = resource.getString("redis.timeout_connect");
    	//resource.getString("redis.timeout_execute");
    	
    }
 
    /**
     * 初始化非切片池
     */
    private void initialPool() 
    { 
        // 池基本配置 
        JedisPoolConfig config = new JedisPoolConfig(); 
        config.setMaxTotal(20); 
        config.setMaxIdle(5); 
        config.setMaxWaitMillis(1000l); 
        config.setTestOnBorrow(false); 
        
    	String password = resource.getString("redis.password");
    	String host = resource.getString("redis.host");
    	String port = resource.getString("redis.port");
    	String timeout = resource.getString("redis.timeout_connect");
    	
        jedisPool = new JedisPool(config,
        							host,
        		   Integer.valueOf(port),
        		 Integer.valueOf(timeout),
        		                password,
					        	    0);
    }
    
    /** 
     * 初始化切片池 
     */ 
    private void initialShardedPool() 
    { 
        // 池基本配置 
        JedisPoolConfig config = new JedisPoolConfig(); 
        config.setMaxTotal(20);
        config.setMaxIdle(5); 
        config.setMaxWaitMillis(1000l); 
        config.setTestOnBorrow(false); 
        // slave链接 
        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>(); 
        
    	String password = resource.getString("redis.password");
    	String host = resource.getString("redis.host");
    	String port = resource.getString("redis.port");
    	String timeout = resource.getString("redis.timeout_connect");
    	
        JedisShardInfo jedisShardInfo = new JedisShardInfo(host, 6379,Integer.valueOf(port), "master");
        jedisShardInfo.setPassword(password);
        jedisShardInfo.setConnectionTimeout(Integer.valueOf(timeout));
        shards.add(jedisShardInfo); 

        // 构造池 
        shardedJedisPool = new ShardedJedisPool(config, shards); 
    } 


}