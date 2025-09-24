package cc.jagind.jaguar.service;

import cc.jagind.jaguar.model.Transaction;
import cc.jagind.jaguar.model.User;
import cc.jagind.jaguar.repository.TransactionRepository;
import cc.jagind.jaguar.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public Transaction createTransaction(long fromUserId, long toUserId, double amount) throws Exception {
        if (amount <= 0) {
            throw new Exception("Transaction amount must be positive");
        }

        Optional<User> fromUserOpt = userRepository.findById(fromUserId);
        Optional<User> toUserOpt = userRepository.findById(toUserId);

        if (fromUserOpt.isEmpty()) {
            throw new Exception("From user not found with ID: " + fromUserId);
        }

        if (toUserOpt.isEmpty()) {
            throw new Exception("To user not found with ID: " + toUserId);
        }

        if (fromUserId == toUserId) {
            throw new Exception("Cannot send transaction to yourself");
        }

        User fromUser = fromUserOpt.get();
        User toUser = toUserOpt.get();

        if (fromUser.getBalance() < amount) {
            throw new Exception("From user balance is too low");
        }

        Transaction transaction = new Transaction();
        transaction.setFromUser(fromUser);
        transaction.setToUser(toUser);
        transaction.setAmount(amount);
        transaction.setTimestamp(System.currentTimeMillis());

        fromUser.addSentTransaction(transaction);
        toUser.addReceivedTransaction(transaction);

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(long id) throws Exception {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        if (transaction.isEmpty()) {
            throw new Exception("Transaction not found with ID: " + id);
        }
        return transaction.get();
    }

    public List<Transaction> getTransactionsByUser(long userId) throws Exception {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new Exception("User not found with ID: " + userId);
        }
        return transactionRepository.findByUser(userOpt.get());
    }

    public List<Transaction> getTransactionsSentByUser(long userId) throws Exception {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new Exception("User not found with ID: " + userId);
        }
        return transactionRepository.findByFromUserOrderByTimestampDesc(userOpt.get());
    }

    public List<Transaction> getTransactionsReceivedByUser(long userId) throws Exception {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new Exception("User not found with ID: " + userId);
        }
        return transactionRepository.findByToUserOrderByTimestampDesc(userOpt.get());
    }

    public List<Transaction> getTransactionsBetweenUsers(long userId1, long userId2) throws Exception {
        Optional<User> user1Opt = userRepository.findById(userId1);
        Optional<User> user2Opt = userRepository.findById(userId2);

        if (user1Opt.isEmpty()) {
            throw new Exception("User not found with ID: " + userId1);
        }

        if (user2Opt.isEmpty()) {
            throw new Exception("User not found with ID: " + userId2);
        }

        return transactionRepository.findTransactionsBetweenUsers(user1Opt.get(), user2Opt.get());
    }

    public List<Transaction> getRecentTransactions() {
        return transactionRepository.findTop10ByOrderByTimestampDesc();
    }

    public void deleteTransaction(long id) throws Exception {
        if (!transactionRepository.existsById(id)) {
            throw new Exception("Transaction not found with ID: " + id);
        }
        transactionRepository.deleteById(id);
    }

    public List<Transaction> getTransactionsByAmountRange(double minAmount, double maxAmount) throws Exception {
        if (minAmount < 0 || maxAmount < 0) {
            throw new Exception("Amount values must be non-negative");
        }
        if (minAmount > maxAmount) {
            throw new Exception("Minimum amount cannot be greater than maximum amount");
        }
        return transactionRepository.findByAmountBetweenOrderByTimestampDesc(minAmount, maxAmount);
    }
}
