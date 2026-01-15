package org.f3.postalmanagement.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DomainStatusException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    public DomainStatusException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public static DomainStatusException badRequest(String message, String errorCode) {
        return new DomainStatusException(message, HttpStatus.BAD_REQUEST, errorCode);
    }

    public static DomainStatusException notFound(String message, String errorCode) {
        return new DomainStatusException(message, HttpStatus.NOT_FOUND, errorCode);
    }

    public static DomainStatusException forbidden(String message, String errorCode) {
        return new DomainStatusException(message, HttpStatus.FORBIDDEN, errorCode);
    }
}
