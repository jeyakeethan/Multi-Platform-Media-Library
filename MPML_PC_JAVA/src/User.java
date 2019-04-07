public class User {
    private String username;
    private String password;
    private int id;
    public User(String username, String password,int id){
        this.setUsername(username);
        this.setPassword(password);
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public int getId() {
        return id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
