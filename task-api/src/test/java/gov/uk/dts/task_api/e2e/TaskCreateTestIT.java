package gov.uk.dts.task_api.e2e;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.dts.task_api.dto.TaskRequest;
import gov.uk.dts.task_api.dto.TaskResponse;
import gov.uk.dts.task_api.entity.TaskDao;
import gov.uk.dts.task_api.repository.TaskRepository;
import gov.uk.dts.task_api.utility.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TaskCreateTestIT {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final LocalDateTime DUE_DATE_TIME = LocalDateTime.now().plusDays(2);

    @Test
    void createTask_shouldPersist() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Test Task");
        request.setDescription("Task description");
        request.setStatus(Status.CREATED);
        request.setDueDateTime(DUE_DATE_TIME);

        var response = rest.postForEntity("/task/v1/create", request, TaskResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();

        assertThat(response.getBody())
                .extracting(TaskResponse::getTitle, TaskResponse::getDescription, TaskResponse::getStatus, TaskResponse::getDueDateTime)
                        .containsExactly(request.getTitle(), request.getDescription(), request.getStatus(), request.getDueDateTime());

        var tasks = taskRepository.findAll();
        assertThat(tasks.size()).isEqualTo(1);
        assertThat(tasks.getFirst())
                .extracting(TaskDao::getTitle, TaskDao::getDescription, TaskDao::getStatus, TaskDao::getDueDateTime)
                .containsExactly(request.getTitle(), request.getDescription(), request.getStatus(), request.getDueDateTime());
    }

    @ParameterizedTest
    @MethodSource("provideTaskRequest")
    void createTask_shouldGiveValidationError(String request, List<Map<String, String>> expectedErrors) throws JsonProcessingException {

        var json = objectMapper.readValue(request, JsonNode.class);
        var response = rest.postForEntity("/task/v1/create", json, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        List<Map<String, String>> errors = (List<Map<String, String>>) body.get("errors");
        assertThat(errors.size()).isEqualTo(expectedErrors.size());
        assertThat(errors).isNotNull();
        errors.forEach(error -> {
            var match = expectedErrors.stream().anyMatch(e -> e.get("field").equals(error.get("field")) && e.get("message").equals(error.get("message")));
            assertThat(match).isTrue();
        });

        var tasks = taskRepository.findAll();
        assertThat(tasks.size()).isEqualTo(0);
    }

    static Stream<Arguments> provideTaskRequest() {
        return Stream.of(
                Arguments.of("{\"title\":null,\"description\":null,\"status\":null,\"dueDateTime\":null}",
                        List.of(Map.of("field", "title", "message", "Title is required"),
                                Map.of("field", "status", "message", "Status is required"),
                                Map.of("field", "dueDateTime", "message", "DueDateTime is required")),
                        "Provide null data for non-null field"),
                Arguments.of("{\"title\":\"Test Task\",\"description\":\"Task description\",\"status\":\"invalid\",\"dueDateTime\":\""+DUE_DATE_TIME+"\"}",
                        List.of(Map.of("field", "status", "message", "Invalid value. Allowed values: [CREATED, IN_PROGRESS, CANCELLED, COMPLETED]")),
                        "Provide invalid status enum"),
                Arguments.of("{\"title\":\"Test Task\",\"description\":\"Task description\",\"status\":\"IN_PROGRESS\",\"dueDateTime\":\"2025-11-30T22:35:07.642817\"}",
                        List.of(Map.of("field", "dueDateTime", "message", "Due date/time must be in the future")),
                        "Provide past dated due date")
        );
    }

}
