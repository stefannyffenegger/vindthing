package ch.vindthing.payload.response;

import java.io.Serializable;

public class ImageResponse implements Serializable {
    private String imageId;

    public ImageResponse(String imageId) {
        this.imageId = imageId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}