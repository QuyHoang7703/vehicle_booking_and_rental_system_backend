//package com.pbl6.VehicleBookingRental.user.config;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.data.redis.serializer.RedisSerializer;
//import org.springframework.data.redis.serializer.SerializationException;
//
//import java.nio.charset.StandardCharsets;
//
//public class Jackson2JsonRedisSerializer<T> implements RedisSerializer<T> {
//    private final ObjectMapper objectMapper;
//    private final Class<T> type;
//
//    public Jackson2JsonRedisSerializer(ObjectMapper objectMapper, Class<T> type) {
//        this.objectMapper = objectMapper;
//        this.type = type;
//    }
//
//    @Override
//    public byte[] serialize(T t) throws SerializationException {
//        try {
//            if (t == null) {
//                return new byte[0];
//            }
//            return objectMapper.writeValueAsBytes(t);
//        } catch (Exception e) {
//            throw new SerializationException("Could not write JSON: " + e.getMessage(), e);
//        }
//    }
//
//    @Override
//    public T deserialize(byte[] bytes) throws SerializationException {
//        try {
//            if (bytes == null || bytes.length == 0) {
//                return null;
//            }
//            String jsonString = new String(bytes, StandardCharsets.UTF_8);
//            System.out.println("Deserializing JSON: " + jsonString);
//            return objectMapper.readValue(bytes, type);  // Deserialize into the correct type
//        } catch (Exception e) {
//            throw new SerializationException("Could not read JSON: " + e.getMessage(), e);
//        }
//    }
//}
//
//
