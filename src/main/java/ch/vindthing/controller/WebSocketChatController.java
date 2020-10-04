package ch.vindthing.controller;

import ch.vindthing.util.ChatMessage;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Handles all messages
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
//@RequestMapping("/ws")
public class WebSocketChatController implements ActiveUserChangeListener {

    // private final static Logger LOGGER = LoggerFactory.getLogger(WebSocketChatController.class);

    @Autowired
    private SimpMessagingTemplate webSocket;

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

    @GetMapping("/sockjs-message")
    public String getWebSocketWithSockJs() {return "sockjs-message";}

    @MessageMapping("/chat")
    public void send(SimpMessageHeaderAccessor sha, @Payload ChatMessage chatMessage) throws Exception {
        System.out.println("hi, im in chat hoho");
        String sender = sha.getUser().getName();
        ChatMessage message = new ChatMessage(chatMessage.getFrom(), chatMessage.getText(), chatMessage.getRecipient());
        if (!sender.equals(chatMessage.getRecipient())) {
            webSocket.convertAndSendToUser(sender, "/queue/messages", message);
        }

        webSocket.convertAndSendToUser(chatMessage.getRecipient(), "/queue/messages", message);
    }

    @Override
    public void notifyActiveUserChange() {
        Set<String> activeUsers = activeUserManager.getAll();
        webSocket.convertAndSend("/topic/active", activeUsers);
    }
}