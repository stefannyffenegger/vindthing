package ch.vindthing.payload.request;

import ch.vindthing.payload.StorePayload;

import javax.validation.constraints.NotBlank;

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
