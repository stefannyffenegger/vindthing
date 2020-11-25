package ch.vindthing.controller;

import ch.vindthing.model.Item;
import ch.vindthing.model.Store;
import ch.vindthing.model.User;
import ch.vindthing.payload.request.*;
import ch.vindthing.payload.response.ItemResponse;
import ch.vindthing.payload.response.MessageResponse;
import ch.vindthing.payload.response.StoreResponse;
import ch.vindthing.repository.StoreRepository;
import ch.vindthing.repository.UserRepository;
import ch.vindthing.security.jwt.JwtUtils;
import ch.vindthing.service.ImageStoreService;
import ch.vindthing.util.StringUtils;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 * Controls the Application API for the Frontend
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping("/api/")
public class AppController {
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtils jwtUtils;

    ImageStoreService imageStoreService;

    /**
     * Add an Item to a Store
     * @param itemAddRequest Request, must contain storeId
     * @return Response with ID, Name, Description, Quantity
     */
    @RequestMapping("/item/add")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> addItemToStore(@Valid @RequestHeader (name="Authorization") String token,
                                            @RequestBody() ItemAddRequest itemAddRequest) {
        // Check if store exists
        Store store = storeRepository.findById(itemAddRequest.getStoreId()).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Item Add: Store ID not found: " + itemAddRequest.getStoreId()));

        // Usercheck
        if(!jwtUtils.checkPermissionSharedUsers(token, store)){
            return ResponseEntity.badRequest().body("Item Add: No Permission for this Store!");
        }

        Item item = new Item(itemAddRequest.getName(), itemAddRequest.getDescription(), itemAddRequest.getQuantity());
        store.getItems().add(item);
        storeRepository.save(store); // Update Store
        return ResponseEntity.status(HttpStatus.CREATED).body(new ItemResponse(item.getId(), item.getName(),
                item.getDescription(), item.getQuantity(), item.getCreated(), item.getLastedit()));
    }

    /**
     * Update an existing Item
     * @param itemUpdateRequest Request, must contain Item id
     * @return Response with ID, Name, Description, Quantity
     */
    @RequestMapping("/item/update")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> updateItem(@Valid @RequestHeader (name="Authorization") String token,
                                        @RequestBody() ItemUpdateRequest itemUpdateRequest) {
        // Find Store by Item ID
        Query query = new Query(Criteria.where("items._id").is(itemUpdateRequest.getId()));
        try {
            Store store = mongoTemplate.findOne(query, Store.class);
            // Usercheck
            if(!jwtUtils.checkPermissionSharedUsers(token, store)){
                return ResponseEntity.badRequest().body("Item Update: No Permission for this Store!");
            }
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Item Update Failed for ID: " + itemUpdateRequest.getId()
                    + " Exception: " + e);
        }

        Update update = new Update();
        if(itemUpdateRequest.getName()!=null && !itemUpdateRequest.getName().equals("")){
            update.set("items.$.name", itemUpdateRequest.getName());
        }
        if(itemUpdateRequest.getDescription()!=null && !itemUpdateRequest.getDescription().equals("")){
            update.set("items.$.description", itemUpdateRequest.getDescription());
        }
        if(itemUpdateRequest.getQuantity() != 0){
            update.set("items.$.quantity", itemUpdateRequest.getQuantity());
        }
        String timestamp = StringUtils.getCurrentTimeStamp();
        update.set("items.$.lastedit", timestamp);
        update.set("lastedit", timestamp);

        // Find Item and send response
        Query findQuery = query;
        findQuery.fields().include("items.$");

