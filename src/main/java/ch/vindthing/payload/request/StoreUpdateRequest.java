package ch.vindthing.payload.request;

import ch.vindthing.payload.StorePayload;

import javax.validation.constraints.NotBlank;
import java.util.Set;

public class StoreUpdateRequest implements StorePayload {
    @NotBlank
    private String id;
    private String name;
    private String description;
    private String location;

    /**
     * Request to update or delete an existing Store
     * @param id Required
     * @param name Optional
     * @param description Optional
     * @param location Optional
     */
    public StoreUpdateRequest(String id, String name, String description, String location) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
    }

    public String getId() { return id; }

    public void setId(String id) {this.id = id; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) { this.location = location; }
}
