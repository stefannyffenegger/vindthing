package ch.vindthing.payload.request;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class UserUpdateRequest {
    private String owner;
    private ArrayList<String> sharedUsers;
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
