package cc.jagind.user.grpc;

import cc.jagind.grpc.UserProto;
import cc.jagind.grpc.UserServiceGrpc;
import cc.jagind.user.model.User;
import cc.jagind.user.repository.UserRepository;
import cc.jagind.user.service.UserService;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

@Service
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private final UserRepository userRepository;
    private final UserService userService;

    public UserGrpcService(UserRepository userRepo, UserService userService) {
        this.userRepository = userRepo;
        this.userService = userService;
    }

    @Override
    public void getBalance(UserProto.UserIdRequest request, StreamObserver<UserProto.BalanceResponse> responseObserver) {
        Double balance = userRepository.getBalanceById(request.getUserId());
        double safeBalance = balance == null ? 0.0 : balance;

        responseObserver.onNext(UserProto.BalanceResponse.newBuilder().setBalance(safeBalance).build());
        responseObserver.onCompleted();
    }

    @Override
    public void deductBalance(UserProto.DeductRequest request, StreamObserver<UserProto.DeductResponse> responseObserver) {
        int affectedRows = userRepository.deductBalanceById(request.getUserId(), request.getAmount());
        boolean success = affectedRows > 0;

        responseObserver.onNext(UserProto.DeductResponse.newBuilder().setSuccess(success).build());
        responseObserver.onCompleted();
    }

    @Override
    public void addBalance(UserProto.AddRequest request, StreamObserver<UserProto.AddResponse> responseObserver) {
        int affectedRows = userRepository.addBalanceById(request.getUserId(), request.getAmount());
        boolean success = affectedRows > 0;

        responseObserver.onNext(UserProto.AddResponse.newBuilder().setSuccess(success).build());
        responseObserver.onCompleted();
    }

    @Override
    public void getUserIdByEmail(UserProto.EmailRequest request, StreamObserver<UserProto.UserIdResponse> responseObserver) {
        User user = userService.getUserByEmail(request.getEmail());
        
        UserProto.UserIdResponse.Builder responseBuilder = UserProto.UserIdResponse.newBuilder();
        
        if (user != null) {
            responseBuilder.setUserId(user.getId()).setFound(true);
        } else {
            responseBuilder.setUserId(0).setFound(false);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getEmailByUserId(UserProto.UserIdRequest request, StreamObserver<UserProto.EmailResponse> responseObserver) {
        User user = userService.getUserById(request.getUserId());
        
        UserProto.EmailResponse.Builder responseBuilder = UserProto.EmailResponse.newBuilder();
        
        if (user != null) {
            responseBuilder.setEmail(user.getEmail()).setFound(true);
        } else {
            responseBuilder.setEmail("").setFound(false);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

}
