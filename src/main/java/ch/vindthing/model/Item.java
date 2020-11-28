package ch.vindthing.model;

import ch.vindthing.util.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Size;

@Document
public class Item {
    @Id
    private String id;

    @Size(max = 20)
    private String name;

    @Size(max = 250)
    private String description;

    private int quantity;

    private String imageId;

    private String created;

    private String lastedit;

    private boolean inStore;

    private int useCount;

    public Item() {
    }

    /**
     * @param name        Item name
     * @param description Item description
     * @param quantity    Item quantity
     */
    public Item(String name, String description, int quantity) {
        this.id = new ObjectId().toString();
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.created = StringUtils.getCurrentTimeStamp();
        this.lastedit = StringUtils.getCurrentTimeStamp();
        this.inStore = true;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
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

    public boolean isInStore() {
        return inStore;
    }

    /**
     * Toggles inStore and counts up useCount if toggle to false
     */
    public void toggleInStore() {
        inStore = !inStore; // toggle
        if(!inStore){
            useCount++;
        }
    }

    public int getUseCount() {
        return useCount;
    }
}
