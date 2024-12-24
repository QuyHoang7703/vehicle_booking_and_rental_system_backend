package com.pbl6.VehicleBookingRental.user.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

// K: key, F: Field, V: Value
public interface RedisService<K, F, V> {
    // Lưu một giá trị đơn giản vào Redis với key
    void setValue(K key, V value);

    // Thiết lập TTL cho key
    void setTimeToLive(K key, long timeoutInMinutes);

    // Lưu giá trị trong Hash
    void setHashSet(K key, F field, V value);

    // Kiểm tra sự tồn tại của field trong Hash
    boolean isHashFieldExists(K key, F field);

    // Lấy giá trị từ Redis
    V getValue(K key);

    // Lấy toàn bộ field và giá trị từ Hash
    Map<F, V> getAllHashValues(K key);

    // Lấy giá trị từ Hash
    V getHashValue(K key, F field);

    // Lấy các giá trị trong Hash theo danh sách field
    List<V> hashGetByFieldPrefix(K key, List<F> fieldPrefixes);

    // Lấy danh sách toàn bộ field trong Hash qua Key
    Set<F> getAllHashFields(K key);

    // Xóa một key khỏi Redis
    void deleteKey(K key);

    // Xóa một field trong Hash
    void deleteHashFile(K key, F field);

    // Xóa nhiều field trong Hash
    void deleteHashFields(K key, List<F> fields);
    public long incrementHashValue(K key, F field, long incrementBy);
    public Object runLuaScript(String script, List<String> keys, List<String> args);
}
