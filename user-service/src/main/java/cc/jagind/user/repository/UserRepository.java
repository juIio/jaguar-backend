package cc.jagind.user.repository;

import cc.jagind.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u.balance FROM User u WHERE u.id = :id")
    Double getBalanceById(@Param("id") long id);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.balance = u.balance - :amount WHERE u.id = :id AND u.balance >= :amount")
    int deductBalanceById(@Param("id") long id, @Param("amount") double amount);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.balance = u.balance + :amount WHERE u.id = :id")
    int addBalanceById(@Param("id") long id, @Param("amount") double amount);
}
