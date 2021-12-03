package io.good.food.exception;

public class BusinessException extends RuntimeException {

    public BusinessException() {
        this("Internal Server Error");
    }

    public BusinessException(final String message) {
        super(message);
    }

    public BusinessException(final Throwable cause) {
        super(cause);
    }

    public BusinessException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
