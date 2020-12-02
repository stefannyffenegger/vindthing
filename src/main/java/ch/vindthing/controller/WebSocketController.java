package ch.vindthing.controller;

import ch.vindthing.model.ChatMessage;

import java.util.Objects;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * Handles all messages
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
public class WebSocketController implements ActiveUserChangeListener {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ActiveUserManager activeUserManager;

    @PostConstruct
    private void init() {
        activeUserManager.registerListener(this);
    }

    @PreDestroy
    private void destroy() {
        activeUserManager.removeListener(this);
    }

    //@GetMapping("/webchat")
    //public String getWebSocketWithSockJs() {return "webchat";}

    @MessageMapping("/ws/chat")
    public void send(SimpMessageHeaderAccessor sha, @Payload ChatMessage chatMessage) throws Exception {
        String sender = Objects.requireNonNull(sha.getUser()).getName();
        ChatMessage message = new ChatMessage(chatMessage.getFrom(), chatMessage.getText(), chatMessage.getRecipient());

        //Needed to show own conversation in chat!
        if (!sender.equals(chatMessage.getRecipient())) {
            simpMessagingTemplate.convertAndSendToUser(sender, "/queue/messages", message);
        }

        simpMessagingTemplate.convertAndSendToUser(chatMessage.getRecipient(), "/queue/messages", message);
    }

    @Override
    public void notifyActiveUserChange() {
        Set<String> activeUsers = activeUserManager.getAll();
        simpMessagingTemplate.convertAndSend("/topic/active", activeUsers);
    }
}