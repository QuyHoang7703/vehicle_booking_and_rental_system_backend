package com.pbl6.VehicleBookingRental.user.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pbl6.VehicleBookingRental.user.util.RedisMessageListener;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    @Value("${redis.port}")
    private String redisPort;
    @Value("${redis.host}")
    private String redisHost;

//    private final RedisMessageListener redisMessageListener;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisHost);
        redisStandaloneConfiguration.setPort(Integer.parseInt(redisPort));

        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

//    @Bean
//    public RedisTemplate<String, Object> redisTemplate() {
//        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//
//        redisTemplate.setConnectionFactory(jedisConnectionFactory());
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
//
//        return redisTemplate;
//    }
   private final ObjectMapper objectMapper;
    @Bean
    public <K, V> RedisTemplate<K, V> redisTemplate() {
        RedisTemplate<K, V> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(jedisConnectionFactory());
//        redisTemplate.setKeySerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
//        redisTemplate.setHashKeySerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        redisTemplate.setKeySerializer(new StringRedisSerializer()); // Key là chuỗi
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, BusTripSchedule.class));
//        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, BusTripSchedule.class));
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

        return redisTemplate;
    }

    @Bean
    <K, F, V>HashOperations<K, F, V> hashOperations(RedisTemplate<K, V> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter(RedisMessageListener redisMessageListener) {
        return new MessageListenerAdapter(redisMessageListener, "onMessage");
    }

    @Bean
    public ChannelTopic topic() {
        return new ChannelTopic("__keyevent@0__:expired");
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(MessageListenerAdapter messageListenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());

        // Register listener for channel "__keyevent@0__:expired"
        container.addMessageListener(messageListenerAdapter, topic());
        return container;
    }


    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        // Cấu hình kết nối Redis (single server, cluster, hoặc sentinel)
        String redisAddress = "redis://" + redisHost + ":" + redisPort;
        config.useSingleServer().setAddress(redisAddress);
//        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        return Redisson.create(config);
    }



}
