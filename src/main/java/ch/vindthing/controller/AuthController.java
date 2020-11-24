package ch.vindthing.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;

import ch.vindthing.model.ConfirmationToken;
import ch.vindthing.payload.request.ProfileUpdateRequest;
import ch.vindthing.payload.response.UserResponse;
import ch.vindthing.repository.ConfTokenRepository;
import ch.vindthing.repository.RoleRepository;
import ch.vindthing.repository.UserRepository;
import ch.vindthing.security.jwt.JwtUtils;
import ch.vindthing.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ch.vindthing.model.ERole;
import ch.vindthing.model.Role;
import ch.vindthing.model.User;
import ch.vindthing.payload.request.LoginRequest;
import ch.vindthing.payload.request.SignupRequest;
import ch.vindthing.payload.response.JwtResponse;
import ch.vindthing.payload.response.MessageResponse;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controls the /api/auth API
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Value("${spring.mail.username}")
    private String springMailUsername;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;



    @Autowired
    private ConfTokenRepository confirmationTokenRepository;

    @Autowired
    private EmailSenderService emailSenderService;



    /**
     * Signin/Login user
     * @param loginRequest Request
     * @return Response
     */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid credentials"));
        if (!user.isEnabled()) {
            return ResponseEntity.badRequest().body("User is not yet confirmed!");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        /*UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());*/

        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    /**
     * Signup new users
     * @param signUpRequest Request
     * @return Respoonse
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user account
        User user = new User(signUpRequest.getName(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        // If no role is included
        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default: // If included role is not found, assign user role
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);


        ConfirmationToken confirmationToken = new ConfirmationToken(user);

        confirmationTokenRepository.save(confirmationToken);

        /*SimpleMailMessage mailMessage = new SimpleMailMessage();


        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setFrom(springMailUsername);
        mailMessage.setText("To confirm your account, please click here : "
                +"http://localhost:8080/api/auth/profile/confirm-account?token="
                +confirmationToken.getConfirmationToken());*/

        try {

            MimeMessage message = emailSenderService.createMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");
            String htmlMsg = "<body style='border:2px solid black'>"
                    +"<a href=http://localhost:8080/api/auth/profile/confirm-account?token="
                    + confirmationToken.getConfirmationToken()
                    +">Click here to validate your account<a></body>";
            message.setContent(htmlMsg, "text/html");
            helper.setTo(user.getEmail());
            helper.setSubject("Complete Registration!");

            emailSenderService.sendEmail(message);

        }catch (javax.mail.MessagingException e){
            return ResponseEntity.badRequest().body("Error");
        }




        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    /**
     * Get user profile
     * @return Response
     */
    @GetMapping("/profile/confirm-account")
    public ResponseEntity<?> confirmUserAccount(@Valid @RequestParam("token")String confirmationToken) {

        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Not a valid Confirmation Token"));

        if(token != null){
            User user = userRepository.findByEmail(token.getUser().getEmail())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Not a valid Confirmation Token"));
            user.setEnabled(true);
            userRepository.save(user);
        }
        else{
            return ResponseEntity.badRequest().body("Empty token!");
        }

        return ResponseEntity.ok(new MessageResponse("User validated successfully!"));
    }

    /**
     * Get user profile
     * @return Response
     */
    @GetMapping("/profile/get")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserProfile(@Valid @RequestHeader (name="Authorization") String token) {
        User user = jwtUtils.getUserFromJwtToken(token);

        if (user != null) {
            List<String> roles = user.getRoles().stream()
                    .map(item -> item.getName().toString())
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new UserResponse(user.getName(), user.getEmail(), roles));
        }else{
            return ResponseEntity.badRequest().body("couldn't find profile");
        }
    }

    /**
     * Update user profile
     * @param token JWT token
     * @param profileUpdateRequest Update Request
     * @return Response
     */
    @PutMapping("/profile/update")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> updateUserProfile(@Valid @RequestHeader (name="Authorization") String token,
                                               @RequestBody ProfileUpdateRequest profileUpdateRequest) {
        User user = jwtUtils.getUserFromJwtToken(token);

        if(profileUpdateRequest.getEmail() != null && !profileUpdateRequest.getEmail().equals("")){
            if (userRepository.existsByEmail(profileUpdateRequest.getEmail())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
            }
            user.setEmail(profileUpdateRequest.getEmail());
        }
        if(profileUpdateRequest.getPassword() != null && !profileUpdateRequest.getPassword().equals("")){
            user.setPassword(encoder.encode(profileUpdateRequest.getPassword()));
        }
        if(profileUpdateRequest.getName() != null && !profileUpdateRequest.getName().equals("")){
            user.setName(profileUpdateRequest.getName());
        }

        // Update User
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User updated successfully!"));
    }

    @PostMapping("/profile/imageupload")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> uploadUserProfileImage(@RequestParam("file") MultipartFile file,
                                                    RedirectAttributes redirectAttributes) {

        return ResponseEntity.ok(new MessageResponse("Image uploaded!"));
    }
}
