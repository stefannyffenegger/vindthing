package ch.vindthing.payload.request;

import ch.vindthing.payload.StorePayload;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreAddRequest implements StorePayload {
    @NotBlank
    private String name;
    private String description;
    private String location;

    /**
     * Request to add a new Store
     * @param name Required
     * @param description Optional
     * @param location Optional
     */
    public StoreAddRequest(String name, String description, String location) {
        this.name = name;
        this.description = description;
        this.location = location;
    }
}
