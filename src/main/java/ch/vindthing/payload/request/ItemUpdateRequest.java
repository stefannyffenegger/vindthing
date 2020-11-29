package ch.vindthing.payload.request;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemUpdateRequest {
    @NotBlank
    private String id;
    private String name;
    private String description;
    private int quantity;
    //@NotBlank
    private boolean inStore;

    /**
     * Request to update or delete an existing Item
     * @param id Required
     * @param name Optional
     * @param description Optional
     * @param quantity Optional
     */
    public ItemUpdateRequest(String id, String name, String description, int quantity, boolean inStore) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.inStore = inStore;
    }
}

