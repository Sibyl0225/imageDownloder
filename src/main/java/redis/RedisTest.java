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
     * ��ʼ��redis���ӳ� 
     */  
    private static void init() {  
        JedisPoolConfig config = new JedisPoolConfig(); // Jedis���ӳ�  
        config.setMaxIdle(8); // ������������  
        config.setMaxTotal(8);// ���������  
        config.setMaxWaitMillis(1000); // ��ȡ�����ǵ����ȴ�ʱ�䣬�����ʱ���׳��쳣  
        config.setTestOnBorrow(false);// ��borrowһ��jedisʵ��ʱ���Ƿ���ǰ����validate���������Ϊtrue����õ���jedisʵ�����ǿ��õģ�  
        config.setTestOnReturn(true);  
        //"localhost"  "192.168.8.128"
        jedisPool = new JedisPool(config, "localhost", 6379, 5000, "sion8940", 0); // ���á�ip���˿ڡ����ӳ�ʱʱ�䡢���롢���ݿ��ţ�0~15��  
    } 
    
    private static VideoModel initVideo() {  
    	VideoModel video = new VideoModel();
    	video.setName("��ƨ��ĳɳ�֮·��");
    	video.setPath("C:\\��ƨ��ĳɳ�֮·��.mp4");
    	video.setSize((long) 1234568);
    	video.setCreateDate(new Date());
		return video;
    } 
    
//    public static void main(String[] args) {
//        //���ӱ��ص� Redis ����
//        Jedis jedis = new Jedis("localhost");
//        jedis.auth("sion8940");
//        System.out.println("���ӳɹ�");
//        //�鿴�����Ƿ�����
//        System.out.println("������������: "+jedis.ping());
//    }
    public static void main(String[] args) {
        //���ӱ��ص� Redis ����
    	init();
    	Jedis jedis = jedisPool.getResource();
        System.out.println("���ӳɹ�");
        
        // ��ȡ���ݲ����
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
//        System.out.println("Ƭ����"+video.getName()+";��ַ��"+jedis.get(video.getName()));
        
        
        
    }
}
