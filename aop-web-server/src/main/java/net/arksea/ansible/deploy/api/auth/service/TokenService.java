package net.arksea.ansible.deploy.api.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 *
 * Created by xiaohaixing on 2020/04/29.
 */
@Component
public class TokenService {
    private static final int COOKIE_EXPIRY = 600000;
    private static final int EXPIRES_SECONDS = 604800; //7天
    private static Logger logger = LogManager.getLogger(TokenService.class);
    private Algorithm algorithm;
    private final String ISSUER = "felink.com";

    @Value("${config.web.tokenSecret}")
    String tokenSecret;

    @PostConstruct
    public void init() throws UnsupportedEncodingException {
        logger.debug("tokenSecret: {}", tokenSecret);
        algorithm = Algorithm.HMAC256(tokenSecret);
    }

    public int getCookieExpiry() {
        return COOKIE_EXPIRY;
    }
    public String getCookieName() {
        return "access_token";
    }

    /**
     * @param userName
     * @param userId
     * @return Pair of token and expiresTime
     * @throws JWTCreationException
     */
    public Pair<String,Long> create(String userName,long userId) throws JWTCreationException {
        long exp = System.currentTimeMillis()+EXPIRES_SECONDS*1000L;
        String token = JWT.create()
            .withIssuer(ISSUER)
            .withExpiresAt(new Date(exp))
            .withClaim("userName", userName)
            .withClaim("userId", userId)
            .sign(algorithm);
        return Pair.of(token, exp);
    }

    public DecodedJWT verify(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .acceptExpiresAt(EXPIRES_SECONDS)
                .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);
            Date now = new Date(System.currentTimeMillis());
            if (jwt.getExpiresAt().after(now)) {
                return jwt;
            } else {
                return null;
            }
        } catch (JWTVerificationException ex){
            logger.warn("token verify failed", ex);
            return null;
        }
    }
}
