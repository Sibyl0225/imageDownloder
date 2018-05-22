package redis;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisTest {
	
    private static JedisPool jedisPool;

	/** 
     * 初始化redis连接池 
     */  
    private static void init() {  
        JedisPoolConfig config = new JedisPoolConfig(); // Jedis连接池  
        config.setMaxIdle(8); // 最大空闲连接数  
        config.setMaxTotal(8);// 最大连接数  
        config.setMaxWaitMillis(1000); // 获取连接是的最大等待时间，如果超时就抛出异常  
        config.setTestOnBorrow(false);// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；  
        config.setTestOnReturn(true);  
        //"localhost"  "192.168.8.128"
        jedisPool = new JedisPool(config, "localhost", 6379, 5000, "sion8940", 0); // 配置、ip、端口、连接超时时间、密码、数据库编号（0~15）  
    } 
    
    private static VideoModel initVideo() {  
    	VideoModel video = new VideoModel();
    	video.setName("死屁鬼的成长之路！");
    	video.setPath("C:\\死屁鬼的成长之路！.mp4");
    	video.setSize((long) 1234568);
    	video.setCreateDate(new Date());
		return video;
    } 
    
//    public static void main(String[] args) {
//        //连接本地的 Redis 服务
//        Jedis jedis = new Jedis("localhost");
//        jedis.auth("sion8940");
//        System.out.println("连接成功");
//        //查看服务是否运行
//        System.out.println("服务正在运行: "+jedis.ping());
//    }
    public static void main(String[] args) {
        //连接本地的 Redis 服务
    	init();
    	Jedis jedis = jedisPool.getResource();
        System.out.println("连接成功");
        
        // 获取数据并输出
        Set<String> keys = jedis.keys("*"); 
        Iterator<String> it=keys.iterator() ;   
        while(it.hasNext()){   
            String key = it.next();   
            System.out.println(key);   
        }
        
        jedis.flushAll();

//        jedis.set("Path", "C:\\Redis-x64-3.2.100");
//        System.out.println("RedisPath: "+jedis.get("Path"));
//        VideoModel video = initVideo();
//        jedis.set(video.getName(),video.getPath());
//        System.out.println("片名："+video.getName()+";地址："+jedis.get(video.getName()));
        
        
        
    }
}
