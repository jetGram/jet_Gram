package finalsoft.FcmService.Models;

/**
 * Created by Pouya on 6/13/2016.
 */
public class Favor {
    private Long userid;
    private String username;

    public Favor(Long userid) {
        this.userid = userid;
        this.username = username;
    }

    public Favor() {

    }

    public Favor(String user) {
        this.username = user;
        this.userid = Long.valueOf(0) ;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
