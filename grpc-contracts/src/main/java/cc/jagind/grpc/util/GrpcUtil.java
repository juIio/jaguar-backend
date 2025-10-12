package cc.jagind.grpc.util;

import cc.jagind.grpc.UserProto;
import cc.jagind.grpc.UserServiceGrpc;

public class GrpcUtil {

    public static String getEmailByUserId(UserServiceGrpc.UserServiceBlockingStub userServiceStub, long userId) throws Exception {
        UserProto.UserIdRequest emailRequest = UserProto.UserIdRequest.newBuilder()
                .setUserId(userId)
                .build();

        UserProto.EmailResponse emailResponse = userServiceStub.getEmailByUserId(emailRequest);

        if (!emailResponse.getFound()) {
            throw new Exception("User not found with ID: " + userId);
        }

        return emailResponse.getEmail();
    }
}

