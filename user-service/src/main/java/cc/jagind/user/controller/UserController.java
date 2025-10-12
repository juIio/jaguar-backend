package cc.jagind.user.controller;

import cc.jagind.commons.utils.JwtUtil;
import cc.jagind.user.dto.UserDashboardPageDto;
import cc.jagind.user.model.User;
import cc.jagind.user.service.UserService;
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

    @GetMapping("/{id}")
    public User getUser(@PathVariable long id) {
        return this.userService.getUserById(id);
    }

    @GetMapping("/dashboard")
    public UserDashboardPageDto getDashboardData(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        long userId = jwtUtil.getUserIdFromToken(token);
        User user = userService.getUserById(userId);

        if (user == null) {
            throw new RuntimeException("User not found from auth token");
        }

        return new UserDashboardPageDto(user);
    }

    @PostMapping
    public void createUser(@RequestBody User user) throws Exception {
        String userEmail = user.getEmail();

        if (this.userService.getUserByEmail(userEmail) != null) {
            throw new Exception("User with that email already exists");
        }

        this.userService.saveUser(user);
    }

    @GetMapping("/balance")
    public double getBalance(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        long userId = jwtUtil.getUserIdFromToken(token);
        User user = userService.getUserById(userId);

        if (user == null) {
            throw new RuntimeException("User not found from auth token");
        }

        return user.getBalance();
    }

    @GetMapping
    public List<User> getAllUsers() {
        return this.userService.getAllUsers();
    }

    @DeleteMapping
    public void deleteAllUsers() {
        this.userService.deleteAllUsers();
    }
}
