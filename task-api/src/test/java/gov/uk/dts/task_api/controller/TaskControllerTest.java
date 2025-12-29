package gov.uk.dts.task_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.dts.task_api.dto.TaskRequest;
import gov.uk.dts.task_api.dto.TaskResponse;
import gov.uk.dts.task_api.utility.Status;
import gov.uk.dts.task_api.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    private static final LocalDateTime DUE_DATE_TIME = LocalDateTime.now().plusDays(2);

    @Test
    void createTask_success() throws Exception {
        TaskRequest request = new TaskRequest("Title", "Desc", Status.CREATED, DUE_DATE_TIME);
        TaskResponse response = TaskResponse.builder()
                .id(1L)
                .title("Title")
                .description("Desc")
                .status(Status.CREATED)
                .dueDateTime(DUE_DATE_TIME)
                .build();

        Mockito.when(taskService.create(any(TaskRequest.class))).thenReturn(response);

        mockMvc.perform(post("/task/v1/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    void createTask_internalServerError() throws Exception {
        TaskRequest request = new TaskRequest("Title", "Desc", Status.CREATED, DUE_DATE_TIME);

        Mockito.when(taskService.create(any(TaskRequest.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(post("/task/v1/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateTask_success() throws Exception {
        TaskRequest request = new TaskRequest("Updated", "Desc", Status.IN_PROGRESS, DUE_DATE_TIME);
        TaskResponse response = TaskResponse.builder()
                .id(1L)
                .title("Updated")
                .description("Desc")
                .status(Status.IN_PROGRESS)
                .dueDateTime(DUE_DATE_TIME)
                .build();

        Mockito.when(taskService.update(any(TaskRequest.class), eq(1L)))
                .thenReturn(Optional.of(response));

        mockMvc.perform(put("/task/v1/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }

    @Test
    void updateTask_notFound() throws Exception {
        TaskRequest request = new TaskRequest("Updated", "Desc", Status.IN_PROGRESS, DUE_DATE_TIME);

        Mockito.when(taskService.update(any(TaskRequest.class), eq(1L)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/task/v1/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void findTask_success() throws Exception {
        TaskResponse response = TaskResponse.builder()
                .id(1L)
                .title("Title")
                .description("Desc")
                .status(Status.CREATED)
                .dueDateTime(DUE_DATE_TIME)
                .build();

        Mockito.when(taskService.getByTaskId(1L)).thenReturn(Optional.of(response));

        mockMvc.perform(get("/task/v1/find/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void findTask_notFound() throws Exception {
        Mockito.when(taskService.getByTaskId(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/task/v1/find/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllTasks_success() throws Exception {
        List<TaskResponse> list = List.of(
                TaskResponse.builder()
                        .id(1L)
                        .title("A")
                        .description("Desc")
                        .status(Status.CREATED)
                        .dueDateTime(DUE_DATE_TIME)
                        .build(),
                TaskResponse.builder()
                        .id(2L)
                        .title("B")
                        .description("Desc")
                        .status(Status.IN_PROGRESS)
                        .dueDateTime(DUE_DATE_TIME)
                        .build()
        );

        Mockito.when(taskService.getAll()).thenReturn(list);

        mockMvc.perform(get("/task/v1/find-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void deleteTask_success() throws Exception {
        Mockito.when(taskService.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/task/v1/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void deleteTask_notFound() throws Exception {
        Mockito.when(taskService.delete(1L)).thenReturn(false);

        mockMvc.perform(delete("/task/v1/delete/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("false"));
    }
}