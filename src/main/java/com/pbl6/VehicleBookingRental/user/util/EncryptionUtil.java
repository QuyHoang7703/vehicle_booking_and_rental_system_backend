package com.pbl6.VehicleBookingRental.user.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class EncryptionUtil {

    private static final String ALGORITHM_AES = "AES";
    private static final int AES_KEY_SIZE = 128;
    private static final String ALGORITHM_RSA = "RSA";
    @Value("${rsa.public.key}")
    private String publicKeyString;

    @Value("${rsa.private.key}")
    private String privateKeyString;

    // Tạo khóa AES
    public SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM_AES);
        keyGenerator.init(AES_KEY_SIZE);
        return keyGenerator.generateKey();
    }

    // Mã hóa dữ liệu bằng AES
    public String encrypt(String data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM_AES);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // Giải mã dữ liệu bằng AES
    public String decrypt(String encryptedData, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM_AES);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes);
    }

    // Chuyển đổi chuỗi thành SecretKey
    public SecretKey getKeyFromString(String keyString) {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM_AES);
    }

    // Mã hóa khóa AES bằng RSA
    public String encryptAESKey(SecretKey aesKey, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(aesKey.getEncoded()));
    }

    // Giải mã khóa AES bằng RSA
    public SecretKey decryptAESKey(String encryptedAESKey, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedKey = cipher.doFinal(Base64.getDecoder().decode(encryptedAESKey));
        return new SecretKeySpec(decryptedKey, ALGORITHM_AES);
    }

    // Tạo cặp khóa RSA
    public KeyPair generateRSAKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM_RSA);
        keyGen.initialize(2048); // hoặc 512 cho thử nghiệm
        return keyGen.generateKeyPair();
    }

    // Chuyển đổi PublicKey từ chuỗi
    public PublicKey getPublicKeyFromString(String publicKeyString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
        return keyFactory.generatePublic(spec);
    }

    // Chuyển đổi PrivateKey từ chuỗi
    public PrivateKey getPrivateKeyFromString(String privateKeyString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
        return keyFactory.generatePrivate(spec);
    }
}
