package example.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Objects;

/**
 * Наш фильтр который мы встроим в цепочку секьюрити фильтров и укажем спрингу кого к нам можно пускать
 */
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    // пароль для шифрования jwt токена, должен быть тот же самый что в контроллере
    // лучше его кончно вынести куда то в конфиг и читать в обоих метсах оттуда
    private final String secretWord = "secretWord";
    private final UserDetailsService userDetailsService; //автовайрим наш сервис

    /**
     * Здесь нам нужно проверить наш токен и дать или не дать аутентификацию этому запросу
     *
     * @param request     - это объект хттп запроса из которого мы можем получить нужные данные
     * @param response    - это объект хттп ответа, он нам тут не особо нужен
     * @param filterChain - это объект цепочки фильтров, он нужен чтоб передать управление следующим фильтрам
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // приводим запрос к его хттп реализации
        HttpServletRequest req = (HttpServletRequest) request;
        // получаем токен из хттп заголовка "Authorization"
        String token = req.getHeader("Authorization");
        if (Objects.nonNull(token) && !token.isEmpty()) { // если токен присутствует
            //парсим из него тело, если время токена истекло тут будет исключение которое можно обработать
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretWord).parseClaimsJws(token);
            // получаем из тела токена зашитый туда username
            String username = claims.getBody().getSubject();
            // UserDetails это специальный класс спринга который нужен для создания аутентификации
            // его получаем из соответсвующего сервиса, где он и валидируется
            UserDetails user = userDetailsService.loadUserByUsername(username);
            //создаем аутентификацию для передачи в секьюрити контекст
            // здесь же проверяется имя и пароль, котроые есть в UserDetails
            Authentication auth = new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
            // записываем аутентификацию в секьюрити контекст, теперь спринг знает что этот юзер аутентифицирован
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        // передаем управление следующим по цепочке фильтрам
        filterChain.doFilter(request, response);
    }
}
