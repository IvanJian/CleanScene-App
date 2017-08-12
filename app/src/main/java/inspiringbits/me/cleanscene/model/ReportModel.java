package inspiringbits.me.cleanscene.model;

/**
 * Created by Ivan on 2017/8/12.
 */

public class ReportModel {
    private String reportId;
    private String rating;
    private String source;
    private String type;
    private float Latitude;
    private float Longitude;
    private String description;
    private String photo;
    private boolean isAnonymous;
    private AnonymousUserModel anonymousUser;
    private boolean isSendToCouncil;

    public ReportModel() {
    }

    public ReportModel(String reportId, String rating, String source, String type, float latitude, float longitude, String description, String photo, boolean isAnonymous, AnonymousUserModel anonymousUser, boolean isSendToCouncil) {
        this.reportId = reportId;
        this.rating = rating;
        this.source = source;
        this.type = type;
        Latitude = latitude;
        Longitude = longitude;
        this.description = description;
        this.photo = photo;
        this.isAnonymous = isAnonymous;
        this.anonymousUser = anonymousUser;
        this.isSendToCouncil = isSendToCouncil;
    }

    public boolean isSendToCouncil() {
        return isSendToCouncil;
    }

    public void setSendToCouncil(boolean sendToCouncil) {
        isSendToCouncil = sendToCouncil;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getLatitude() {
        return Latitude;
    }

    public void setLatitude(float latitude) {
        Latitude = latitude;
    }

    public float getLongitude() {
        return Longitude;
    }

    public void setLongitude(float longitude) {
        Longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    public AnonymousUserModel getAnonymousUser() {
        return anonymousUser;
    }

    public void setAnonymousUser(AnonymousUserModel anonymousUser) {
        this.anonymousUser = anonymousUser;
    }
}
