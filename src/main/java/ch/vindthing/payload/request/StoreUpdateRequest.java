package ch.vindthing.payload.request;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreUpdateRequest {
    @NotBlank
    private String id;
    private String name;
    private String description;
    private String location;
    private String owner;
    private String sharedUser;

    /**
     * Request to update or delete an existing Store
     * @param id Required
     * @param name Optional
     * @param description Optional
     * @param location Optional
     */
    public StoreUpdateRequest(String id, String name, String description, String location, String owner, String sharedUser) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.owner = owner;
        this.sharedUser = sharedUser;
    }
}
