package ch.vindthing.payload.request;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CommentAddRequest {
    @NotBlank
    @Size(max = 250)
    private String message;
    @NotBlank
    private String storeId;

    /**
     * Add Comment to Store
     * @param message message
     * @param storeId Store id
     */
    public CommentAddRequest(String message, String storeId) {
        this.message = message;
        this.storeId = storeId;
    }
}
