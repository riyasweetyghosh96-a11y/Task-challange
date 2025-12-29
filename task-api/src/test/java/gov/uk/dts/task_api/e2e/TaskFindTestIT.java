package gov.uk.dts.task_api.e2e;

import gov.uk.dts.task_api.dto.TaskResponse;
import gov.uk.dts.task_api.entity.TaskDao;
import gov.uk.dts.task_api.repository.TaskRepository;
import gov.uk.dts.task_api.utility.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TaskFindTestIT {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private TaskRepository taskRepository;

    private static final LocalDateTime DUE_DATE_TIME = LocalDateTime.now().plusDays(2);

    @BeforeEach
    void setUp() {
        List<TaskDao> daoList = List.of(new TaskDao(null, "Test Task - 1", "Task description - 1", Status.IN_PROGRESS, DUE_DATE_TIME, null),
                new TaskDao(null, "Test Task - 2", "Task description", Status.CREATED, DUE_DATE_TIME, null),
                new TaskDao(null, "Test Task - 3", "Task description", Status.COMPLETED, DUE_DATE_TIME, null),
                new TaskDao(null, "Test Task - 4", "Task description", Status.CANCELLED, DUE_DATE_TIME, null));
        taskRepository.saveAll(daoList);
    }

    @Test
    void findTask_shouldReturnAll() {

        var response = rest.getForEntity("/task/v1/find-all", List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        assertThat(response.getBody())
                .extracting("title")
                        .containsExactly("Test Task - 1", "Test Task - 2", "Test Task - 3", "Test Task - 4");
    }

    @Test
    void findByIdTask_shouldReturnMatched() {

        var response = rest.getForEntity("/task/v1/find/1", TaskResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        assertThat(response.getBody())
                .extracting(TaskResponse::getId, TaskResponse::getTitle, TaskResponse::getDescription, TaskResponse::getStatus, TaskResponse::getDueDateTime)
                .containsExactly(1L, "Test Task - 1", "Task description - 1", Status.IN_PROGRESS, DUE_DATE_TIME);
    }

    @Test
    void findByIdTask_shouldGiveNotFoundError() {

        var response = rest.getForEntity("/task/v1/find/100", TaskResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }
}
