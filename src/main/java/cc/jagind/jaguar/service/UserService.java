package cc.jagind.jaguar.service;

import cc.jagind.jaguar.model.User;
import cc.jagind.jaguar.repository.UserRepository;
import cc.jagind.jaguar.utils.PasswordUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;

    public UserService(UserRepository userRepository, PasswordUtil passwordUtil) {
        this.userRepository = userRepository;
        this.passwordUtil = passwordUtil;
    }

    public User getUserById(long id) {
        return this.userRepository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        return this.userRepository.findByEmail(email).orElse(null);
    }

    public void saveUser(User user) {
        String password = user.getPassword();

        if (password != null && !passwordUtil.isPasswordEncrypted(password)) {
            user.setPassword(passwordUtil.encryptPassword(password));
        }

        this.userRepository.save(user);
    }

    public List<User> getAllUsers() {
        Iterable<User> iterableUsers = userRepository.findAll();
        List<User> userList = new ArrayList<>();
        iterableUsers.forEach(userList::add);

        return List.copyOf(userList);
    }

    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    public User authenticateUser(String email, String password) {
        User user = this.getUserByEmail(email);

        if (user != null && passwordUtil.verifyPassword(password, user.getPassword())) {
            return user;
        }

        return null;
    }

    public boolean emailExists(String email) {
        return getUserByEmail(email) != null;
    }
}
