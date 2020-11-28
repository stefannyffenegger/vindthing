package ch.vindthing.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemResponse {
    private String id;
    private String name;
    private String description;
    private int quantity;
    private String imageId;
    private final String created;
    private String lastedit;
    private boolean inStore;
    private int useCount;

    public ItemResponse(String id, String name, String description, int quantity, String created, String lastedit,
                        String imageId, boolean inStore, int useCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.imageId = imageId;
        this.created = created;
        this.lastedit = lastedit;
        this.inStore = inStore;
        this.useCount = useCount;
    }
}
