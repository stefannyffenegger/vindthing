package ch.vindthing.payload.request;

import javax.validation.constraints.*;

public class ItemRequest {
    @NotBlank
    private String name;
    private String description;
    private int quantity;
    @NotBlank
    private String store; // Store to which item belongs to

    public ItemRequest(String name, String description, int quantity, String store) {
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.store = store;
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

    public String getStore() {
        return this.store;
    }

    public void setStore(String stores) {
        this.store = stores;
    }
}
