package ch.vindthing.payload.request;

import ch.vindthing.payload.ItemPayload;

import javax.validation.constraints.NotBlank;

public class ItemUpdateRequest implements ItemPayload {
    @NotBlank
    private String id;
    private String name;
    private String description;
    private int quantity;
    private String storeId; // Store to which item moves

    /**
     * Request to update or delete an existing Item, or move an Item to another Store
     * @param id Required
     * @param name Optional
     * @param description Optional
     * @param quantity Optional
     */
    public ItemUpdateRequest(String id, String name, String description, int quantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
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

    public String getStoreId() {
        return this.storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}

