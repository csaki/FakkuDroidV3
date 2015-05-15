package net.fakku.api.dto.single;

/**
 * Created by DevSaki on 15/05/2015.
 */
public class ErrorMessageDto {

    private String error;
    private String refresh;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getRefresh() {
        return refresh;
    }

    public void setRefresh(String refresh) {
        this.refresh = refresh;
    }
}
