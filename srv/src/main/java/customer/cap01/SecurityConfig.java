package customer.cap01;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("cloud")
@Order(1)
public class SecurityConfig {
    @Autowired
    private JwtDecoder jwtDecoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/**")
            // CAP アプリでは Authorization ヘッダの JWT で認証するため CSRF トークンチェックは不要。ユーザからのアクセスは App Router で CSRF トークンチェックが行われる。
            .csrf(c -> c.disable())
            .authorizeHttpRequests(authorize -> authorize
            .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt
            .decoder(jwtDecoder)));
        return http.build();
    }
}
