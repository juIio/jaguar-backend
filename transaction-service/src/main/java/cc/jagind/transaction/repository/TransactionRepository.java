package cc.jagind.transaction.repository;

import cc.jagind.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByFromUserId(@Param("userId") long userId);

    List<Transaction> findByToUserId(@Param("userId") long userId);

    List<Transaction> findByAmountBetweenOrderByTimestampDesc(double minAmount, double maxAmount);

    List<Transaction> findTop10ByOrderByTimestampDesc();
}

