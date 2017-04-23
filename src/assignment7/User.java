package assignment7;


public class User {

    private String ID;
    private Boolean isOnline;

    User(String name){
        ID = name;
        isOnline = true;
    }

    public String toString(){
        String str = "User " +ID+ " is ";
        return isOnline ? str+str.concat("online.") : str+str.concat("not online.");
    }

    public void changeUserID(String ID) {
        this.ID = ID;
    }

    public String getUserID()
    {
        return ID;
    }
    public Boolean isOnline() {
        return isOnline;
    }

    public void setOnlineStatus(Boolean online) {
        isOnline = online;
    }

}
