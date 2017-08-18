package inspiringbits.me.cleanscene.model;

public class Volunteer {
    private String vName;
    private String address;
    private String suburb;
    private String zipcode;

    public Volunteer() {
    }

    public Volunteer(String vName, String address, String suburb, String zipcode) {
        this.vName = vName;
        this.address = address;
        this.suburb = suburb;
        this.zipcode = zipcode;
    }

    public String getvName() {
        return vName;
    }

    public void setvName(String vName) {
        this.vName = vName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
}
