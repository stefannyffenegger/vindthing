package ch.vindthing.model;

import ch.vindthing.util.StringUtils;
import org.apache.coyote.http11.filters.SavedRequestInputFilter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
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

    @DBRef
    private Set<Item> items = new HashSet<>();

    @DBRef
    private Set<Store> stores = new HashSet<>();

    @DBRef
    private Set<User> users = new HashSet<>();

    private String created;

    private String lastedit;

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
        this.users.add(user);
        this.created = StringUtils.getCurrentTimeStamp();
        this.lastedit = StringUtils.getCurrentTimeStamp();
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

    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> item) {
        this.items = item;
    }

    public Set<Store> getStores() {
        return stores;
    }

    public void setStores(Set<Store> store) {
        this.stores = store;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> user) {
        this.users = user;
    }

    public String getCreated() {
        return created;
    }

    public String getLastedit() {
        return lastedit;
    }

    public void setLastedit(String lastedit) {
        this.lastedit = lastedit;
    }
}
