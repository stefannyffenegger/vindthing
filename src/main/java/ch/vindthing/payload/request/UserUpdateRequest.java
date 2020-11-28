package ch.vindthing.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;

@Getter
@Setter
public class UserUpdateRequest {
    @Size(max = 50)
    @Email
    private String owner;
    private ArrayList<@Size(max = 50) @Email String> sharedUsers;
    @NotBlank
    private String storeId;

    /**
     * Add User to Store
     * @param owner new Owner (e-mail)
     * @param sharedUser new regular User (e-mail)
     * @param storeId Store id
     */
    public UserUpdateRequest(String owner, ArrayList<String> sharedUser, String storeId) {
        this.owner = owner;
        this.sharedUsers = sharedUser;
        this.storeId = storeId;
    }
}
