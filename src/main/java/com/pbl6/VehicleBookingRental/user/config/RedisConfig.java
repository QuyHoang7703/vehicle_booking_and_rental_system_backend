//package com.pbl6.VehicleBookingRental.user.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
//import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
//import org.springframework.data.redis.core.HashOperations;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//@Configuration
//public class RedisConfig {
//    @Value("6379")
//    private String redisPort;
//    @Value("localhost")
//    private String redisHost;
//
//    @Bean
//    public JedisConnectionFactory jedisConnectionFactory() {
//        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
//        redisStandaloneConfiguration.setHostName(redisHost);
//        redisStandaloneConfiguration.setPort(Integer.parseInt(redisPort));
//
//        return new JedisConnectionFactory(redisStandaloneConfiguration);
//    }
//
////    @Bean
////    public RedisTemplate<String, Object> redisTemplate() {
////        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
////
////        redisTemplate.setConnectionFactory(jedisConnectionFactory());
////        redisTemplate.setKeySerializer(new StringRedisSerializer());
////        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
////        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
////        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
////
////        return redisTemplate;
////    }
//
//    @Bean
//    public <K, V> RedisTemplate<K, V> redisTemplate() {
//        RedisTemplate<K, V> redisTemplate = new RedisTemplate<>();
//
//        redisTemplate.setConnectionFactory(jedisConnectionFactory());
//        redisTemplate.setKeySerializer(new GenericJackson2JsonRedisSerializer());
//        redisTemplate.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
//        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
//
//        return redisTemplate;
//    }
//
//    @Bean
//    <K, F, V>HashOperations<K, F, V> hashOperations(RedisTemplate<K, V> redisTemplate) {
//        return redisTemplate.opsForHash();
//    }
//
//
//}
