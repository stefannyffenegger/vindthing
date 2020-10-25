package ch.vindthing.payload.request;

import javax.validation.constraints.NotBlank;
import java.util.Set;

public class StoreRequest {
    @NotBlank
    private String name;
    private String description;
    private String location;
    private Set<String> items;

    public StoreRequest(String name, String description, String location) {
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

    public Set<String> getItems() {
        return this.items;
    }

    public void setItem(Set<String> items) { this.items = items; }
}
