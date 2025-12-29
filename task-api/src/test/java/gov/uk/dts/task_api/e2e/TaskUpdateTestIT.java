package gov.uk.dts.task_api.e2e;

import gov.uk.dts.task_api.dto.TaskRequest;
import gov.uk.dts.task_api.dto.TaskResponse;
import gov.uk.dts.task_api.entity.TaskDao;
import gov.uk.dts.task_api.repository.TaskRepository;
import gov.uk.dts.task_api.utility.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TaskUpdateTestIT {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private TaskRepository taskRepository;

    private static final LocalDateTime DUE_DATE_TIME = LocalDateTime.now().plusDays(2);

    @BeforeEach
    void setUp() {
        TaskDao dao = new TaskDao(null, "Test Task", "Task description", Status.IN_PROGRESS, DUE_DATE_TIME, null);
        taskRepository.save(dao);
    }

    @Test
    void updateTask_shouldPersist() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Updated Test Task");
        request.setDescription("Updated Task description");
        request.setStatus(Status.COMPLETED);
        request.setDueDateTime(DUE_DATE_TIME.plusDays(2));
        HttpEntity<TaskRequest> httpEntity = new HttpEntity<>(request);

        var response = rest.exchange("/task/v1/update/1", HttpMethod.PUT, httpEntity, TaskResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        assertThat(response.getBody())
                .extracting(TaskResponse::getId, TaskResponse::getTitle, TaskResponse::getDescription, TaskResponse::getStatus, TaskResponse::getDueDateTime)
                        .containsExactly(1L, request.getTitle(), request.getDescription(), request.getStatus(), request.getDueDateTime());

        var tasks = taskRepository.findAll();
        assertThat(tasks.size()).isEqualTo(1);
        assertThat(tasks.get(0))
                .extracting(TaskDao::getId, TaskDao::getTitle, TaskDao::getDescription, TaskDao::getStatus, TaskDao::getDueDateTime)
                .containsExactly(1L, request.getTitle(), request.getDescription(), request.getStatus(), request.getDueDateTime());
    }

    @Test
    void updateTask_shouldGiveNotFoundError() {

        TaskRequest request = new TaskRequest();
        request.setTitle("Updated Test Task");
        request.setDescription("Updated Task description");
        request.setStatus(Status.COMPLETED);
        request.setDueDateTime(DUE_DATE_TIME);
        HttpEntity<TaskRequest> httpEntity = new HttpEntity<>(request);

        var response = rest.exchange("/task/v1/update/100", HttpMethod.PUT, httpEntity, TaskResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();

    }
}
