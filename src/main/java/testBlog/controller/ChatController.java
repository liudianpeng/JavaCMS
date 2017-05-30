package testBlog.controller;

import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;

import javax.websocket.OnOpen;

@Controller
@RequestMapping("/chat-page")
public class ChatController {



    @GetMapping("")
    public String getMainChatPage(Model model) {
        model.addAttribute("view","chat/main");
        return "base-layout";
    }
    @MessageMapping("/message")
    @SendTo("/topic/replay")
    public String messageFromClient(String message, MessageHeaders messageHeaders,@Headers Map headersMap) throws Exception {
        System.out.println(messageHeaders);
        System.out.println(headersMap);
        for (int i = 0; i < headersMap.values().toArray().length; i++) {
            System.out.println(headersMap.values().toArray()[i]);
        }
        return message;
    }

    @MessageExceptionHandler
    @SendTo("/topic/error")
    public String handleException(Throwable exception) {
        System.out.println(exception);
        System.out.println(exception.getMessage());
        return exception.getMessage();
    }
}
