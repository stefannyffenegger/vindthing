package ch.vindthing.model;

import ch.vindthing.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Size;

/**
 * Comment Object
 */
@Getter
@Setter
@Document
public class Comment {
    @Id
    private String id;

    @Size(max = 50)
    private String user;

    private String created;

    @Size(max = 250)
    private String comment;

    public Comment(String user, String comment) {
        this.id = new ObjectId().toString();
        this.user = user;
        this.comment = comment;
        this.created = StringUtils.getCurrentTimeStamp();
    }
}
