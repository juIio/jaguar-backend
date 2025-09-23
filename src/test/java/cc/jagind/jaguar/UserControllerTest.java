package cc.jagind.jaguar;

import cc.jagind.jaguar.controllers.UserController;
import cc.jagind.jaguar.model.User;
import cc.jagind.jaguar.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetAllUsers() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        user1.setFirstName("Andrew");
        user1.setLastName("Peterson");
        user1.setEmail("a.peterson@gmail.com");
        user1.setBalance(1000.50);
        user1.setAccountNumber("1234567890");
        user1.setRoutingNumber("987654321");
        user1.setVerifiedAt(System.currentTimeMillis());

        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("Jacob");
        user2.setLastName("Smith");
        user2.setEmail("j.smith@yahoo.com");
        user2.setBalance(2500.75);
        user2.setAccountNumber("0987654321");
        user2.setRoutingNumber("123456789");
        user2.setVerifiedAt(System.currentTimeMillis());

        List<User> mockUsers = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(mockUsers);

        MvcResult result = mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andExpect(jsonPath("$[1].lastName").value("Smith"))
                .andExpect(jsonPath("$[1].email").value("jane.smith@example.com"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        List<User> users = objectMapper.readValue(responseContent,
                objectMapper.getTypeFactory().constructCollectionType(List.class, User.class));

        System.out.println("=== LIST OF ALL USERS ===");
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            System.out.printf("User %d:%n", i + 1);
            System.out.printf("  ID: %d%n", user.getId());
            System.out.printf("  Name: %s %s%n", user.getFirstName(), user.getLastName());
            System.out.printf("  Email: %s%n", user.getEmail());
            System.out.printf("  Balance: $%.2f%n", user.getBalance());
            System.out.printf("  Account Number: %s%n", user.getAccountNumber());
            System.out.printf("  Routing Number: %s%n", user.getRoutingNumber());
            System.out.println("  ---");
        }
        System.out.printf("Total users found: %d%n", users.size());
        System.out.println("=== END OF USER LIST ===");
    }
}
