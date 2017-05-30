package testBlog.config.communication;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.HashMap;

public class WebSocketHandler implements org.springframework.web.socket.WebSocketHandler{

    private Map<String,WebSocketSession> socketsByUserName = new HashMap<String,WebSocketSession>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        socketsByUserName.put(session.getId(),session);
        System.out.println("WebSocket session with id '" + session.getId() + "' established!");
        System.out.println(socketsByUserName.size());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        System.out.println("WebSocket session with id '" + session.getId() +
                           "' sent message:\n" + message + "\n");
        socketsByUserName.forEach((webSocket_session_id,webSocketSession) -> {
            if(webSocket_session_id != session.getId()) {
                try {
                    webSocketSession.sendMessage(message);
                } catch (IOException exept) {
                    System.out.println(exept);
                }
            }
        } );
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("WebSocket session with id '" + session.getId() +
                           "' has exception '" + exception.getMessage() +
                            "' and cause '" + exception.getCause() + "'!");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        System.out.println(
                "WebSocket session with id '" + session.getId() +
                "' closed with close status '" + closeStatus.getReason() +
                "' and close code '" + closeStatus.getCode() + "'!");
        socketsByUserName.remove(session.getId());
        System.out.println(socketsByUserName.size());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
