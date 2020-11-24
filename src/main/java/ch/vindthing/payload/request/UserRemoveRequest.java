package ch.vindthing.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRemoveRequest {
    private String sharedUser;
    private String storeId;

    /**
     * Add User to Store
     * @param sharedUser new regular User (e-mail)
     * @param storeId Store id
     */
    public UserRemoveRequest(String sharedUser, String storeId) {
        this.sharedUser = sharedUser;
        this.storeId = storeId;
    }
}
