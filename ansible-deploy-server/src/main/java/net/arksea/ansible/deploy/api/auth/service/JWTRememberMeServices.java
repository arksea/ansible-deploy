package net.arksea.ansible.deploy.api.auth.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class JWTRememberMeServices extends AbstractRememberMeServices {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private AuthService authService;

    public JWTRememberMeServices(AuthService authService, UserDetailsService userDetailsService, JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        super("ansible-deploy", userDetailsService);
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.authService = authService;
    }

    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request,
                                                 HttpServletResponse response) {
        if (cookieTokens.length != 1) {
            throw new InvalidCookieException(
                    "Cookie token did not contain 1" + " tokens, but contained '" + Arrays.asList(cookieTokens) + "'");
        }
        Jwt jwt = jwtDecoder.decode(cookieTokens[0]);
        Instant exp = jwt.getExpiresAt();
        if (exp == null || exp.isBefore(Instant.now())) {
            throw new InvalidCookieException("Cookie token has expired (expired on '"
                    + exp + "'; current time is '" + Instant.now() + "')");
        }
        // Check the user exists. Defer lookup until after expiry time checked, to
        // possibly avoid expensive database call.
        String sub = jwt.getSubject();
        String rolesStr = (String)jwt.getClaims().get("roles");
        List<String> roles = new ArrayList<>(StringUtils.commaDelimitedListToSet(rolesStr));
        //当用户使用有效的JWT Token登录时，直接用Token中保存的角色，从AuthServer获取缓存的权限信息，而无需访问数据库
        Collection<GrantedAuthority> authorities = authService.getByRoles(roles).stream()
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return new UserDetailsImpl(sub, "", false, authorities, roles);
    }

    /**
     * Creates a new token for the current user, setting the expiry time according to the
     * @param request the request.
     * @param response the response.
     * @param authentication the current user authentication.
     */
    @Override
    public void onLoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String username = retrieveUserName(authentication);
        // If unable to find a username, just abort as
        // JWTRememberMeServices is
        // unable to construct a valid token in this case.
        if (!StringUtils.hasLength(username)) {
            this.logger.debug("Unable to retrieve username");
            return;
        }
        Instant now = Instant.now();
        int tokenLifetime = TWO_WEEKS_S;
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String roles = String.join(",", userDetails.getRoles());
        logger.debug("onLoginSuccess() roles: " + roles);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("AnsibleDeploy")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(tokenLifetime))
                .subject(authentication.getName())
                .claim("roles", roles)
                .build();
        String token = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        setCookie(new String[] { token }, tokenLifetime, request, response);
    }

    protected String retrieveUserName(Authentication authentication) {
        if (isInstanceOfUserDetails(authentication)) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        return authentication.getPrincipal().toString();
    }

    private boolean isInstanceOfUserDetails(Authentication authentication) {
        return authentication.getPrincipal() instanceof UserDetails;
    }

    protected String encodeCookie(String[] cookieTokens) {
        return cookieTokens[0];
    }

    protected String[] decodeCookie(String cookieValue) throws InvalidCookieException {
        return new String[] {cookieValue};
    }
}
