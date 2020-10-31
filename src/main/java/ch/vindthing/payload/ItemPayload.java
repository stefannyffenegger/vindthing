package ch.vindthing.payload;

public interface ItemPayload {

    String getDescription();

    void setDescription(String description);

    String getName();

    void setName(String name);

    int getQuantity();

    void setQuantity(int quantity);
}
