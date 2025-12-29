package gov.uk.dts.task_api.handler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class RequestExceptionHandler {

    /**
     * Intercept binding exception and wrap it in validation error object
     *
     * @param ex - exception thrown by dispatcher servlet
     * @return - ValidationError object
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleValidationErrors(MethodArgumentNotValidException ex) {

        List<ValidationError.FieldError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> new ValidationError.FieldError(err.getField(), err.getDefaultMessage()))
                .toList();

        return new ResponseEntity<>(new ValidationError(errors), HttpStatus.BAD_REQUEST);
    }

    /**
     * Intercept json deserialization exception and wrap it in validation error object
     *
     * @param ex - exception thrown by dispatcher servlet
     * @return - ValidationError object
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ValidationError> handleJsonParseErrors(HttpMessageNotReadableException ex) {

        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException ife && ife.getTargetType().isEnum()) {

            String field = ife.getPath().isEmpty()
                    ? "unknown"
                    : ife.getPath().getFirst().getFieldName();

            String allowedValues = Arrays.toString(ife.getTargetType().getEnumConstants());

            ValidationError.FieldError error = new ValidationError.FieldError(field, "Invalid value. Allowed values: " + allowedValues);
            return new ResponseEntity<>(new ValidationError(List.of(error)), HttpStatus.BAD_REQUEST);
        }

        ValidationError.FieldError error = new ValidationError.FieldError("request", "Malformed JSON request");
        return new ResponseEntity<>(new ValidationError(List.of(error)), HttpStatus.BAD_REQUEST);
    }
}
