package cc.jagind.transaction.grpc;

import cc.jagind.grpc.TransactionProto;
import cc.jagind.grpc.TransactionServiceGrpc;
import cc.jagind.transaction.model.Transaction;
import cc.jagind.transaction.service.TransactionService;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

@Service
public class TransactionGrpcService extends TransactionServiceGrpc.TransactionServiceImplBase {

    private final TransactionService transactionService;

    public TransactionGrpcService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public void createTransaction(TransactionProto.CreateTransactionRequest request, StreamObserver<TransactionProto.CreateTransactionResponse> responseObserver) {
        try {
            long fromUserId = request.getFromUserId();
            long toUserId = request.getToUserId();
            double amount = request.getAmount();
            String description = request.getDescription();

            Transaction transaction = transactionService.createTransaction(fromUserId, toUserId, amount, description);

            TransactionProto.Transaction grpcTransaction = TransactionProto.Transaction.newBuilder()
                    .setId(transaction.getId())
                    .setTimestamp(transaction.getTimestamp())
                    .setAmount(transaction.getAmount())
                    .setDescription(transaction.getDescription() == null ? "" : transaction.getDescription())
                    .setFromUserId(transaction.getFromUserId())
                    .setToUserId(transaction.getToUserId())
                    .setResult(transaction.getResult() == Transaction.TransactionResult.APPROVED
                            ? TransactionProto.TransactionResult.APPROVED
                            : TransactionProto.TransactionResult.DECLINED)
                    .build();

            TransactionProto.CreateTransactionResponse response = TransactionProto.CreateTransactionResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Transaction created successfully")
                    .setTransaction(grpcTransaction)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            TransactionProto.CreateTransactionResponse response = TransactionProto.CreateTransactionResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(e.getMessage() == null ? "Unknown error" : e.getMessage())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}


