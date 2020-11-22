package ch.vindthing.payload.request;

import ch.vindthing.payload.ItemPayload;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemUpdateRequest implements ItemPayload {
    @NotBlank
    private String id;
    private String name;
    private String description;
    private int quantity;

    /**
     * Request to update or delete an existing Item
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
}

