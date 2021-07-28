package net.arksea.ansible.deploy.api.auth.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TokenServiceTest {
    @Test public void testValidToken() throws UnsupportedEncodingException {
        TokenService svc = new TokenService();
        svc.expiresSeconds = 5;
        svc.tokenSecret = "123456";
        svc.init();
        Map<String,Object> claimMap = new HashMap<>();
        long time = System.currentTimeMillis() / 1000 * 1000; //时间类型claim精度为秒
        Date date = new Date(time);
        claimMap.put("key1", 1);
        claimMap.put("key2", "2");
        claimMap.put("key3", 3L);
        claimMap.put("key4", 4.0d);
        claimMap.put("key5", date);
        String token = svc.create(claimMap);
        DecodedJWT jwt = svc.verify(token);
        int v1 = jwt.getClaim("key1").asInt();
        assertEquals(1,v1);
        String v2 = jwt.getClaim("key2").asString();
        assertEquals("2",v2);
        long v3 = jwt.getClaim("key3").asLong();
        assertEquals(3L,v3);
        double v4 = jwt.getClaim("key4").asDouble();
        assertEquals(4.0d,v4,0.001d);
        Date v5 = jwt.getClaim("key5").asDate();
        assertEquals(date,v5);
    }

    @Test(expected = TokenExpiredException.class)
    public void testExpires() throws JWTVerificationException, UnsupportedEncodingException {
        TokenService svc = new TokenService();
        svc.expiresSeconds = -30;
        svc.tokenSecret = "123456";
        svc.init();
        Map<String,Object> claimMap = new HashMap<>();
        claimMap.put("key1", 1);
        String token = svc.create(claimMap);
        svc.expiresSeconds = 30;
        svc.verify(token);
    }

    @Test(expected = SignatureVerificationException.class)
    public void testInvalidToken() throws JWTVerificationException, UnsupportedEncodingException {
        TokenService svc = new TokenService();
        svc.expiresSeconds = 300;
        svc.tokenSecret = "invalid secret";
        svc.init();
        Map<String,Object> claimMap = new HashMap<>();
        claimMap.put("key1", 1);
        String tokenInvalid = svc.create(claimMap);
        svc.tokenSecret = "123456";
        svc.init();
        svc.verify(tokenInvalid);
    }
}
