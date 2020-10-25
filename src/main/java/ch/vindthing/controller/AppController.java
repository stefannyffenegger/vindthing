package ch.vindthing.controller;

import ch.vindthing.model.Item;
import ch.vindthing.payload.request.ItemRequest;
import ch.vindthing.payload.response.MessageResponse;
import ch.vindthing.repository.ItemRepository;
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
    UserRepository userRepository;

    @Autowired
    JwtUtils jwtUtils;

    @GetMapping("/db-overview")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public String dbOverview() {
        return "db-overview";
    }

    @RequestMapping("/items/add")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> addItem(@Valid @RequestBody() ItemRequest itemRequest) {
        System.out.println("Item Request ADD: " + itemRequest.getName() + itemRequest.getDescription());

        Item item = new Item(itemRequest.getName(), itemRequest.getDescription(), itemRequest.getQuantity());
        itemRepository.save(item);

        return ResponseEntity.ok(new MessageResponse("Item added!"));
    }

    @RequestMapping("/items/get-by-name")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getItemByName(@Valid @RequestBody() ItemRequest itemRequest) {
        /*if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }else{
            return ResponseEntity.badRequest().body("couldn't find profile");
        }
        String email = jwtUtils.getEmailFromJwtToken(token);*/
        System.out.println("Item Request find by name: " + itemRequest.getName() + itemRequest.getDescription());

        Item item = itemRepository.findByName(itemRequest.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Item Not Found with name: " + itemRequest.getName()));

        return ResponseEntity.ok(new ItemRequest(item.getName(), item.getDescription(), item.getQuantity()));
    }
}