package ch.vindthing.payload.request;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemMoveRequest{
    @NotBlank
    private String id;
    @NotBlank
    private String storeId; // Store to which item moves

    /**
     * Request to move an existing Item to another Store
     * @param id Required
     * @param storeId Required
     */
    public ItemMoveRequest(String id, String storeId) {
        this.id = id;
        this.storeId = storeId;
    }
}

