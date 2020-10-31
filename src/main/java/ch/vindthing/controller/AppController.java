package ch.vindthing.controller;

import ch.vindthing.model.Item;
import ch.vindthing.model.Store;
import ch.vindthing.payload.request.ItemAddRequest;
import ch.vindthing.payload.request.ItemUpdateRequest;
import ch.vindthing.payload.request.StoreAddRequest;
import ch.vindthing.payload.request.StoreUpdateRequest;
import ch.vindthing.payload.response.ItemResponse;
import ch.vindthing.payload.response.MessageResponse;
import ch.vindthing.payload.response.StoreResponse;
import ch.vindthing.repository.ItemRepository;
import ch.vindthing.repository.StoreRepository;
import ch.vindthing.repository.UserRepository;
import ch.vindthing.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

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
        return ResponseEntity.ok(new ItemResponse(
                item.getId(), item.getName(), item.getDescription(), item.getQuantity()));
    }

    /**
     * Update an Item
     * storeId provided: Item is deleted from current store and added to new store
     * A new Item id will be generated
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
        if(itemUpdateRequest.getStoreId()!=null && !itemUpdateRequest.getStoreId().equals("")){ // Move to new store
            Store store = storeRepository.findById(itemUpdateRequest.getStoreId()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Item Update Move: Store ID not found: " + itemUpdateRequest.getStoreId()));
            itemRepository.delete(item); // Delete here and add new Item to other Store
            //item.setId(null); // todo needed?
            item = itemRepository.save(item); //todo old item id?
            Set<Item> items = store.getItems(); // Add Item to other Store
            items.add(item);
            store.setItems(items);
            storeRepository.save(store); // Update Store
        }else{
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
        }
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
        // Check if id or name
        if(itemUpdateRequest.getId()!=null && !itemUpdateRequest.getId().equals("")){
            Item item = itemRepository.findById(itemUpdateRequest.getId())
                    .orElseThrow(() ->
                            new UsernameNotFoundException("Item Delete: ID not found: " + itemUpdateRequest.getId()));
            itemRepository.delete(item);
            return ResponseEntity.ok(new MessageResponse("Item successfully deleted!"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("No ID provided!"));
    }

    /**
     * Add a new store
     * @param storeAddRequest Request
     * @return Response with ID, Name, Description, Location
     */
    @RequestMapping("/store/add")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> addStore(@Valid @RequestBody() StoreAddRequest storeAddRequest) {
        // TODO: Add User to store from token
        Store store = new Store(storeAddRequest.getName(), storeAddRequest.getDescription(), storeAddRequest.getLocation());
        storeRepository.save(store); // Save store
        return ResponseEntity.ok(
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
                new UsernameNotFoundException("Store Update: ID Not Found: " + storeUpdateRequest.getId()));
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
     * @param storeRequest Request
     * @return Status Response
     */
    @RequestMapping("/store/delete")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteStore(@Valid @RequestBody() StoreUpdateRequest storeRequest) {
        // TODO: only stores of user, delete all items in store first
        // Check if id or name
        if(storeRequest.getId()!=null && !storeRequest.getId().equals("")){
            Store store = storeRepository.findById(storeRequest.getId())
                    .orElseThrow(() ->
                            new UsernameNotFoundException("Store ID Not Found: " + storeRequest.getId()));
            storeRepository.delete(store);
            return ResponseEntity.ok(new MessageResponse("Store deleted!"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("No ID provided!"));
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
        List<Store> store = storeRepository.findAll();
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