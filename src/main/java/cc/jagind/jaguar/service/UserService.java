package cc.jagind.jaguar.service;

import cc.jagind.jaguar.model.User;
import cc.jagind.jaguar.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(long id) {
        return this.userRepository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        return this.userRepository.findByEmail(email).orElse(null);
    }

    public void saveUser(User user) {
        this.userRepository.save(user);
    }

    public List<User> getAllUsers() {
        Iterable<User> iterableUsers = userRepository.findAll();
        List<User> userList = new ArrayList<>();
        iterableUsers.forEach(userList::add);

        return List.copyOf(userList);
    }

    public User authenticateUser(String email, String password) {
        User user = this.getUserByEmail(email);

        // TODO: Password encryption
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }

        return null;
    }

    public boolean emailExists(String email) {
        return getUserByEmail(email) != null;
    }
}
