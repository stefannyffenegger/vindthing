package ch.vindthing.controller;

import ch.vindthing.model.Item;
import ch.vindthing.model.Role;
import ch.vindthing.model.Store;
import ch.vindthing.payload.request.ItemRequest;
import ch.vindthing.payload.request.StoreRequest;
import ch.vindthing.payload.response.MessageResponse;
import ch.vindthing.repository.ItemRepository;
import ch.vindthing.repository.StoreRepository;
import ch.vindthing.repository.UserRepository;
import ch.vindthing.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping("/")
public class AppController {
    @Autowired
    ItemRepository itemRepository;

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtils jwtUtils;

    @GetMapping("/db-overview")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public String dbOverview() {
        return "db-overview";
    }

    @RequestMapping("/item/add")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @Deprecated
    public ResponseEntity<?> addItem(@Valid @RequestBody() ItemRequest itemRequest) {
        // Use /store/add-item instead
        Item item = new Item(itemRequest.getName(), itemRequest.getDescription(), itemRequest.getQuantity());
        itemRepository.save(item);
        return ResponseEntity.ok(new MessageResponse("Item added!"));
    }

    @RequestMapping("/item/get-by-name")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getItemByName(@Valid @RequestBody() ItemRequest itemRequest) {
        // TODO: only items of user or store; better returns
        Item item = itemRepository.findByName(itemRequest.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Item Not Found: " + itemRequest.getName()));
        return ResponseEntity.ok(new ItemRequest(item.getId(), item.getName(), item.getDescription(), item.getQuantity()));
    }

    @RequestMapping("/item/get-by-id")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getItemById(@Valid @RequestBody() ItemRequest itemRequest) {
        // TODO: only items of user or store; better returns
        Item item = itemRepository.findByName(itemRequest.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Item Not Found: " + itemRequest.getName()));
        return ResponseEntity.ok(new ItemRequest(item.getId(), item.getName(), item.getDescription(), item.getQuantity()));
    }

    @RequestMapping("/store/add")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> addStore(@Valid @RequestBody() StoreRequest storeRequest) {
        // TODO: Add User to store as notblank
        Store store = new Store(storeRequest.getName(), storeRequest.getDescription(), storeRequest.getLocation());
        storeRepository.save(store);
        return ResponseEntity.ok(new MessageResponse("Store added!"));
    }

    @RequestMapping("/store/add-item")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> addItemToStore(@Valid @RequestBody() ItemRequest itemRequest) {
        Item item = new Item(itemRequest.getName(), itemRequest.getDescription(), itemRequest.getQuantity());
        itemRepository.save(item);

        Store store = storeRepository.findByName(itemRequest.getStore()).
                orElseThrow(() -> new UsernameNotFoundException("Store Not Found: " + itemRequest.getStore()));

        Set<Item> items = new HashSet<>();
        items.add(item);
        store.setItems(items);
        storeRepository.save(store);
        return ResponseEntity.ok(new ItemRequest(item.getId(), item.getName(), item.getDescription(), item.getQuantity()));
    }

    @RequestMapping("/store/delete")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteStore(@Valid @RequestBody() StoreRequest storeRequest) {
        // TODO: only stores of user, change to del by id, only if empty
        Store store = storeRepository.findByName(storeRequest.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Store Not Found: " + storeRequest.getName()));
        storeRepository.delete(store);
        return ResponseEntity.ok(new MessageResponse("Store deleted!"));
    }

    @RequestMapping("/store/get-by-name")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getStoreByName(@Valid @RequestBody() StoreRequest storeRequest) {
        // TODO: only stores of user
        Store store = storeRepository.findByName(storeRequest.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Store Not Found: " + storeRequest.getName()));
        return ResponseEntity.ok(new StoreRequest(store.getName(), store.getDescription(), store.getLocation()));
    }

    @GetMapping("/store/get-all")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllStores() {
        // TODO: only stores of user
        List<Store> store = storeRepository.findAll();
        return ResponseEntity.ok(store);
    }
}