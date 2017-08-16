package inspiringbits.me.cleanscene.model;

/**
 * Created by Ivan on 2017/8/13.
 */

public class BasicMessage {
    private String code;
    private boolean status;
    private String content;

    public BasicMessage(String code, boolean isSuccess, String content) {
        this.code = code;
        this.status = isSuccess;
        this.content = content;
    }

    public BasicMessage() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
