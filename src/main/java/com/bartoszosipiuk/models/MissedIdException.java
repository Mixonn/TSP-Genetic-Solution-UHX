package com.bartoszosipiuk.models;

/**
 * Created by Bartosz Osipiuk on 2018-05-07.
 *
 * @author Bartosz Osipiuk
 */

public class MissedIdException extends Exception{
    public MissedIdException() {
        super();
    }

    public MissedIdException(String message) {
        super(message);
    }

    public MissedIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissedIdException(Throwable cause) {
        super(cause);
    }

    protected MissedIdException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
