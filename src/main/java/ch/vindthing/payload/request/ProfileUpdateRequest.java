package ch.vindthing.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateRequest {
    private String email;
    private String name;
    private String password;

    /**
     * Request to update User profile
     * @param email Unique e-mail address / Username
     * @param name Name of User
     * @param password new password
     */
    public ProfileUpdateRequest(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }
}
