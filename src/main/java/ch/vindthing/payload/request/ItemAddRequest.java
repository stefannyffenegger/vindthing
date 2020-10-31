package ch.vindthing.payload.request;

import ch.vindthing.payload.ItemPayload;

import javax.validation.constraints.NotBlank;

public class ItemAddRequest implements ItemPayload {
    @NotBlank
    private String name;
    private String description;
    private int quantity;
    @NotBlank
    private String storeId; // Store to which item initially belongs to

    /**
     * Request to add a new Item
     * @param name Required
     * @param description Optional
     * @param quantity Optional
     */
    public ItemAddRequest(String name, String description, int quantity) {
        this.name = name;
        this.description = description;
        this.quantity = quantity;
    }

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

