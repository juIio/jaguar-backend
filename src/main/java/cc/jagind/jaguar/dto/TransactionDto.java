package cc.jagind.jaguar.dto;

import cc.jagind.jaguar.model.Transaction;

public record TransactionDto(
        long id,
        long timestamp,
        double amount,
        String description,
        long fromUserId,
        String fromUserName,
        long toUserId,
        String toUserName
) {
    public TransactionDto(Transaction transaction) {
        this(
                transaction.getId(),
                transaction.getTimestamp(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getFromUser() == null ? -1 : transaction.getFromUser().getId(),
                transaction.getFromUser() == null ? "JAGUAR FINANCIAL" : transaction.getFromUser().getFullName(),
                transaction.getToUser().getId(),
                transaction.getToUser().getFullName()
        );
    }
}
