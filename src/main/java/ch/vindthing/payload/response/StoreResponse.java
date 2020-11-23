package ch.vindthing.payload.response;

import ch.vindthing.model.Item;
import ch.vindthing.model.User;
import ch.vindthing.payload.StorePayload;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreResponse implements StorePayload {
    private String id;
    private String name;
    private String description;
    private String location;
    private Item items;
    private final String created;
    private String lastedit;
    private String owner;
    private Set<User> sharedUsers;

    public StoreResponse(String id, String name, String description, String location, String created, String lastedit,
                         String owner, Set<User> sharedUsers) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.created = created;
        this.lastedit = lastedit;
        this.owner = owner;
        this.sharedUsers = sharedUsers;
    }
}
