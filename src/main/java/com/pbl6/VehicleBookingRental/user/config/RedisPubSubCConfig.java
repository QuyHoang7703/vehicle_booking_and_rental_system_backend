//package com.pbl6.VehicleBookingRental.user.config;
//
//import com.pbl6.VehicleBookingRental.user.util.RedisMessageListener;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
//import org.springframework.data.redis.listener.ChannelTopic;
//import org.springframework.data.redis.listener.RedisMessageListenerContainer;
//
//@Configuration
//@RequiredArgsConstructor
//public class RedisPubSubCConfig {
//    private final RedisMessageListener redisMessageListener;
//
//    private final JedisConnectionFactory jedisConnectionFactory;
//
//    @Bean
//    public RedisMessageListenerContainer redisMessageListenerContainer() {
//        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//        container.setConnectionFactory(jedisConnectionFactory);
//
//        // Đăng ký listener cho kênh "order-events"
//        container.addMessageListener(redisMessageListener, new ChannelTopic("order-events"));
//        return container;
//    }
//}
