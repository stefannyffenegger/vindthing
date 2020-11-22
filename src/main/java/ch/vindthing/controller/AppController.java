package ch.vindthing.controller;

import ch.vindthing.model.Item;
import ch.vindthing.model.Store;
import ch.vindthing.model.User;
import ch.vindthing.payload.request.*;
import ch.vindthing.payload.response.ItemResponse;
import ch.vindthing.payload.response.MessageResponse;
import ch.vindthing.payload.response.StoreResponse;
import ch.vindthing.repository.ItemRepository;
import ch.vindthing.repository.StoreRepository;
import ch.vindthing.repository.UserRepository;
import ch.vindthing.security.jwt.JwtUtils;
import ch.vindthing.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.DoubleStream;

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

    /**
     * Add an Item to a Store
     * @param itemAddRequest Request, must contain storeId
     * @return Response with ID, Name, Description, Quantity
     */
    @RequestMapping("/item/add")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> addItemToStore(@Valid @RequestHeader (name="Authorization") String token,
                                            @RequestBody() ItemAddRequest itemAddRequest) {
        User user = jwtUtils.getUserFromJwtToken(token);

        // Check if store exists
        Store store = storeRepository.findById(itemAddRequest.getStoreId()).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Item Add: Store ID not found: " + itemAddRequest.getStoreId()));

        // Usercheck
        if(!store.getOwner().getId().equals(user.getId())){
            return ResponseEntity.badRequest().body("Item Add: Not Store of User!");
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
    public ResponseEntity<?> updateItem(@Valid @RequestBody() ItemUpdateRequest itemUpdateRequest) {
        // TODO: Only Items/Stores of User
        // Find Store by Item ID
        Query query = new Query(Criteria.where("items._id").is(itemUpdateRequest.getId()));

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
    public ResponseEntity<?> moveItem(@Valid @RequestBody() ItemMoveRequest itemMoveRequest) {
        // TODO: Only Items/Stores of User
        Query query = new Query(Criteria.where("items._id").is(itemMoveRequest.getId()));
        query.fields().include("items.$");

        // Find Item
        Store store;
        Item item;
        try{
            store = mongoTemplate.findOne(query, Store.class);
            item = store.getItems().get(0);
            System.out.println("ITEM ID "+item.getId());
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Item Move: Item ID not found: " + itemMoveRequest.getId()
                    + " Exception: " + e);
        }

        // Check if store exists
        Store newStore = storeRepository.findById(itemMoveRequest.getStoreId()).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Item Move: Store ID not found: " + itemMoveRequest.getStoreId()));
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
    public ResponseEntity<?> deleteItem(@Valid @RequestBody() ItemUpdateRequest itemUpdateRequest) {

        // TODO: only stores of user
        Query query = new Query(Criteria.where("items._id").is(itemUpdateRequest.getId()));
        query.fields().include("items.$");

        Item newItem;
        try{
            newItem = mongoTemplate.findOne(query, Store.class).getItems().get(0);
            System.out.println("ITEM ID "+newItem.getId());
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Item Delete: Item ID not found: " + itemUpdateRequest.getId()
                    + " Exception: " + e);
        }

        // Delete Item
        Update update = new Update().pull("items", newItem);
        mongoTemplate.updateFirst(query, update, Store.class);
        return ResponseEntity.ok(new MessageResponse("Item " + itemUpdateRequest.getId() + " successfully deleted!"));
    }

    /**
     * Add a new store
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
                store.getName(), store.getDescription(), store.getLocation(), store.getCreated(), store.getLastedit()));
    }

    /**
     * Update a store
     * @param storeUpdateRequest Request with existing Store ID
     * @return Response with ID, Name, Description, Location
     */
    @RequestMapping("/store/update")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> updateStore(@Valid @RequestBody() StoreUpdateRequest storeUpdateRequest) {
        // TODO: Get User from token, check if store from user
        Store store = storeRepository.findById(storeUpdateRequest.getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Store Update: Store ID not found: " + storeUpdateRequest.getId()));
        if(storeUpdateRequest.getName()!=null && !storeUpdateRequest.getName().equals("")){
            store.setName(storeUpdateRequest.getName());
        }
        if(storeUpdateRequest.getDescription()!=null && !storeUpdateRequest.getDescription().equals("")){
            store.setDescription(storeUpdateRequest.getDescription());
        }
        if(storeUpdateRequest.getLocation()!=null && !storeUpdateRequest.getLocation().equals("")){
            store.setLocation(storeUpdateRequest.getLocation());
        }
        store.setLastedit(StringUtils.getCurrentTimeStamp()); // Update last edit
        storeRepository.save(store); // Update store
        return ResponseEntity.ok(new StoreResponse(store.getId(), store.getName(), store.getDescription(),
                store.getLocation(), store.getCreated(), store.getLastedit()));
    }

    /**
     * Deletes a store if correct ID is provided
     * @param storeUpdateRequest Request
     * @return Status Response
     */
    @RequestMapping("/store/delete")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteStore(@Valid @RequestBody() StoreUpdateRequest storeUpdateRequest) {
        // TODO: only stores of user, delete all items in store first
        Store store = storeRepository.findById(storeUpdateRequest.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Store Delete: Store ID not found: " + storeUpdateRequest.getId()));
        storeRepository.delete(store);
        return ResponseEntity.ok(new MessageResponse("Store deleted!"));
    }

    /**
     * Get all stores and contained items
     * Should be loaded on establishment of websocket
     * @return Stores with items
     */
    @GetMapping("/store/get-all")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllStores() {
        // TODO: only stores of user
        List<Store> store = storeRepository.findAll(); //todo stream filter :(
/*                .stream()
                .filter(filst -> !filst.getUsers()
                        .stream()
                        .filter(filus -> !filus.getId()));*/

        return ResponseEntity.ok(store);
    }

    @RequestMapping("/store/get-by-name")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @Deprecated
    public ResponseEntity<?> getStoreByName(@Valid @RequestBody() StoreAddRequest storeRequest) {
        // TODO: only stores of user
        Store store = storeRepository.findByName(storeRequest.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Store Not Found: " + storeRequest.getName()));
        return ResponseEntity.ok(new StoreResponse(store.getId(), store.getName(), store.getDescription(),
                store.getLocation(), store.getCreated(), store.getLastedit()));
    }
}
