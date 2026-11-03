package kun.cn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration @EnableWebSecurity
public class SecurityConfig {
  @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
  @Bean UserDetailsService users(PasswordEncoder encoder) {
    return new InMemoryUserDetailsManager(
      User.withUsername("user").password(encoder.encode("user123")).roles("USER").build(),
      User.withUsername("admin").password(encoder.encode("admin123")).roles("ADMIN").build());
  }
  @Bean SecurityFilterChain security(HttpSecurity http) throws Exception {
    return http.csrf(csrf -> csrf.disable()).cors(cors -> {}).authorizeHttpRequests(auth -> auth
      .requestMatchers("/api/catalog/**").permitAll().requestMatchers("/api/admin/**").hasRole("ADMIN")
      .requestMatchers("/api/orders/**", "/api/addresses/**").hasAnyRole("USER", "ADMIN").anyRequest().authenticated())
      .httpBasic(basic -> {}).build();
  }
}
