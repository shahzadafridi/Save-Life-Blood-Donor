package official.com.savelife_blooddonor.Model;

import android.location.Location;

public class Request {

    String id;
    String name;
    String bgroup;
    String address;
    String phone;
    String message;
    Location location;

    public Request(String id, String name, String message, String bgroup, String address, String phone, Location location) {
        this.id = id;
        this.name = name;
        this.bgroup = bgroup;
        this.address = address;
        this.phone = phone;
        this.message = message;
        this.location = location;
    }

    public String getId(){
     return id;
    }

    public String getName() {
        return name;
    }

    public String getBgroup() {
        return bgroup;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getMessage() {
        return message;
    }

    public Location getLocation() {
        return location;
    }
}
