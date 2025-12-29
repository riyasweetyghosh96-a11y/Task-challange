package gov.uk.dts.task_api.service;

import gov.uk.dts.task_api.dto.TaskRequest;
import gov.uk.dts.task_api.dto.TaskResponse;

import java.util.List;
import java.util.Optional;

public interface TaskService {

    TaskResponse create(TaskRequest taskRequest);
    List<TaskResponse> getAll();
    Optional<TaskResponse> getByTaskId(Long taskId);
    Optional<TaskResponse> update(TaskRequest taskRequest, Long id);
    Boolean delete(Long taskId);
}
