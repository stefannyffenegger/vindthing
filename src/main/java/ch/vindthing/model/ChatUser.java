package ch.vindthing.model;

import java.security.Principal;

public class ChatUser implements Principal {

    String name;

    public ChatUser(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}