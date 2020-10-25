package ch.vindthing.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "items")
public class Item {
  @Id
  private String id;

  @NotBlank
  @Size(max = 20)
  private String name;

  @Size(max = 250)
  private String description;

  private int quantity;

  @DBRef
  private Set<User> users = new HashSet<>();

  public Item() {
  }

  /**
   *
   * @param name Item name
   * @param description Item description
   * @param quantity Item quantity
   */
  public Item(String name, String description, int quantity) {
    this.name = name;
    this.description = description;
    this.quantity = quantity;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getQuantity() { return quantity; }

  public void setQuantity(int quantity) { this.quantity = quantity; }

  public Set<User> getUsers() {
    return users;
  }

  public void setUsers(Set<User> user) {
    this.users = users;
  }
}
