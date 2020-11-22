package ch.vindthing.payload.response;

import ch.vindthing.payload.StorePayload;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreResponse implements StorePayload {
    private String id;
    private String name;
    private String description;
    private String location;
    private final String created;
    private String lastedit;
    private String owner;

    public StoreResponse(String id, String name, String description, String location, String created, String lastedit,
                         String owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.created = created;
        this.lastedit = lastedit;
        this.owner = owner;
    }
}
