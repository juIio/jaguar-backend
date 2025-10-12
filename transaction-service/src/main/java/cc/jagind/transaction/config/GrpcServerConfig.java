package cc.jagind.transaction.config;

import cc.jagind.transaction.grpc.TransactionGrpcService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@Configuration
public class GrpcServerConfig {

    @Value("${grpc.server.port:9091}")
    private int grpcPort;

    private final TransactionGrpcService transactionGrpcService;

    private Server grpcServer;

    public GrpcServerConfig(TransactionGrpcService transactionGrpcService) {
        this.transactionGrpcService = transactionGrpcService;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void startGrpcServer() throws Exception {
        grpcServer = ServerBuilder.forPort(grpcPort)
                .addService(transactionGrpcService)
                .build()
                .start();
        System.out.println("[Transaction Service] gRPC server started on port " + grpcPort);
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


