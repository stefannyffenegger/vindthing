package ch.vindthing.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAddRequest {
    private String owner;
    private String sharedUser;
    private String storeId;

    /**
     * Add User to Store
     * @param owner new Owner (e-mail)
     * @param sharedUser new regular User (e-mail)
     * @param storeId Store id
     */
    public UserAddRequest(String owner, String sharedUser, String storeId) {
        this.owner = owner;
        this.sharedUser = sharedUser;
        this.storeId = storeId;
    }
}
