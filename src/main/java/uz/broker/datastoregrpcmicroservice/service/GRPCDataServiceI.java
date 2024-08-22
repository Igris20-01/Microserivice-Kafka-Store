package uz.broker.datastoregrpcmicroservice.service;

import com.example.grpccommon.GRPCData;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.grpccommon.AnalyticsServerGrpc;
import com.example.grpccommon.GRPCAnalyticsRequest;
import jakarta.annotation.PostConstruct;
import uz.broker.datastoregrpcmicroservice.model.Data;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class GRPCDataServiceI implements GRPCDataService {

    private final ScheduledExecutorService executorService
            = Executors.newSingleThreadScheduledExecutor();

    private final SummaryService summaryService;

    @GrpcClient(value = "dara-store-async")
    private AnalyticsServerGrpc.AnalyticsServerStub asyncStub;

    @Value("${fetch.batch-size}")
    private int batchSize;

    @PostConstruct
    public void init() {
        fetchMessage();
    }


    @Override
    public void fetchMessage() {

        executorService.scheduleAtFixedRate(
                () -> asyncStub.askForData(
                        GRPCAnalyticsRequest.newBuilder()
                                .setBatchSize(batchSize)
                                .build(),
                        new StreamObserver<>() {
                            @Override
                            public void onNext(GRPCData data) {
                                summaryService.handle(new Data(data));
                            }

                            @Override
                            public void onError(Throwable throwable) {

                            }

                            @Override
                            public void onCompleted() {
                                log.info("Batch was handled");
                            }
                        }
                ),
                0,
                10,
                TimeUnit.SECONDS
        );

    }
}
