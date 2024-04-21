package com.narvane.gateway.handler;

import com.narvane.gateway.entity.User;
import com.narvane.gateway.repository.UserRepository;
import com.narvane.gateway.util.JwtTokenGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static com.narvane.gateway.model.exception.InvalidUserException.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RegisterHandler {

    @AllArgsConstructor
    @Data static class RegisterRequest { private String login; private String password; }

    @AllArgsConstructor
    @Data static class RegisterResponse { private String token; }

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenGenerator jwtTokenGenerator;

    @Bean
    public RouterFunction<ServerResponse> register() {
        return route(POST("/register").and(accept(MediaType.APPLICATION_JSON)), this::handleRegister);
    }

    private Mono<ServerResponse> handleRegister(ServerRequest request) {
        Mono<RegisterRequest> tokenRequest = request.bodyToMono(RegisterRequest.class);

        return tokenRequest.flatMap(credentials -> {

            var userSearch = userRepository.findByLogin(
                    credentials.getLogin()
            );

            return userSearch.flatMap(userSearched -> Mono.error(new UserAlreadyExistException()))
                    .switchIfEmpty(Mono.defer(() -> {
                        var newUser = new User();
                        newUser.setLogin(credentials.getLogin());
                        newUser.setPassword(passwordEncoder.encode(credentials.getPassword()));

                        return userRepository.save(newUser).flatMap(savedUser -> {
                            var token = jwtTokenGenerator.generate(savedUser.getLogin());

                            return ServerResponse
                                    .status(HttpStatus.CREATED)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(new RegisterResponse(token));
                        });
                    }
            )).cast(ServerResponse.class);
        });
    }
}
