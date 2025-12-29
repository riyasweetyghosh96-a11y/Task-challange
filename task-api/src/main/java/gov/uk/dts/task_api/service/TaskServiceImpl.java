package gov.uk.dts.task_api.service;

import gov.uk.dts.task_api.dto.TaskRequest;
import gov.uk.dts.task_api.dto.TaskResponse;
import gov.uk.dts.task_api.entity.TaskDao;
import gov.uk.dts.task_api.repository.TaskRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    @NonNull private final TaskRepository taskRepository;

    /**
     * Create a new task in db
     *
     * @param taskRequest - request received from UI
     * @return - response object with new task id
     */
    @Override
    public TaskResponse create(TaskRequest taskRequest) {
        var taskDao = taskRepository.save(new TaskDao(null, taskRequest.getTitle(), taskRequest.getDescription(), taskRequest.getStatus(), taskRequest.getDueDateTime(), null));
        log.info("Task created successfully with id {}", taskDao.getId());
        return TaskResponse.builder()
                .id(taskDao.getId())
                .title(taskDao.getTitle())
                .description(taskDao.getDescription())
                .status(taskDao.getStatus())
                .dueDateTime(taskDao.getDueDateTime())
                .build();
    }

    /**
     * Get all tasks from db
     *
     * @return - List of task object
     */
    @Override
    public List<TaskResponse> getAll() {
        return taskRepository.findAll().stream().map(res -> TaskResponse.builder()
                .id(res.getId())
                .title(res.getTitle())
                .description(res.getDescription())
                .status(res.getStatus())
                .dueDateTime(res.getDueDateTime())
                .build()).toList();
    }

    /**
     * Find task from db for a task id
     *
     * @param taskId - received from UI
     * @return - task object if found or empty
     */
    @Override
    public Optional<TaskResponse> getByTaskId(Long taskId) {
        return taskRepository.findById(taskId).map(dao -> TaskResponse.builder()
                .id(dao.getId())
                .title(dao.getTitle())
                .description(dao.getDescription())
                .status(dao.getStatus())
                .dueDateTime(dao.getDueDateTime())
                .build());
    }

    /**
     * Update existing task in db for a task id
     *
     * @param taskRequest - request received from UI to update
     * @param id - task id to update
     * @return - updated task object if found or empty
     */
    @Override
    public Optional<TaskResponse> update(TaskRequest taskRequest, Long id) {
        return taskRepository.findById(id)
                .map(dao -> {
                    dao.setTitle(taskRequest.getTitle());
                    dao.setDescription(taskRequest.getDescription());
                    dao.setStatus(taskRequest.getStatus());
                    dao.setDueDateTime(taskRequest.getDueDateTime());
                    dao.setUpdatedAt(LocalDateTime.now());
                    var updatedTaskDao = taskRepository.save(dao);
                    log.info("Task updated successfully with id {}", updatedTaskDao.getId());
                    return Optional.of(TaskResponse.builder()
                            .id(updatedTaskDao.getId())
                            .title(updatedTaskDao.getTitle())
                            .description(updatedTaskDao.getDescription())
                            .status(updatedTaskDao.getStatus())
                            .dueDateTime(updatedTaskDao.getDueDateTime())
                            .build());
                }).orElse(Optional.empty());
    }

    /**
     * Delete a task from db
     *
     * @param taskId - received from UI
     * @return - true if successfully deleted else false
     */
    @Override
    public Boolean delete(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            return Boolean.FALSE;
        }

        taskRepository.deleteById(taskId);
        log.info("Task deleted successfully with id {}", taskId);
        return Boolean.TRUE;
    }
}
