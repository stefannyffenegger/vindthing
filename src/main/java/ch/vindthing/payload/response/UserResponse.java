package ch.vindthing.payload.response;

import java.util.List;

public class UserResponse {
    private String name;
    private String email;
    private List<String> roles;

    public UserResponse(String name, String email, List<String> roles) {
        this.name = name;
        this.email = email;
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRoles() {
        return roles;
    }
}
