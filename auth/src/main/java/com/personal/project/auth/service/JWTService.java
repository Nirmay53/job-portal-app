package com.personal.project.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.personal.project.auth.entity.JWTHeader;
import com.personal.project.auth.entity.JWTPayload;
import com.personal.project.common.entity.User;

@Service
public class JWTService {
    @Autowired
    RSAEncryptionService encryptionService;
    @Value("${auth.jwt.token-validity-secs}")
    private Integer expirySeconds;

    public String getSHA256Hashed(String input) {
        byte[] hash;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unknown hash algorithm", e);
        }
        
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = String.format("%02x", b);
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    public String generateToken(User userDetails) throws JsonProcessingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        JWTHeader header = new JWTHeader("RS256", "JWT");
        JWTPayload payload = new JWTPayload(userDetails, expirySeconds);
        ObjectWriter ow = new ObjectMapper().writer();
        String encodedHeader = Base64.getUrlEncoder().withoutPadding().encodeToString(ow.writeValueAsString(header).getBytes());
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(ow.writeValueAsString(payload).getBytes());
        
        StringBuilder token = new StringBuilder(encodedHeader + "." + encodedPayload);
        token.append("." + encryptionService.encrypt(getSHA256Hashed(token.toString())));
        return token.toString();
    }

    public JWTPayload extractJwtPayload(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }
            String encodedHeader = parts[0];
            String encodedPayload = parts[1];
            String receivedSignature = parts[2];
    
            String dataToVerify = encodedHeader + "." + encodedPayload;

            String decryptedSignature = encryptionService.decrypt(receivedSignature);

            if (!getSHA256Hashed(dataToVerify).equals(decryptedSignature)) {
                return null;
            }
            String decodedPayload = new String(Base64.getUrlDecoder().decode(encodedPayload));
            ObjectMapper objectMapper = new ObjectMapper();
            JWTPayload payload = objectMapper.readValue(decodedPayload, JWTPayload.class);
            if (payload.getExp() * 1000 < System.currentTimeMillis()) {
                return null;
            }
            
            return payload;
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return null;
        }
    }
    
    public boolean validateToken(String token) {
        return null != extractJwtPayload(token);
    }
}
