package example.security.config;

import example.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

/**
 * Конфигурационный файл для Spring Security
 */
// сообщаем спрингу что нужно включить web security
@EnableWebSecurity // в эту аннотацию уже входит @Configuration поэтому отдельно её не указываем
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService; //автовайрим наш сервис

    /**
     * Говорим спрингу что нам нужен такой объукт в контексте и выбираем его реализацию
     * он нам понадобится для шифрования паролей
     *
     * @return выбранная реалиция PasswordEncoder
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Так же говорим спрингу чтоб поместил в контекст какую то дефолнтую реализацию AuthenticationManager
     * он нам понадобится чтоб проверять аутентификацю пользователя в контроллерах
     *
     * @return какое то дефолтное значени из спринга
     * @throws Exception
     */
    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    /**
     * переопределяем метод из WebSecurityConfigurerAdapter
     * и настраиваем в нем HttpSecurity для нашего приложения
     *
     * @param http - объект спринга который нам нужно настроить
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable() //отключаем базовую http авторизацию
                .csrf().disable() //отключаем csrf
                // разрешаем Cross Origin запросы в защищенную область
                .cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER) //отключаем механизм сессий
                .and()
                .authorizeRequests() //даллее указываем какие запросы и как авторизовать
                .antMatchers("/login", "/register").permitAll() // разрешаем доступ к этим эндпоинтам для всех
                .anyRequest().authenticated() // запрещаем неавторизованный доступ ко всем остальным
                .and() // добавляем наш фильтр в цепочку фильтров спринга и указываем класс аутентификации
                .addFilterBefore(new JwtFilter(userDetailsService), UsernamePasswordAuthenticationFilter.class);
    }
}
