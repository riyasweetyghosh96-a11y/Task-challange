package gov.uk.dts.task_api.controller;

import gov.uk.dts.task_api.dto.TaskRequest;
import gov.uk.dts.task_api.dto.TaskResponse;
import gov.uk.dts.task_api.handler.ValidationError;
import gov.uk.dts.task_api.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/task/v1")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(value = "localhost:3100")
public class TaskController {

    @NonNull private final TaskService taskService;

    @Operation(summary = "Create a new task")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task created"),
            @ApiResponse(responseCode = "400",
                    description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ValidationError.class))),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest taskRequest) {
        try {
         TaskResponse taskResponse = taskService.create(taskRequest);
         return new ResponseEntity<>(taskResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("error creating task: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Update a task using task id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task updated"),
            @ApiResponse(responseCode = "400",
                    description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ValidationError.class))),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @PutMapping(value = "/update/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskResponse> update(@Valid @RequestBody TaskRequest taskRequest, @PathVariable Long id) {
        try {
            Optional<TaskResponse> taskResponse = taskService.update(taskRequest, id);
            return taskResponse.map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            log.error("error updating task with id: {} error: {}", id, e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Find a task using task id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task found"),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @GetMapping(value = "/find/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskResponse> find(@PathVariable Long id) {
        try {
            Optional<TaskResponse> taskResponse = taskService.getByTaskId(id);
            return taskResponse.map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            log.error("error fetching task with id: {} error: {}", id, e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Find all tasks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task list"),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @GetMapping(value = "/find-all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TaskResponse>> findAll() {
        try {
            List<TaskResponse> taskResponse = taskService.getAll();
            return new ResponseEntity<>(taskResponse, HttpStatus.OK);
        } catch (Exception e) {
            log.error("error fetching all tasks error: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Delete a task using task id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task deleted",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class),
                            examples = @ExampleObject(value = "true")
                    )),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class),
                            examples = @ExampleObject(value = "false")
                    )),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class),
                            examples = @ExampleObject(value = "false")
                    ))
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        try {
            Boolean isDeleted = taskService.delete(id);
            if(isDeleted) {
                return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
            }
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("error deleting task with id: {} error: {}", id, e.getMessage());
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
