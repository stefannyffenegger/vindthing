package ch.vindthing.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.Size;

@Document(collection = "items")
public class Item {
  @Id
  private String id;

  @Size(max = 20)
  private String name;

  @Size(max = 250)
  private String description;

  private int quantity;

  private String imageUrl;

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

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
}
