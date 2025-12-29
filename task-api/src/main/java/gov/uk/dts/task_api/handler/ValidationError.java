package gov.uk.dts.task_api.handler;

import java.util.List;

public record ValidationError(List<FieldError> errors) {
    public record FieldError(String field, String message) {}
}
