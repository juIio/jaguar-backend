package cc.jagind.user.config;

import cc.jagind.grpc.TransactionServiceGrpc;
import cc.jagind.user.grpc.UserGrpcService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import jakarta.annotation.PreDestroy;

import java.io.IOException;

@Configuration
public class GrpcServerConfig {

    @Value("${grpc.server.port:9090}")
    private int grpcPort;

    @Value("${grpc.transaction.service.host:localhost}")
    private String transactionServiceHost;

    @Value("${grpc.transaction.service.port:9091}")
    private int transactionServicePort;

    private Server grpcServer;
    private final UserGrpcService userGrpcService;

    public GrpcServerConfig(UserGrpcService userGrpcService) {
        this.userGrpcService = userGrpcService;
    }

    @Bean
    public ManagedChannel transactionServiceChannel() {
        return ManagedChannelBuilder.forAddress(transactionServiceHost, transactionServicePort)
                .usePlaintext()
                .build();
    }

    @Bean
    public TransactionServiceGrpc.TransactionServiceBlockingStub transactionServiceBlockingStub(ManagedChannel transactionServiceChannel) {
        return TransactionServiceGrpc.newBlockingStub(transactionServiceChannel);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void startGrpcServer() throws IOException {
        grpcServer = ServerBuilder.forPort(grpcPort)
                .addService(userGrpcService)
                .build()
                .start();

        System.out.println("[User Service] gRPC server started on port " + grpcPort);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (grpcServer != null) {
                grpcServer.shutdown();
            }
        }));
    }

    @PreDestroy
    public void stopGrpcServer() {
        if (grpcServer != null) {
            grpcServer.shutdown();
        }
    }
}
