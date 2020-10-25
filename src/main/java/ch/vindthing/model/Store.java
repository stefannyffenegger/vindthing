package ch.vindthing.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * Stores contain Items
 */
@Document(collection = "stores")
public class Store {
  @Id
  private String id;

  @NotBlank
  @Size(max = 20)
  private String name;

  @Size(max = 250)
  private String description;

  private String location;

  @DBRef
  private Set<Item> items = new HashSet<>();

  @DBRef
  private Set<User> users = new HashSet<>();

  public Store() {
  }

  /**
   *
   * @param name Store name
   * @param description Store description
   * @param location Store location
   */
  public Store(String name, String description, String location) {
    this.name = name;
    this.description = description;
    this.location = location;
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

  public String getLocation() { return location; }

  public void setLocation(String quantity) { this.location = quantity; }

  public Set<Item> getItems() {
    return items;
  }

  public void setItems(Set<Item> item) {
    this.items = item;
  }

  public Set<User> getUsers() {
    return users;
  }

  public void setUsers(Set<User> user) {
    this.users = users;
  }
}
