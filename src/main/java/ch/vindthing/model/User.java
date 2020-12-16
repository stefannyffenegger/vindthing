package ch.vindthing.model;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {
  @Id
  private String id;

  @NotBlank
  @Size(max = 50)
  private String name;

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  private String imageId;

  @NotBlank
  @Size(max = 64)
  private String password;

  @DBRef
  private Set<Role> roles = new HashSet<>();

  private boolean isEnabled;

  public User() {
  }

  public User(String name, String email, String password) {
    this.name = name;
    this.email = email;
    this.password = password;
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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }

  public boolean isEnabled() {
    return isEnabled;
  }

  public void setEnabled(boolean enabled) {
    isEnabled = enabled;
  }

  public String getImageId() { return imageId; }

  public void setImageId(String imageId) { this.imageId = imageId; }

  @Override
  public String toString(){
    return email;
  }
}
