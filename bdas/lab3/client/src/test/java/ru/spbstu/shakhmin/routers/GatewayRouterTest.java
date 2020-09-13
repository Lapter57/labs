package ru.spbstu.shakhmin.routers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.spbstu.shakhmin.handlers.GatewayHandler;
import ru.spbstu.shakhmin.handlers.dto.ServerStatus;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebFluxTest
@ContextConfiguration(classes = {GatewayRouter.class})
class GatewayRouterTest {

    private static final ServerStatus serverStatus = new ServerStatus("ONLINE");

    @MockBean
    private GatewayHandler gatewayHandler;

    @Autowired
    private RouterFunction<ServerResponse> serverStatusRoute;

    @Test
    public void testServerStatusRoute() {
        Mockito.when(gatewayHandler.getServerStatus(Mockito.any(ServerRequest.class)))
                .thenReturn(ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .bodyValue(serverStatus));
        WebTestClient
                .bindToRouterFunction(serverStatusRoute)
                .build()
                .get().uri("/server-status")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ServerStatus.class).isEqualTo(serverStatus);
    }
}