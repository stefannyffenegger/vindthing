package ch.vindthing.payload.response;

import ch.vindthing.model.Comment;
import ch.vindthing.model.Item;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class StoreResponse {
    private final String created;
    private String id;
    private String name;
    private String description;
    private String location;
    private String lastedit;
    private String imageId;
    private String owner;
    private ArrayList<String> sharedUsers;
    private ArrayList<Item> items;
    private ArrayList<Comment> comments;

    public StoreResponse(String id, String name, String description, String location, String created, String lastedit,
                         String imageId, String owner, ArrayList<String> sharedUsers, ArrayList<Item> items,
                         ArrayList<Comment> comments) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.created = created;
        this.lastedit = lastedit;
        this.owner = owner;
        this.sharedUsers = sharedUsers;
        this.items = items;
        this.comments = comments;
    }
}
