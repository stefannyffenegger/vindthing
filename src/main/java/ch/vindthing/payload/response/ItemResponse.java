package ch.vindthing.payload.response;

import ch.vindthing.payload.ItemPayload;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemResponse implements ItemPayload {
    private String id;
    private String name;
    private String description;
    private int quantity;
    private final String created;
    private String lastedit;

    public ItemResponse(String id, String name, String description, int quantity, String created, String lastedit) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.created = created;
        this.lastedit = lastedit;
    }
}
