package group.starRock;

public abstract class Person {

    private String email;
    protected String password;
    protected String userName;
    public Person(){

    }

    public Person(String email, String password, String userName) {
        this.email = email;
        this.password = password;
        this.userName = userName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getUserName() {
        return userName;
    }
    public void setUsername(String userName) {
        this.userName = userName;
    }
    public abstract String getUsername();
}
