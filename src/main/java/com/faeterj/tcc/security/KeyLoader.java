package com.faeterj.tcc.security;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyLoader {

    public static RSAPublicKey loadPublicKey() throws Exception {
        String publicKeyPath = "/etc/secrets/public.pem";  // Caminho do arquivo da chave pública
        String publicKeyPEM = readKeyFromFile(publicKeyPath);
        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM.replaceAll("-----BEGIN PUBLIC KEY-----", "").replaceAll("-----END PUBLIC KEY-----", "").replaceAll("\\s", ""));
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    public static RSAPrivateKey loadPrivateKey() throws Exception {
        String privateKeyPath = "/etc/secrets/private.pem";  // Caminho do arquivo da chave privada
        String privateKeyPEM = readKeyFromFile(privateKeyPath);
        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM.replaceAll("-----BEGIN PRIVATE KEY-----", "").replaceAll("-----END PRIVATE KEY-----", "").replaceAll("\\s", ""));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    private static String readKeyFromFile(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        byte[] keyBytes = new byte[fis.available()];
        fis.read(keyBytes);
        fis.close();
        return new String(keyBytes);
    }
}
