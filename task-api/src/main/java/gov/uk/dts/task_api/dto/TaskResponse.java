package gov.uk.dts.task_api.dto;

import gov.uk.dts.task_api.utility.Status;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.LocalDateTime;

@Builder
@Getter
public class TaskResponse {

    @NonNull private Long id;
    @NonNull private String title;
    private String description;
    @NonNull private Status status;
    @NonNull private LocalDateTime dueDateTime;
}
