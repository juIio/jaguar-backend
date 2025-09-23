package cc.jagind.jaguar;

import cc.jagind.jaguar.controllers.TransactionController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TransactionTests {

    private MockMvc mockMvc;

    @InjectMocks
    private TransactionController transactionController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testSendTransaction() throws Exception {
        Map<String, Object> transactionData = new HashMap<>();
        transactionData.put("fromUserId", 1);
        transactionData.put("toUserId", 2);
        transactionData.put("amount", 99.99);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionData)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Transaction created successfully"))
                .andExpect(jsonPath("$.transaction").exists())
                .andExpect(jsonPath("$.transaction.amount").value(100.50))
                .andExpect(jsonPath("$.transaction.fromUser").exists())
                .andExpect(jsonPath("$.transaction.toUser").exists())
                .andExpect(jsonPath("$.transaction.timestamp").exists());
    }
}
