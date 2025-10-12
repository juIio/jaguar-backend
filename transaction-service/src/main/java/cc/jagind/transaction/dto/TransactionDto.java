package cc.jagind.transaction.dto;

import cc.jagind.transaction.model.Transaction;

public record TransactionDto(
        long id,
        long timestamp,
        double amount,
        String description,
        long fromUserId,
        long toUserId,
        String fromUserEmail,
        String toUserEmail
) {
    public TransactionDto(Transaction transaction, String fromUserEmail, String toUserEmail) {
        this(
                transaction.getId(),
                transaction.getTimestamp(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getFromUserId(),
                transaction.getToUserId(),
                fromUserEmail,
                toUserEmail
        );
    }
}