        Item newItem;
        try{
            mongoTemplate.updateFirst(query, update, Store.class);
            newItem = mongoTemplate.findOne(findQuery, Store.class).getItems().get(0);
            return ResponseEntity.ok(new ItemResponse(newItem.getId(), newItem.getName(), newItem.getDescription(),
                    newItem.getQuantity(), newItem.getCreated(), newItem.getLastedit()));
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Item Update Failed for ID: " + itemUpdateRequest.getId()
                    + " Exception: " + e);
        }
    }

    /**
     * Move an existing Item to another Store
     * A new Item id will be generated
     * @param itemMoveRequest Request, must contain Item id and new Store id
     * @return Response with ID, Name, Description, Quantity
     */
    @RequestMapping("/item/move")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> moveItem(@Valid @RequestHeader (name="Authorization") String token,
                                      @RequestBody() ItemMoveRequest itemMoveRequest) {
        // Find Store and Item by Item ID
        Query query = new Query(Criteria.where("items._id").is(itemMoveRequest.getId()));
        query.fields().include("items.$").include("sharedUsers");

        // Find Item and current Store
        Store store;
        Item item;
        try{
            store = mongoTemplate.findOne(query, Store.class);
            // Usercheck current Store
            if(!jwtUtils.checkPermissionSharedUsers(token, store)){
                return ResponseEntity.badRequest().body("Item Move: No Permission for this Store!");
            }
            item = store.getItems().get(0);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Item Move: Item ID not found: " + itemMoveRequest.getId()
                    + " Exception: " + e);
        }

        // Check if store exists
        Store newStore = storeRepository.findById(itemMoveRequest.getStoreId()).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Item Move: Store ID not found: " + itemMoveRequest.getStoreId()));
        // Usercheck new Store
        if(!jwtUtils.checkPermissionSharedUsers(token, newStore)){
            return ResponseEntity.badRequest().body("Item Move: No Permission for the new Store!");
        }

        newStore.getItems().add(item);
        storeRepository.save(newStore); // Update Store

        // Delete Item
        Query deleteQuery = new Query();
        deleteQuery.addCriteria(new Criteria().andOperator(
                Criteria.where("items._id").is(itemMoveRequest.getId()),
                Criteria.where("_id").is(store.getId())));
        deleteQuery.fields().include("items.$");

        Update update = new Update().pull("items", item);
        mongoTemplate.updateFirst(deleteQuery, update, Store.class);

        return ResponseEntity.ok(new ItemResponse(item.getId(), item.getName(), item.getDescription(),
                item.getQuantity(), item.getCreated(), item.getLastedit()));
    }

    /**
     * Deletes an item if correct ID is provided
     * @param itemUpdateRequest Request
     * @return Status Response
     */
    @RequestMapping("/item/delete")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteItem(@Valid @RequestHeader (name="Authorization") String token,
                                        @RequestBody() ItemUpdateRequest itemUpdateRequest) {
        // Find Store and Item by Item ID
        Query query = new Query(Criteria.where("items._id").is(itemUpdateRequest.getId()));
        query.fields().include("items.$").include("sharedUsers");

        Store store;
        Item item;
        try{
            store = mongoTemplate.findOne(query, Store.class);
            // Usercheck
            if(!jwtUtils.checkPermissionSharedUsers(token, store)){
                return ResponseEntity.badRequest().body("Item Delete: No Permission for this Store!");
            }
            item = store.getItems().get(0);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Item Delete: Item ID not found: " + itemUpdateRequest.getId()
                    + " Exception: " + e);
        }

        // Delete Item
        Update update = new Update().pull("items", item);
        mongoTemplate.updateFirst(query, update, Store.class);
        return ResponseEntity.ok(new MessageResponse("Item " + itemUpdateRequest.getId() + " successfully deleted!"));
    }

    /**
     * Add a new Store
     * Sets the current User as owner
     * @param storeAddRequest Request
     * @return Response with ID, Name, Description, Location
     */
    @RequestMapping("/store/add")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> addStore(@Valid @RequestHeader(name="Authorization") String token,
                                      @Valid @RequestBody() StoreAddRequest storeAddRequest) {
        User user = jwtUtils.getUserFromJwtToken(token);
        Store store = new Store(storeAddRequest.getName(), storeAddRequest.getDescription(),
                storeAddRequest.getLocation(), user);
        storeRepository.save(store); // Save store
        return ResponseEntity.status(HttpStatus.CREATED).body(new StoreResponse(store.getId(),
                store.getName(), store.getDescription(), store.getLocation(), store.getCreated(), store.getLastEdit(),
                store.getOwner().toString(), store.getSharedUsers()));
    }

    /**
     * Update a Store
     * Only the owner can update a Store
     * @param storeUpdateRequest Request with existing Store ID
     * @return Response with ID, Name, Description, Location
     */
    @RequestMapping("/store/update")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> updateStore(@Valid @RequestHeader (name="Authorization") String token,
                                         @RequestBody() StoreUpdateRequest storeUpdateRequest) {
        Store store = storeRepository.findById(storeUpdateRequest.getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Store Update: Store ID not found: " + storeUpdateRequest.getId()));
        // Usercheck
        if(!jwtUtils.checkPermissionOwner(token, store)){
            return ResponseEntity.badRequest().body("Store Update: Only owners can update a Store!");
        }
        if(storeUpdateRequest.getName()!=null && !storeUpdateRequest.getName().equals("")){
            store.setName(storeUpdateRequest.getName());
        }
        if(storeUpdateRequest.getDescription()!=null && !storeUpdateRequest.getDescription().equals("")){
            store.setDescription(storeUpdateRequest.getDescription());
        }
        if(storeUpdateRequest.getLocation()!=null && !storeUpdateRequest.getLocation().equals("")){
            store.setLocation(storeUpdateRequest.getLocation());
        }
        store.setLastEdit(StringUtils.getCurrentTimeStamp()); // Update last edit
        storeRepository.save(store); // Update store
        return ResponseEntity.ok(new StoreResponse(store.getId(), store.getName(), store.getDescription(),
                store.getLocation(), store.getCreated(), store.getLastEdit(), store.getOwner().toString(),
                store.getSharedUsers()));
    }

    /**
     * Deletes a store if correct ID is provided
     * Only the owner can delete a Store
     * @param storeUpdateRequest Request
     * @return Status Response
     */
    @RequestMapping("/store/delete")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteStore(@Valid @RequestHeader (name="Authorization") String token,
                                         @RequestBody() StoreUpdateRequest storeUpdateRequest) {
        Store store = storeRepository.findById(storeUpdateRequest.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Store Delete: Store ID not found: " + storeUpdateRequest.getId()));
        // Usercheck
        if(!jwtUtils.checkPermissionOwner(token, store)){
            return ResponseEntity.badRequest().body("Store Delete: Only owners can delete a Store!");
        }
        storeRepository.delete(store);
        return ResponseEntity.ok(new MessageResponse("Store deleted!"));
    }

    /**
     * Get all stores and contained items of user
     * Should be loaded on establishment of websocket
     * @return Stores with items
     */
    @GetMapping("/store/get-all")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllStores(@RequestHeader (name="Authorization") String token) {
        User user = jwtUtils.getUserFromJwtToken(token);
        Query query = new Query(Criteria.where("sharedUsers._id").is(user.getId()));
        List<Store> stores = mongoTemplate.find(query, Store.class);
        for (Store store: stores) {
            store.getOwner().setId(null);
            store.getOwner().setPassword(null);
            store.getOwner().setRoles(null);
            for (User fuser: store.getSharedUsers()) {
                fuser.setPassword(null);
                fuser.setId(null);
                fuser.setRoles(null);
            }
        }

        return ResponseEntity.ok(stores);
    }

    /**
     * Adds Users to Stores
     * Only the owner can update a Store
     * @param userAddRequest Request
     * @return Status Response
     */
    @RequestMapping("/store/user/add")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> addUserToStore(@Valid @RequestHeader (name="Authorization") String token,
                                         @RequestBody() UserAddRequest userAddRequest) {
        Store store = storeRepository.findById(userAddRequest.getStoreId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Store Add User: Store ID not found: " + userAddRequest.getStoreId()));
        // Usercheck
        if(!jwtUtils.checkPermissionOwner(token, store)){
            return ResponseEntity.badRequest().body("Store Add User: Only owners can update a Store!");
        }
        if(userAddRequest.getOwner()!=null && !userAddRequest.getOwner().equals("")){
            User newOwner = userRepository.findByEmail(userAddRequest.getOwner()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Store Add User: User not found: " + userAddRequest.getOwner()));
            System.out.println("Owner update "+!newOwner.getEmail().equals(userAddRequest.getOwner()));
            System.out.println(store.getOwner().getEmail());
            System.out.println(userAddRequest.getOwner());

            if(!store.getOwner().getEmail().equals(userAddRequest.getOwner())){
                store.setOwner(newOwner);
                if(store.getSharedUsers().stream().noneMatch(bob -> bob.getEmail().equals(userAddRequest
                        .getSharedUser()))){
                    store.getSharedUsers().add(newOwner);
                }
            }
        }
        if(userAddRequest.getSharedUser()!=null && !userAddRequest.getSharedUser().equals("")){
            User newUser = userRepository.findByEmail(userAddRequest.getSharedUser()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Store Add User: User not found: " + userAddRequest.getSharedUser()));
            if(store.getSharedUsers().stream().noneMatch(bob -> bob.getEmail().equals(userAddRequest
                    .getSharedUser()))){
                store.getSharedUsers().add(newUser);
            }
        }
        store.setLastEdit(StringUtils.getCurrentTimeStamp()); // Update last edit
        storeRepository.save(store); // Update store
        return ResponseEntity.ok(new StoreResponse(store.getId(), store.getName(), store.getDescription(),
                store.getLocation(), store.getCreated(), store.getLastEdit(), store.getOwner().toString(),
                store.getSharedUsers()));
    }

    /**
     * Removes Users from Stores
     * Only the owner can update a Store
     * @param userRemoveRequest Request
     * @return Status Response
     */
    @RequestMapping("/store/user/remove")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> removeUserFromStore(@Valid @RequestHeader (name="Authorization") String token,
                                            @RequestBody() UserRemoveRequest userRemoveRequest) {
        Store store = storeRepository.findById(userRemoveRequest.getStoreId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Store Remove User: Store ID not found: " + userRemoveRequest.getStoreId()));
        // Usercheck
        if(!jwtUtils.checkPermissionOwner(token, store)){
            return ResponseEntity.badRequest().body("Store Remove User: Only owners can delete a Store!");
        }
        if(userRemoveRequest.getSharedUser() != null && !userRemoveRequest.getSharedUser().equals("")){
            User user = userRepository.findByEmail(userRemoveRequest.getSharedUser()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Store Remove User: User not found: " + userRemoveRequest.getSharedUser()));
            store.getSharedUsers().removeIf(buzz -> buzz.getEmail().equals(userRemoveRequest.getSharedUser()));
        }
        store.setLastEdit(StringUtils.getCurrentTimeStamp()); // Update last edit
        storeRepository.save(store);
        return ResponseEntity.ok(new StoreResponse(store.getId(), store.getName(), store.getDescription(),
                store.getLocation(), store.getCreated(), store.getLastEdit(), store.getOwner().toString(),
                store.getSharedUsers()));
    }

    /**
     *
     * @return
     */
    @PostMapping("/image/add")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> uploadImage(@RequestParam("id") String id,
                                         @RequestParam("type") String type,
                                         @RequestParam("file") MultipartFile file) throws IOException {

        //todo everything here O.O

        imageStoreService = new ImageStoreService();
        System.out.println("hallo vorher: "+ file.getOriginalFilename());
        String imageId = imageStoreService.addImage(file);
        switch (type){
            case "item":
                // Find Store and Item by Item ID
                Query query = new Query(Criteria.where("items._id").is(id));
                query.fields().include("items.$"); //todo .include("sharedUsers");
                Query findQuery = query;
                findQuery.fields().include("items.$");

                Update update = new Update();
                if(imageId!=null && !imageId.equals("")){
                    update.set("items.$.imageId", imageId);
                }

                Item newItem;
                try{
                    mongoTemplate.updateFirst(query, update, Store.class);
                    newItem = mongoTemplate.findOne(findQuery, Store.class).getItems().get(0);
                    return ResponseEntity.ok(new ItemResponse(newItem.getId(), newItem.getName(), newItem.getDescription(),
                            newItem.getQuantity(), newItem.getCreated(), newItem.getLastedit()));
                }catch (Exception e) {
                    return ResponseEntity.badRequest().body("Item Update Failed for ID: " + id
                            + " Exception: " + e);
                }
            case "store":
                //todo la meme chause
                break;
            default:
                return ResponseEntity.badRequest().body("Wrong image parameters!");
        }
        return ResponseEntity.ok(new MessageResponse("Image uploaded!"));
    }

    /**
     *
     * @return
     */
    @GetMapping("image/get/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> downloadImage(@PathVariable String id) {
        GridFsResource image;
        try {
            image = imageStoreService.getImage(id);
            return ResponseEntity.ok()
                    .contentLength(image.getFile().length())
                    .contentType(MediaType.parseMediaType(image.getContentType()))
                    .body(new InputStreamResource(image.getInputStream()));

        } catch (IllegalStateException | IOException e) {
            return ResponseEntity.badRequest().body("Cannot get image");
        }

    }
}
