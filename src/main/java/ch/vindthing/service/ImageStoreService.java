package ch.vindthing.service;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class ImageStoreService {

    @Value("vindthing-store")
    private String gridDatabase = "vindthing-store";

    @Value("localhost")
    private String mongoHost;

    MongoClient mongoClient = new MongoClient(mongoHost);;
    MongoDatabase mongoDatabase = mongoClient.getDatabase(gridDatabase);

    public GridFSBucket getGridFSBucket(){
        return GridFSBuckets.create(mongoDatabase);
    }

    public String addImage(MultipartFile file) throws IOException {

        GridFSBucket bucket = getGridFSBucket();

        GridFSUploadOptions uploadOptions = new GridFSUploadOptions();
        uploadOptions.metadata(new Document("type", file.getContentType()));

        ObjectId fileId = bucket.uploadFromStream(file.getName(), file.getInputStream(),uploadOptions);

        return fileId.toString();
    }

    public GridFSDownloadStream getImage(String id) throws IllegalStateException, IOException {

        GridFSBucket bucket = getGridFSBucket();
        GridFSDownloadStream stream = bucket.openDownloadStream(new ObjectId(id));

        return stream;
    }
}
