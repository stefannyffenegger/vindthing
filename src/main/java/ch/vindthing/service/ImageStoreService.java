package ch.vindthing.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.gridfs.GridFS;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class ImageStoreService {
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFsOperations operations;

    public String addImage(MultipartFile file) throws IOException {
        System.out.println("hallo: "+ file.getName());
        DBObject metaData = new BasicDBObject();
        System.out.println("hallo 1: "+ file.getName());
        metaData.put("type", "image");
        System.out.println("hallo 1.2: ");
        ObjectId id = gridFsTemplate.store(
                file.getInputStream(), file.getName(), file.getContentType(), metaData);
        System.out.println("hallo 2: "+ file.getName());
        return id.toString();
    }

    public GridFsResource getImage(String id) throws IllegalStateException, IOException {
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
        GridFsResource resource = gridFsTemplate.getResource(file.getFilename());
        return resource;
    }
}
