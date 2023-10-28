package sovcombank.jabka.studyservice.exceptions;

public class FileException extends RuntimeException {
    public FileException(String msg) {
        super(msg);
    }

    public FileException() {
        super();
    }

    public FileException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
