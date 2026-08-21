package lk.ijse.eca.enrollmentservice.client;

import lk.ijse.eca.enrollmentservice.dto.StudentDto;
import lk.ijse.eca.enrollmentservice.exception.StudentServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@Slf4j
public class StudentServiceClient {

    private final RestClient restClient;

    public StudentServiceClient(@LoadBalanced  RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("http://STUDENT-SERVICE")
                .build();
    }

    public StudentDto getStudent(String studentId) {
        log.debug("Fetching student from Student-Service for ID: {}", studentId);
        try {
            return restClient.get()
                    .uri("/api/v1/students/{id}", studentId)
                    .retrieve()
                    .body(StudentDto.class);
        } catch (RestClientException e) {
            log.error("Failed to fetch student details for ID: {}", studentId, e);
            throw new StudentServiceException(
                    "Unable to retrieve student details for: " + studentId, e);
        }
    }
}
