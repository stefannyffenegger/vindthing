package ch.vindthing.payload.request;

import javax.validation.constraints.*;

public class ItemRequest {
    @NotBlank
    private String name;
    private String description;
    private int quantity;

    public ItemRequest(String name, String description, int quantity) {
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
}
