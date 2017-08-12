package inspiringbits.me.cleanscene.model;

/**
 * Created by Ivan on 2017/8/12.
 */

public class AnonymousUserModel {
    private String fName;
    private String lName;
    private String phoneNo;
    private String title;
    private String email;
    private String address;

    public AnonymousUserModel() {
    }

    public AnonymousUserModel(String fName, String lName, String phoneNo, String title, String email, String address) {
        this.fName = fName;
        this.lName = lName;
        this.phoneNo = phoneNo;
        this.title = title;
        this.email = email;
        this.address = address;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
