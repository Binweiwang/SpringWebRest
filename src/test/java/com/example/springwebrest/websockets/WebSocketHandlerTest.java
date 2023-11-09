package com.example.springwebrest.websockets;

import com.example.springwebrest.config.websockets.WebSocketHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class WebSocketHandlerTest {

    @Mock
    private WebSocketSession sessionMock;

    @Test
    void afterConnectionEstablished() throws Exception {
        WebSocketHandler handler = new WebSocketHandler("testEntity");
        handler.afterConnectionEstablished(sessionMock);

        verify(sessionMock, times(1)).sendMessage(any(TextMessage.class));
    }

    @Test
    void afterConnectionClosed() throws Exception {
        WebSocketHandler handler = new WebSocketHandler("testEntity");
        handler.afterConnectionClosed(sessionMock, CloseStatus.NORMAL);


    }

    @Test
    void sendMessage() throws Exception {
        WebSocketHandler handler = new WebSocketHandler("testEntity");

        // Simulamos que hay una sesión abierta


        // Enviamos un mensaje
        handler.sendMessage("Test message");

    }

    @Test
    void sendPeriodicMessages() throws Exception {
        WebSocketHandler handler = new WebSocketHandler("testEntity");

        // Simulamos que hay una sesión abierta

        handler.afterConnectionEstablished(sessionMock);

        // Llamamos al método de envío de mensajes periódicos
        handler.sendPeriodicMessages();

    }

    @Test
    void handleTransportError() throws Exception {
        WebSocketHandler handler = new WebSocketHandler("testEntity");

        // Simulamos un error de transporte
        Throwable exception = new RuntimeException("Test error");
        handler.handleTransportError(sessionMock, exception);

        // Verificamos que se registre el error correctamente
        // Puedes ajustar la verificación según lo que haga tu aplicación en esta situación
    }
}