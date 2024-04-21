package com.narvane.gateway.handler;

import com.narvane.gateway.repository.UserRepository;
import com.narvane.gateway.util.JwtTokenGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static com.narvane.gateway.model.exception.InvalidUserException.LoginNotFoundException;
import static com.narvane.gateway.model.exception.InvalidUserException.WrongPasswordException;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class AuthHandler {

    @AllArgsConstructor
    @Data static class TokenRequest { private String login; private String password; }

    @AllArgsConstructor
    @Data static class TokenResponse { private String token; }

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenGenerator jwtTokenGenerator;

    @Bean
    public RouterFunction<ServerResponse> login() {
        return route(POST("/login").and(accept(MediaType.APPLICATION_JSON)), this::handleLogin);
    }

    private Mono<ServerResponse> handleLogin(ServerRequest request) {
        Mono<TokenRequest> tokenRequest = request.bodyToMono(TokenRequest.class);

        return tokenRequest.flatMap(credentials -> {

            var user = userRepository.findByLogin(
                    credentials.getLogin()
            );

            return user.flatMap(existentUser -> {
                if (passwordEncoder.matches(credentials.getPassword(), existentUser.getPassword())) {
                    var token = jwtTokenGenerator.generate(existentUser.getLogin());

                    return ServerResponse
                            .ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new TokenResponse(token));
                } else {
                    return Mono.error(new WrongPasswordException());
                }
            }).switchIfEmpty(Mono.error(new LoginNotFoundException()));
        });
    }

}
