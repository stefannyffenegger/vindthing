package ch.vindthing.payload.request;

import javax.validation.constraints.NotBlank;

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

    public String getId() { return id; }

    public void setId(String id) {this.id = id; }

    public String getStoreId() {
        return this.storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}

