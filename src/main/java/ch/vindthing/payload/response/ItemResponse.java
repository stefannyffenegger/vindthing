package ch.vindthing.payload.response;

import ch.vindthing.payload.ItemPayload;
import ch.vindthing.util.StringUtils;

public class ItemResponse implements ItemPayload {
    private String id;
    private String name;
    private String description;
    private int quantity;
    private final String created;
    private String lastedit;

    public ItemResponse(String id, String name, String description, int quantity, String created, String lastedit) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.created = created;
        this.lastedit = lastedit;
    }

    public String getId() { return id; }

    public void setId(String id) {this.id = id; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) { this.quantity = quantity; }

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
