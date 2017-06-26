package testBlog.config.communication;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.lang.ArrayIndexOutOfBoundsException;
import java.lang.*;


public class WebSocketHandler implements org.springframework.web.socket.WebSocketHandler{

    private Map<String,WebSocketSession> socketsBysocketId = new HashMap<String,WebSocketSession>();
    private Map<String,ArrayList<WebSocketMessage<?>>> messagesBysocketId = new HashMap<String,ArrayList<WebSocketMessage<?>>>();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        socketsBysocketId.put(session.getId(),session);
        messagesBysocketId.put(session.getId(),new ArrayList<WebSocketMessage<?>>(2000));
        System.out.println("WebSocket session with id '" + session.getId() + "' established!");
        System.out.println(socketsBysocketId.size());
        messagesBysocketId.forEach((socketId,arrayOfmessages) -> {
            if (socketId != session.getId()) {
                for (int i=0;i<arrayOfmessages.size();i++){
                    try {
                        session.sendMessage(arrayOfmessages.get(i));
                    } catch (IOException exept) {
                        System.out.println(exept);
                    }
                }
                System.out.println(socketId);
            }
        });
    }
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        //String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("WebSocket session with id '" + session.getId() +
                           "' sent message:\n" + /*name +*/ " " + message + "\n");
        messagesBysocketId.forEach((key,value)->{
            if(key==session.getId()){
                value.add(message);
            }
        });
        socketsBysocketId.forEach((webSocket_session_id,webSocketSession) -> {
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
        System.out.println("msg " + messagesBysocketId.size());
        messagesBysocketId.remove(session.getId());
        System.out.println(
                "WebSocket session with id '" + session.getId() +
                "' closed with close status '" + closeStatus.getReason() +
                "' and close code '" + closeStatus.getCode() + "'!");
        socketsBysocketId.remove(session.getId());
        messagesBysocketId.remove(session.getId());
        System.out.println(socketsBysocketId.size());
        System.out.println("msg " + messagesBysocketId.size());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
