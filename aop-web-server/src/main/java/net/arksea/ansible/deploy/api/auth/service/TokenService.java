package net.arksea.ansible.deploy.api.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 * Created by xiaohaixing on 2020/04/29.
 */
@Component
public class TokenService {
    private static Logger logger = LogManager.getLogger(TokenService.class);
    private Algorithm algorithm;
    private final String ISSUER = "arksea.net";

    @Value("${tokenService.secret:123456}")
    String tokenSecret;
    @Value("${tokenService.expiresSeconds:3600}")
    int expiresSeconds;
    @PostConstruct
    public void init() throws UnsupportedEncodingException {
        algorithm = Algorithm.HMAC256(tokenSecret);
    }

    /**
     * @return Pair of token and expiresTime
     * @throws IllegalArgumentException, JWTCreationException
     */
    public String create(Map<String,Object> claimMap) throws IllegalArgumentException, JWTCreationException {
        long exp = System.currentTimeMillis()+expiresSeconds*1000L;
        JWTCreator.Builder b = JWT.create()
                .withIssuer(ISSUER)
                .withExpiresAt(new Date(exp));
        claimMap.forEach((k,v) -> {
            if (v instanceof String) {
                b.withClaim(k,(String)v);
            } else if (v instanceof Boolean) {
                b.withClaim(k, (Boolean)v);
            } else if (v instanceof Integer) {
                b.withClaim(k, (Integer)v);
            } else if (v instanceof Long) {
                b.withClaim(k, (Long)v);
            } else if (v instanceof Double) {
                b.withClaim(k, (Double)v);
            } else if (v instanceof Date) {
                b.withClaim(k, (Date)v);
            } else {
                b.withClaim(k, v.toString());
            }
        });
        return b.sign(algorithm);
    }

    public DecodedJWT verify(String token) {
        return JWT.require(algorithm)
            .withIssuer(ISSUER)
            .build().verify(token);
    }

    public Optional<DecodedJWT> verifyNotThrow(String token) {
        try {
            return Optional.of(JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build().verify(token));
        } catch (JWTVerificationException ex){
            logger.warn("token verify failed", ex);
            return Optional.empty();
        }
    }

    public static void main(String[] args) throws Exception {
        Configurator.initialize("", "./src/test/resources/log4j2-test.xml");
        TokenService svc = new TokenService();
        svc.expiresSeconds = 60;
        svc.tokenSecret = "123456";
        svc.init();
        Map<String,Object> claimMap = new HashMap<>();
        claimMap.put("verId", 17);
        claimMap.put("buildNo", 5);
        String token = svc.create(claimMap);
        System.out.println("token: "+token);
//        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ2ZXJJZCI6MTcsImlzcyI6ImFya3NlYS5uZXQiLCJleHAiOjE2MTU1MjA2ODgsImJ1aWxkTm8iOjV9.-FK7CbvEQI43V21EZeTgmMCeEsh8rhKYlsyzKL0fdZc";
//        DecodedJWT jwt = svc.verify(token);
//        System.out.println(jwt.getExpiresAt());
    }
}
