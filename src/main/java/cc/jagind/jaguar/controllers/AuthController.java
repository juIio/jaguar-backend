package cc.jagind.jaguar.controllers;

import cc.jagind.jaguar.model.User;
import cc.jagind.jaguar.service.UserService;
import cc.jagind.jaguar.utils.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (userService.getUserByEmail(user.getEmail()) != null) {
                response.put("success", false);
                response.put("message", "User with this email already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            // TODO: Send verification email, set them verified when they confirm it
            user.setVerifiedAt(System.currentTimeMillis());

            // TODO: Generate account/routing numbers
            user.setAccountNumber(990000042);
            user.setRoutingNumber(987654321);

            // We are a very generous bank
            user.setBalance(25000.50);

            userService.saveUser(user);

            // TODO: Return a UserDto rather than the whole User

            response.put("success", true);
            response.put("message", "Registration successful");
            response.put("user", user);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<Map<String, Object>> signInUser(@RequestBody Map<String, String> credentials) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = credentials.get("email");
            String password = credentials.get("password");

            User user = userService.authenticateUser(email, password);

            if (user != null) {
                response.put("success", true);
                response.put("message", "Successful login");
                response.put("token", jwtUtil.createTokenFromEmail(user.getEmail()));
                response.put("user", user);

                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Invalid email or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Sign in failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
