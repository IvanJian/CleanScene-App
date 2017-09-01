package inspiringbits.me.cleanscene.model;

import java.util.List;

/**
 * Created by Ivan on 2017/8/24.
 */

public class VolunteeringActivity {
    public static final String STATUS_CLOSE = "close";
    public static final String STATUS_OPEN="open";
    private Integer volunteeringActivityId;
    private Double latitude;
    private Double longitude;
    private String address;
    private String createdDate;
    private String createdTime;
    private String activityDate;
    private String fromTime;
    private String toTime;
    private Boolean isPrivate;
    private List<User> members;
    private String status;
    private Integer anonymousMember;


    public Integer getVolunteeringActivityId() {
        return volunteeringActivityId;
    }

    public void setVolunteeringActivityId(Integer volunteeringActivityId) {
        this.volunteeringActivityId = volunteeringActivityId;
    }

    public Boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Integer getAnonymousMember() {
        return anonymousMember;
    }

    public void setAnonymousMember(Integer anonymousMember) {
        this.anonymousMember = anonymousMember;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(String activityDate) {
        this.activityDate = activityDate;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
