package com.bartoszosipiuk.model;

/**
 * Created by Bartosz Osipiuk on 2018-04-15.
 *
 * @author Bartosz Osipiuk
 */

public class NoPathGeneratedException extends Exception {
    public NoPathGeneratedException() {
        super();
    }

    public NoPathGeneratedException(String message) {
        super(message);
    }

    public NoPathGeneratedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoPathGeneratedException(Throwable cause) {
        super(cause);
    }

    protected NoPathGeneratedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
