package ch.vindthing.payload.response;

import ch.vindthing.model.Item;
import ch.vindthing.model.User;

import java.util.ArrayList;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreResponse {
    private String id;
    private String name;
    private String description;
    private String location;
    private final String created;
    private String lastedit;
    private String imageId;
    private String owner;
    private ArrayList<String> sharedUsers;
    private ArrayList<Item> items;

    public StoreResponse(String id, String name, String description, String location, String created, String lastedit,
                         String imageId, String owner, ArrayList<String> sharedUsers, ArrayList<Item> items) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.created = created;
        this.lastedit = lastedit;
        this.owner = owner;
        this.sharedUsers = sharedUsers;
        this.items = items;
    }
}
