package gov.uk.dts.task_api.e2e;

import gov.uk.dts.task_api.entity.TaskDao;
import gov.uk.dts.task_api.repository.TaskRepository;
import gov.uk.dts.task_api.utility.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TaskDeleteTestIT {

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
    void deleteTask_shouldSuccess() {

        var response = rest.exchange("/task/v1/delete/1", HttpMethod.DELETE, null, Boolean.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isTrue();

        var tasks = taskRepository.findAll();
        assertThat(tasks.size()).isEqualTo(0);
    }

    @Test
    void deleteTask_shouldGiveNotFoundError() {

        var response = rest.exchange("/task/v1/delete/200", HttpMethod.DELETE, null, Boolean.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isFalse();

    }
}
