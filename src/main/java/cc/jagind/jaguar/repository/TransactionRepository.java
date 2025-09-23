package cc.jagind.jaguar.repository;

import cc.jagind.jaguar.model.Transaction;
import cc.jagind.jaguar.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.fromUser = :user OR t.toUser = :user ORDER BY t.timestamp DESC")
    List<Transaction> findByUser(@Param("user") User user);

    List<Transaction> findByFromUserOrderByTimestampDesc(User fromUser);

    List<Transaction> findByToUserOrderByTimestampDesc(User toUser);

    @Query("SELECT t FROM Transaction t WHERE (t.fromUser = :user1 AND t.toUser = :user2) OR (t.fromUser = :user2 AND t.toUser = :user1) ORDER BY t.timestamp DESC")
    List<Transaction> findTransactionsBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);

    List<Transaction> findByAmountBetweenOrderByTimestampDesc(double minAmount, double maxAmount);

    List<Transaction> findTop10ByOrderByTimestampDesc();
}
