package ch.vindthing.controller;

import java.util.Set;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * Handles user connections and add/removes them to/from ActiveUserManager
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
//@RequestMapping("/ws")
public class WebSocketConnectionRestController {

    @Autowired
    private ActiveUserManager activeSessionManager;

    @PostMapping("/rest/user-connect")
    public String userConnect(HttpServletRequest request, @ModelAttribute("username") String userName) {
        String remoteAddr = "";
        System.out.println("User connected");
        if (request != null) {
            remoteAddr = request.getHeader("Remote_Addr");
            System.out.println("not null: "+remoteAddr);
            if (StringUtils.isEmpty(remoteAddr)) {
                remoteAddr = request.getHeader("X-FORWARDED-FOR");
                System.out.println("Is empty: "+remoteAddr);
                if (remoteAddr == null || "".equals(remoteAddr)) {
                    remoteAddr = request.getRemoteAddr();
                    System.out.println("remaddr is null: "+remoteAddr);
                }
            }
        }

        activeSessionManager.add(userName, remoteAddr);
        return remoteAddr;
    }

    @PostMapping("/rest/user-disconnect")
    public String userDisconnect(@ModelAttribute("username") String userName) {
        activeSessionManager.remove(userName);
        return "disconnected";
    }

    @GetMapping("/rest/active-users-except/{userName}")
    public Set<String> getActiveUsersExceptCurrentUser(@PathVariable String userName) {
        return activeSessionManager.getActiveUsersExceptCurrentUser(userName);
    }
}