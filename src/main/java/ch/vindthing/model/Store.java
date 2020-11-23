package ch.vindthing.model;

import ch.vindthing.util.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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

    @DBRef
    private User owner;

    @DBRef
    private Set<User> sharedUsers = new HashSet<>();

    private String created;

    private String lastEdit;

    public Store() {
    }

    /**
     * @param name        Store name
     * @param description Store description
     * @param location    Store location
     */
    public Store(String name, String description, String location, User user) {
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

    public User getOwner() { return owner; }

    public void setOwner(User owner) { this.owner = owner; }

    public Set<User> getSharedUsers() {
        return sharedUsers;
    }

    public void setSharedUsers(Set<User> user) {
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
}
