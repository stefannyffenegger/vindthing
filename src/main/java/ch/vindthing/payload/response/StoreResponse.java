package ch.vindthing.payload.response;

import ch.vindthing.payload.StorePayload;

public class StoreResponse implements StorePayload {
    private String id;
    private String name;
    private String description;
    private String location;
    private final String created;
    private String lastedit;

    public StoreResponse(String id, String name, String description, String location, String created, String lastedit) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.created = created;
        this.lastedit = lastedit;
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

    public String getCreated() {
        return created;
    }

    public String getLastedit() {
        return lastedit;
    }

    public void setLastedit(String lastedit) {
        this.lastedit = lastedit;
    }
}
