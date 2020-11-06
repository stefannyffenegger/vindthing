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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * Controls the Application API for the Frontend
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping("/api/")
public class AppController {
    @Autowired
    ItemRepository itemRepository;

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
    public ResponseEntity<?> addItemToStore(@Valid @RequestBody() ItemAddRequest itemAddRequest) {
        // TODO: Only Items/Stores of User
        // Check if store exists
        Store store = storeRepository.findById(itemAddRequest.getStoreId()).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Item Add: Store ID not found: " + itemAddRequest.getStoreId()));
        Item item = new Item(itemAddRequest.getName(), itemAddRequest.getDescription(), itemAddRequest.getQuantity());
        itemRepository.save(item); // Save new Item
        Set<Item> items = store.getItems(); //todo test with only add
        items.add(item);
        store.setItems(items);
        storeRepository.save(store); // Update Store
        return ResponseEntity.status(HttpStatus.CREATED).body(new ItemResponse(
                item.getId(), item.getName(), item.getDescription(), item.getQuantity()));
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
        Item item = itemRepository.findById(itemUpdateRequest.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Item Update: Item ID not found: " + itemUpdateRequest.getId()));
        if(itemUpdateRequest.getName()!=null && !itemUpdateRequest.getName().equals("")){
            item.setName(itemUpdateRequest.getName());
        }
        if(itemUpdateRequest.getDescription()!=null && !itemUpdateRequest.getDescription().equals("")){
            item.setDescription(itemUpdateRequest.getDescription());
        }
        if(itemUpdateRequest.getQuantity() != 0){
            item.setQuantity(itemUpdateRequest.getQuantity());
        }
        itemRepository.save(item);
        return ResponseEntity.ok(new ItemResponse(
                item.getId(), item.getName(), item.getDescription(), item.getQuantity()));
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
        Item item = itemRepository.findById(itemMoveRequest.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Item Move: Item ID not found: " + itemMoveRequest.getId()));
        Store store = storeRepository.findById(itemMoveRequest.getStoreId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Item Move: Store ID not found: " + itemMoveRequest.getStoreId()));
        itemRepository.deleteById(item.getId()); // Delete here and add new Item to other Store
        item.setId(null);
        item = itemRepository.save(item);
        Set<Item> items = store.getItems(); // Add Item to other Store
        items.add(item);
        store.setItems(items);
        storeRepository.save(store); // Update Store
        return ResponseEntity.ok(new ItemResponse(
                item.getId(), item.getName(), item.getDescription(), item.getQuantity()));
    }

    /**
     * Deletes an item if correct ID is provided
     * @param itemUpdateRequest Request
     * @return Status Response
     */
    @RequestMapping("/item/delete")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteItem(@Valid @RequestBody() ItemUpdateRequest itemUpdateRequest) {
        // TODO: only stores of user, what happens in store if item deleted?
        Item item = itemRepository.findById(itemUpdateRequest.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Item Delete: Item ID not found: " + itemUpdateRequest.getId()));
        itemRepository.deleteById(item.getId());
        return ResponseEntity.ok(new MessageResponse("Item successfully deleted!"));
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
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new StoreResponse(store.getId(), store.getName(), store.getDescription(), store.getLocation()));
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
        storeRepository.save(store); // Update store
        return ResponseEntity.ok(
                new StoreResponse(store.getId(), store.getName(), store.getDescription(), store.getLocation()));
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
        return ResponseEntity.ok(new StoreResponse(
                store.getId(), store.getName(), store.getDescription(), store.getLocation()));
    }
}
