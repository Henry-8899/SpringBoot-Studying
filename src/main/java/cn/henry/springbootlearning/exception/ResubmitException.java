package cn.henry.springbootlearning.exception;

public class ResubmitException extends RuntimeException {

    public ResubmitException() {
    }

    public ResubmitException(String message) {
        super(message);
    }

    public ResubmitException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResubmitException(Throwable cause) {
        super(cause);
    }

    public ResubmitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
