package ch.vindthing.controller;

import ch.vindthing.model.Item;
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
    public ResponseEntity<?> addItem(@Valid @RequestBody() ItemRequest itemRequest) {
        Item item = new Item(itemRequest.getName(), itemRequest.getDescription(), itemRequest.getQuantity());
        itemRepository.save(item);

        return ResponseEntity.ok(new MessageResponse("Item added!"));
    }

    @RequestMapping("/item/get-by-name")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getItemByName(@Valid @RequestBody() ItemRequest itemRequest) {
        Item item = itemRepository.findByName(itemRequest.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Item Not Found with name: " + itemRequest.getName()));

        return ResponseEntity.ok(new ItemRequest(item.getName(), item.getDescription(), item.getQuantity()));
    }

    @RequestMapping("/store/add")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> addStore(@Valid @RequestBody() StoreRequest storeRequest) {
        Store store = new Store(storeRequest.getName(), storeRequest.getDescription(), storeRequest.getLocation());
        storeRepository.save(store);

        return ResponseEntity.ok(new MessageResponse("Store added!"));
    }

    @RequestMapping("/store/get-by-name")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getItemByName(@Valid @RequestBody() StoreRequest storeRequest) {
        Store store = storeRepository.findByName(storeRequest.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Store Not Found with name: " + storeRequest.getName()));

        return ResponseEntity.ok(new StoreRequest(store.getName(), store.getDescription(), store.getLocation()));
    }
}