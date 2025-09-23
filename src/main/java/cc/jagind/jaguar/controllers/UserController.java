package cc.jagind.jaguar.controllers;

import cc.jagind.jaguar.model.User;
import cc.jagind.jaguar.service.UserService;
import cc.jagind.jaguar.utils.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/id/{id}")
    public User getUser(@PathVariable long id) {
        return this.userService.getUserById(id);
    }

    @GetMapping("/get-data")
    public User getData(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.getUserEmailFromToken(token);

        return userService.getUserByEmail(email);
    }


    @PostMapping("/create")
    public void createUser(@RequestBody User user) throws Exception {
        String userEmail = user.getEmail();

        if (this.userService.getUserByEmail(userEmail) != null) {
            throw new Exception("User with that email already exists");
        }

        this.userService.saveUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return this.userService.getAllUsers();
    }
}
