package mr.iscae.exceptions;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles exceptions related to validation failures.
     *
     * @param ex the exception instance
     * @return a response entity with an appropriate error message and status code
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        HashMap<String, Object> validations = new HashMap<>();
        for (FieldError fieldError : fieldErrors) {
            validations.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return new ResponseEntity<>(new HashMap<String, Object>() {{ put("message", validations); }}, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exceptions related to Serialisation and Deserialization.
     *
     * @param ex the exception instance
     * @return a response entity with an appropriate error message and status code
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String errorMessage;
        try {
            String[] parts = ex.getCause().getMessage().split("`");
            String enumType = parts[1].substring(parts[1].lastIndexOf('.') + 1);
            errorMessage = "invalid value provided for: " + enumType;
        } catch (Exception e) {
            errorMessage = ex.getMessage();
        }

        String finalErrorMessage = errorMessage;
        return new ResponseEntity<>(new HashMap<String, String>() {{ put("message", finalErrorMessage); }}, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exceptions where an expected entity is not found in the system.
     *
     * @param ex the exception instance
     * @return a response entity with an appropriate error message and status code
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleIdNotFoundException(EntityNotFoundException ex) {
        return new ResponseEntity<>(new HashMap<String, String>() {{ put("message", ex.getMessage()); }}, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles exceptions related to invalid arguments.
     *
     * @param ex the exception instance
     * @return a response entity with an appropriate error message and status code
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(new HashMap<String, String>() {{ put("message", ex.getMessage()); }}, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exceptions related to Authentication (token).
     *
     * @return a response entity with an appropriate error message and status code
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException() {
        return new ResponseEntity<>(new HashMap<String, String>() {{ put("message", "You don't have the required permissions to access this resource."); }}, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles exceptions related to Authorization.
     *
     * @return a response entity with an appropriate error message and status code
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException() {
        return new ResponseEntity<>(new HashMap<String, String>() {{ put("message", "You don't have the required permissions to access this resource."); }}, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<?> handleAuthenticationCredentialsNotFoundException() {
        return new ResponseEntity<>(new HashMap<String, String>() {{ put("message", "You must provide an authorization token."); }}, HttpStatus.FORBIDDEN);
    }
}
