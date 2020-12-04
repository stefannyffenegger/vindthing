package ch.vindthing.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemoveResponse {
    private String id;
    private String storeId;

    public RemoveResponse(String id, String storeId) {
        this.id = id;
        this.storeId = storeId;
    }
}