package lk.ijse.eca.enrollmentservice.client;

import lk.ijse.eca.enrollmentservice.exception.ProgramServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@Slf4j
public class ProgramServiceClient {

    private final RestClient restClient;

    public ProgramServiceClient(@LoadBalanced RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("http://PROGRAM-SERVICE")
                .build();
    }

    public void validateProgram(String programId) {
        log.debug("Validating program in Program-Service for ID: {}", programId);
        try {
            restClient.get()
                    .uri("/api/v1/programs/{id}", programId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Failed to validate program for ID: {}", programId, e);
            throw new ProgramServiceException(
                    "Unable to validate program: " + programId, e);
        }
    }
}
