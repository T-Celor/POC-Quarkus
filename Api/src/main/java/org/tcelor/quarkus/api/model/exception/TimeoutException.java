package org.tcelor.quarkus.api.model.exception;

public class TimeoutException extends RuntimeException {

    public TimeoutException(String message) {
            super(message);
    }
}
