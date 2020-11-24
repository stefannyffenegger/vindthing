package ch.vindthing.payload.request;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemAddRequest {
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
}

