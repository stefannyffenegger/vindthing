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

    @RequestMapping("/item/add")
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
        return ResponseEntity.ok(new ItemRequest(
                item.getId(), item.getName(), item.getDescription(), item.getQuantity()));
    }

    @RequestMapping("/store/add")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> addStore(@Valid @RequestBody() StoreRequest storeRequest) {
        // TODO: Add User to store as notblank
        Store store = new Store(storeRequest.getName(), storeRequest.getDescription(), storeRequest.getLocation());
        storeRepository.save(store);
        return ResponseEntity.ok(new MessageResponse("Store added!"));
    }

    @RequestMapping("/store/delete")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteStore(@Valid @RequestBody() StoreRequest storeRequest) {
        // TODO: only stores of user, delete all items in store first

        if(storeRequest.getId()!=null && !storeRequest.getId().equals("")){
            Store store = storeRepository.findById(storeRequest.getId())
                    .orElseThrow(() -> new UsernameNotFoundException("Store ID Not Found: " + storeRequest.getId()));
            storeRepository.delete(store);
        }else if(storeRequest.getName()!=null && !storeRequest.getName().equals("")){
            Store store = storeRepository.findByName(storeRequest.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("Store Name Not Found: " + storeRequest.getName()));
            storeRepository.delete(store);
        }
        return ResponseEntity.ok(new MessageResponse("Store deleted!"));
    }

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
    public ResponseEntity<?> getStoreByName(@Valid @RequestBody() StoreRequest storeRequest) {
        // TODO: only stores of user
        Store store = storeRepository.findByName(storeRequest.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Store Not Found: " + storeRequest.getName()));
        return ResponseEntity.ok(new StoreRequest(
                store.getId(), store.getName(), store.getDescription(), store.getLocation()));
    }

    @RequestMapping("/store/get-by-id")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @Deprecated
    public ResponseEntity<?> getStoreById(@Valid @RequestBody() StoreRequest storeRequest) {
        // TODO: only stores of user
        Store store = storeRepository.findById(storeRequest.getId())
                .orElseThrow(() -> new UsernameNotFoundException("Store Not Found: " + storeRequest.getId()));
        return ResponseEntity.ok(new StoreRequest(
                store.getId(), store.getName(), store.getDescription(), store.getLocation()));
    }
}