package cc.jagind.transaction.service;

import cc.jagind.grpc.UserProto;
import cc.jagind.grpc.UserServiceGrpc;
import cc.jagind.transaction.model.Transaction;
import cc.jagind.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserServiceGrpc.UserServiceBlockingStub userStub;

    public TransactionService(TransactionRepository transactionRepository, UserServiceGrpc.UserServiceBlockingStub userStub) {
        this.transactionRepository = transactionRepository;
        this.userStub = userStub;
    }

    public Transaction.TransactionResult processTransaction(long fromUserId, long toUserId, double transactionAmount) {
        if (fromUserId == 0) {
            UserProto.AddResponse addResponse = userStub.addBalance(
                    UserProto.AddRequest.newBuilder()
                            .setUserId(toUserId)
                            .setAmount(transactionAmount)
                            .build()
            );

            if (addResponse.getSuccess()) {
                return Transaction.TransactionResult.APPROVED;
            }
            return Transaction.TransactionResult.DECLINED;
        }

        UserProto.BalanceResponse balanceResponse = userStub.getBalance(
                UserProto.UserIdRequest.newBuilder().setUserId(fromUserId).build()
        );

        double fromUserBalance = balanceResponse.getBalance();

        if (fromUserBalance >= transactionAmount) {
            UserProto.DeductResponse deductResponse = userStub.deductBalance(
                    UserProto.DeductRequest.newBuilder()
                            .setUserId(fromUserId)
                            .setAmount(transactionAmount)
                            .build()
            );

            if (deductResponse.getSuccess()) {
                UserProto.AddResponse addResponse = userStub.addBalance(
                        UserProto.AddRequest.newBuilder()
                                .setUserId(toUserId)
                                .setAmount(transactionAmount)
                                .build()
                );

                if (addResponse.getSuccess()) {
                    return Transaction.TransactionResult.APPROVED;
                }
            }
        }

        return Transaction.TransactionResult.DECLINED;
    }

    public Transaction createTransaction(long fromUserId, long toUserId, double amount, String description) throws Exception {
        if (amount <= 0) {
            throw new Exception("Transaction amount must be positive");
        }

        if (fromUserId == toUserId) {
            throw new Exception("Cannot send transaction to yourself");
        }

        Transaction.TransactionResult result = processTransaction(fromUserId, toUserId, amount);

        // TODO: Push to Kafka asynchronously for email notification

        if (result == Transaction.TransactionResult.DECLINED) {
            // TODO: Run decline logic
        }

        Transaction transaction = new Transaction();
        transaction.setFromUserId(fromUserId);
        transaction.setToUserId(toUserId);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setResult(result);

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

    public List<Transaction> getTransactionsFromUser(long userId) {
        return transactionRepository.findByFromUserId(userId);
    }

    public List<Transaction> getTransactionsToUser(long userId) {
        return transactionRepository.findByToUserId(userId);
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

    public void deleteAllTransactions() {
        transactionRepository.deleteAll();
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

