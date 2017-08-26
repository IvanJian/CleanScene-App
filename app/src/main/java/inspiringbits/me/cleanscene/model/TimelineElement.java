package inspiringbits.me.cleanscene.model;

/**
 * Created by Ivan on 2017/8/25.
 */

public class TimelineElement {
    public final static String TIMELINE_TYPE_REPORT="report";
    public final static String TIMELINE_TYPE_VOLUNTEER_ACTIVITY="volunteer_activity";

    private Integer timelineId;
    private String contentId;
    private String title;
    private String type;
    private String time;
    private String location;
    private String fromDate;
    private String expireDate;
    private Integer userId;

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public Integer getTimelineId() {
        return timelineId;
    }

    public void setTimelineId(Integer timelineId) {
        this.timelineId = timelineId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
