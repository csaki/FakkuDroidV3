package net.fakku.api.exceptions;
import net.fakku.api.dto.single.ErrorMessageDto;

/**
 * Created by DevSaki on 15/05/2015.
 */
public class FakkuApiException extends Exception{

    private ErrorMessageDto errorMessage;
    private int httpCode;

    public FakkuApiException(Throwable throwable) {
        super(throwable);
    }

    public FakkuApiException(ErrorMessageDto errorMessage, int httpCode) {
        this.errorMessage = errorMessage;
        this.httpCode = httpCode;
    }

    public FakkuApiException(String detailMessage, int httpCode) {
        super(detailMessage);
        this.httpCode = httpCode;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public ErrorMessageDto getErrorMessage() {
        return errorMessage;
    }
}
