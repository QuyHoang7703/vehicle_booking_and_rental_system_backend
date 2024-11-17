//package com.pbl6.VehicleBookingRental.user.config;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.RedisSerializer;
//import org.springframework.data.redis.serializer.SerializationException;
//
//public class CustomJacksonRedisSerializer<T> implements RedisSerializer<T> {
//    private final ObjectMapper objectMapper;
//    private final TypeReference<T> typeReference;
//
//    public CustomJacksonRedisSerializer(ObjectMapper objectMapper, TypeReference<T> typeReference) {
//        this.objectMapper = objectMapper;
//        this.typeReference = typeReference;
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
//            throw new SerializationException("Error serializing object", e);
//        }
//    }
//
//    @Override
//    public T deserialize(byte[] bytes) throws SerializationException {
//        try {
//            if (bytes == null || bytes.length == 0) {
//                return null;
//            }
//            return objectMapper.readValue(bytes, typeReference);
//        } catch (Exception e) {
//            throw new SerializationException("Error deserializing object", e);
//        }
//    }
//}
