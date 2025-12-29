package gov.uk.dts.task_api.service;

import gov.uk.dts.task_api.dto.TaskRequest;
import gov.uk.dts.task_api.dto.TaskResponse;
import gov.uk.dts.task_api.entity.TaskDao;
import gov.uk.dts.task_api.repository.TaskRepository;
import gov.uk.dts.task_api.utility.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(OutputCaptureExtension.class)
class TaskServiceImplTest {

    private final TaskRepository taskRepository = mock(TaskRepository.class);
    private final TaskServiceImpl underTest = new TaskServiceImpl(taskRepository);

    private static final LocalDateTime DUE_DATE_TIME = LocalDateTime.now().plusDays(2);
    private static final TaskDao TASK_DAO = new TaskDao(100L, "Test Task", "Task description", Status.IN_PROGRESS, DUE_DATE_TIME, null);
    private static final TaskRequest TASK_REQUEST = new TaskRequest("Test Task", "Task description", Status.IN_PROGRESS, DUE_DATE_TIME);

    @Test
    void createTaskTest(CapturedOutput output) {
        when(taskRepository.save(any(TaskDao.class))).thenReturn(TASK_DAO);
        var result = underTest.create(TASK_REQUEST);
        assertThat(result)
                .extracting(TaskResponse::getId, TaskResponse::getTitle, TaskResponse::getDescription, TaskResponse::getStatus, TaskResponse::getDueDateTime)
                .containsExactly(100L, TASK_REQUEST.getTitle(), TASK_REQUEST.getDescription(), TASK_REQUEST.getStatus(), TASK_REQUEST.getDueDateTime());
        assertThat(output.getOut()).contains("Task created successfully with id 100");
    }

    @Test
    void updateTaskTest(CapturedOutput output) {
        when(taskRepository.findById(100L)).thenReturn(Optional.of(TASK_DAO));
        when(taskRepository.save(any(TaskDao.class))).thenReturn(TASK_DAO);
        var result = underTest.update(TASK_REQUEST, 100L);
        assertThat(result.get())
                .extracting(TaskResponse::getId, TaskResponse::getTitle, TaskResponse::getDescription, TaskResponse::getStatus, TaskResponse::getDueDateTime)
                .containsExactly(100L, TASK_REQUEST.getTitle(), TASK_REQUEST.getDescription(), TASK_REQUEST.getStatus(), TASK_REQUEST.getDueDateTime());
        assertThat(output.getOut()).contains("Task updated successfully with id 100");
    }

    @Test
    void updateTaskTest_idNotExists() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        var result = underTest.update(TASK_REQUEST, 1L);
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    void getByIdTaskTest() {
        when(taskRepository.findById(100L)).thenReturn(Optional.of(TASK_DAO));
        var result = underTest.getByTaskId(100L);
        assertThat(result.get())
                .extracting(TaskResponse::getId, TaskResponse::getTitle, TaskResponse::getDescription, TaskResponse::getStatus, TaskResponse::getDueDateTime)
                .containsExactly(100L, TASK_REQUEST.getTitle(), TASK_REQUEST.getDescription(), TASK_REQUEST.getStatus(), TASK_REQUEST.getDueDateTime());
    }

    @Test
    void getByIdTaskTest_idNotExists() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        var result = underTest.getByTaskId(1L);
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    void getAllTaskTest() {
        when(taskRepository.findAll()).thenReturn(List.of(TASK_DAO, TASK_DAO, TASK_DAO, TASK_DAO));
        var result = underTest.getAll();
        assertThat(result.size()).isEqualTo(4);
        assertThat(result)
                .extracting(TaskResponse::getId)
                .containsExactly(100L, 100L, 100L, 100L);
    }

    @Test
    void deleteTaskTest(CapturedOutput output) {
        when(taskRepository.existsById(100L)).thenReturn(Boolean.TRUE);
        var result = underTest.delete(100L);
        assertThat(result).isTrue();
        assertThat(output.getOut()).contains("Task deleted successfully with id 100");
    }

    @Test
    void deleteTaskTest_idNotExists() {
        when(taskRepository.existsById(1L)).thenReturn(Boolean.FALSE);
        var result = underTest.delete(1L);
        assertThat(result).isFalse();
    }
}
