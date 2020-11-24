package ch.vindthing.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "confirmationToken")
public class ConfirmationToken {
  @Id
  private String id;

  private String confirmationToken;

  private Date createdDate;

  private User user;

  public ConfirmationToken() {

  }

  public ConfirmationToken(User user) {
    this.user = user;
    createdDate = new Date();
    confirmationToken = UUID.randomUUID().toString();
  }

}

