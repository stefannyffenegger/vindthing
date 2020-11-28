package ch.vindthing.payload.request;

import ch.vindthing.model.Comment;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CommentRemoveRequest {
    @NotBlank
    private String id;

    public CommentRemoveRequest(){}
    /**
     * Remove Comment from Store
     * @param id comment id
     */
    public CommentRemoveRequest(String id) {
        this.id = id;
    }
}
