package ch.vindthing.model;

import ch.vindthing.util.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;

/**
 * Stores contain Items
 */
@Document(collection = "stores")
public class Store {
    @Id
    private String id;

    @NotBlank
    @Size(max = 20)
    private String name;

    @Size(max = 250)
    private String description;

    private String location;

    private ArrayList<Item> items = new ArrayList<>();

    @NotBlank
    private String owner;

    private ArrayList<String> sharedUsers = new ArrayList<>();

    private String created;

    private String lastEdit;

    private String imageId;

    private ArrayList<Comment> comments = new ArrayList<>();

    public Store() {
    }

    /**
     * @param name        Store name
     * @param description Store description
     * @param location    Store location
     */
    public Store(String name, String description, String location, String user) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.owner = user; // Add as owner of Store
        this.sharedUsers.add(user); // Add as regular user too
        this.created = StringUtils.getCurrentTimeStamp();
        this.lastEdit = StringUtils.getCurrentTimeStamp();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String quantity) {
        this.location = quantity;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> item) {
        this.items = item;
    }

    public String getOwner() { return owner; }

    public void setOwner(String owner) { this.owner = owner; }

    public ArrayList<String> getSharedUsers() {
        return sharedUsers;
    }

    public void setSharedUsers(ArrayList<String> user) {
        this.sharedUsers = user;
    }

    public String getCreated() {
        return created;
    }

    public String getLastEdit() {
        return lastEdit;
    }

    public void setLastEdit(String lastEdit) {
        this.lastEdit = lastEdit;
    }

    public String getImageId() { return imageId; }

    public void setImageId(String imageId) { this.imageId = imageId; }

    public ArrayList<Comment> getComments() { return comments; }

    public void setComments(ArrayList<Comment> comments) { this.comments = comments; }
}
