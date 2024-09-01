package dfomenko.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Locale;

import static dfomenko.controller.LoginDataController.ADMIN_ROLE_NAME;
import static dfomenko.controller.LoginDataController.CLIENT_ROLE_NAME;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Value("${app.web-security-debug-mode:false}")
    boolean webSecurityDebugMode;

    @Autowired
    private MessageSource messageSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(session -> session
                        .invalidSessionUrl("/")
                        .sessionAuthenticationErrorUrl("/"))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
//                        .requestMatchers("/**").permitAll()
                        .requestMatchers("/create**",
                                         "/update**",
                                         "/delete**",
                                         "/bill**",
                                         "/films**",
                                         "/additions**",
                                         "/placeKinds**",
                                         "/placements**",
                                         "/places**",
                                         "/*statistic*").hasRole(ADMIN_ROLE_NAME)
                        .requestMatchers("/buy**",
                                         "/client**",
                                         "/select**").hasRole(CLIENT_ROLE_NAME)
                        .requestMatchers("/",
                                         "/login**",
                                         "/logout**",
                                         "/signup**",
                                         "/more_info**",
                                         "/recover**",
                                         "/send**",
                                         "/cinemafiles/**",
                                         "/error**",
                                         "/download**",
                                         "/ticket_info/**",
                                         "**/fonts**").permitAll()
                        .anyRequest().denyAll())
                .formLogin(formLogin -> formLogin
                        .loginPage("/login_user_form"))
                .logout((logout) -> logout.logoutUrl("/logout_user")
                        .logoutSuccessUrl("/"))
                .csrf((csrf) -> csrf
                        .ignoringRequestMatchers("/delete**"))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedPage("/login_user_form?error="
                                                  + messageSource.getMessage("errorMessageYouHaveNotPermissions",
                                                                             null, Locale.getDefault()))
                );


        return http.build();
    }


    // !!! Disable Spring security internal users authentication !!!
    @Bean
    public UserDetailsService emptyDetailsService() {
        return username -> {
            throw new UsernameNotFoundException("Users authentication by Spring security is not used.");
        };
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.debug(webSecurityDebugMode);
    }

}