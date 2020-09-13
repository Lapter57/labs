package ru.spbstu.shakhmin.handlers;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.spbstu.shakhmin.handlers.dto.ServerStatus;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class GatewayHandler {

    private final WebClient webClient;

    public GatewayHandler(@NotNull final WebClient webClient,
                          @Value("${app.serverUrl}") final String serverUrl) {
        this.webClient = webClient.mutate()
                .baseUrl(serverUrl)
                .build();
    }

    @NotNull
    public Mono<ServerResponse> getServerStatus(@NotNull final ServerRequest request) {
        return webClient.get()
                .uri("/api/status")
                .retrieve()
                .bodyToMono(ServerStatus.class)
                .flatMap(serverStatus -> ServerResponse
                        .ok()
                        .contentType(APPLICATION_JSON)
                        .bodyValue(serverStatus));

    }
}
