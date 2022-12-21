package net.arksea.ansible.deploy.api.system;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import net.arksea.ansible.deploy.api.auth.service.AuthService;
import net.arksea.ansible.deploy.api.auth.service.AuthenticationEntryPointImpl;
import net.arksea.ansible.deploy.api.auth.service.JWTRememberMeServices;
import net.arksea.ansible.deploy.api.auth.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.annotation.Resource;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Resource(name = "systemProfile")
    String systemProfile;

    @Value("${jwt.public.key}")
    RSAPublicKey publicKey;

    @Value("${jwt.private.key}")
    RSAPrivateKey privateKey;

    @Autowired
    AuthService authService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   RememberMeServices rememberMeServices
                                                   ) throws Exception {
        http.authorizeHttpRequests(this.authorizeHttpRequestsCustomizer())
            .httpBasic(cfg -> cfg.authenticationEntryPoint(new AuthenticationEntryPointImpl()))
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
            .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .rememberMe(rememberMe -> rememberMe.rememberMeServices(rememberMeServices))
            .logout(logout -> logout
                .logoutRequestMatcher(request -> request.getRequestURI().equals("/api/logout"))
                .logoutSuccessUrl("/api/logout/success")
                .invalidateHttpSession(true)
            );
        if (!"prod".equals(systemProfile)) {
            http.cors().configurationSource(corsConfigurationSource()) //允许跨域访问方便开发测试
                .and().csrf().ignoringAntMatchers("/api/**");
        }
        return http.build();
    }

    @Bean
    public RememberMeServices rememberMeServices(UserDetailsService userDetailsService, JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        return new JWTRememberMeServices(authService, userDetailsService, jwtEncoder, jwtDecoder);
    }

    private Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> authorizeHttpRequestsCustomizer() {
        //从前往后找到匹配上的第一条规则，就不再继续匹配
        return authorize -> authorize
            .antMatchers("/api/signup","/api/sys/openRegistry","/api/logout/success").permitAll()
            .anyRequest().authenticated();
    }

    @Bean
    public UserDetailsService users(AuthService authService) {
        return new UserDetailsServiceImpl(authService, systemProfile);
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(this.publicKey).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(this.publicKey).privateKey(this.privateKey).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    private CorsConfigurationSource corsConfigurationSource()
    {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
