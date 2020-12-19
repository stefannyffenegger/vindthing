package ch.vindthing.config;

import ch.vindthing.util.UserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketMessageBrokerConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Defines the subscriber endpoints
     * @param config config
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue", "/store", "/item", "/comment"); //Subscriber endpoint
        //config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/client"); //Prefix
    }

    /**
     * STOMP endpoint definition
     * @param registry Stomp endpoint registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/broadcast");
        registry.addEndpoint("/broadcast").withSockJS().setHeartbeatTime(60_000); //60sec heartbeat interval
        registry.addEndpoint("/chat").setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new UserInterceptor()); //Register interceptor
    }
}
