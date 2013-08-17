package network;

public class UserData {
    String name, password, email, facebook, twitter, linkedin;
    int phone, id;


    public UserData() {
        name = "";
        password = "";
        email = "";
        facebook = "";
        twitter = "";
        linkedin = "";
        phone = 0;
        id = 0;
    }

    public Object[] serialize() {
        Object[] data = new Object[8];
        data[0] = (Integer) id;
        data[1] = password;
        data[2] = email;
        data[3] = facebook;
        data[4] = twitter;
        data[5] = linkedin;
        data[6] = (Integer) phone;
        data[7] = name;
        return data;
    }

    public void deserialize(Object[] data) {
        id = (Integer) data[0];
        password = (String) data[1];
        email = (String) data[2];
        facebook = (String) data[3];
        twitter = (String) data[4];
        linkedin = (String) data[5];
        phone = (Integer) data[6];
        name = (String) data[7];
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String n) {
        name = n;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String s) {
        password = s;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String s) {
        email = s;
    }
    
    public String getFacebook() {
        return facebook;
    }
    
    public void setFacebook(String s) {
        facebook = s;
    }
    
    public String getTwitter() {
        return twitter;
    }
    
    public void setTwitter(String s) {
        twitter = s;
    }
    
    public String getLinkedIn() {
        return linkedin;
    }
    
    public void setLinkedIn(String s) {
        linkedin = s;
    }
    
    public int getPhone() {
        return phone;
    }
    
    public void setPhone(int s) {
        phone = s;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int s) {
        id = s;
    }
}
