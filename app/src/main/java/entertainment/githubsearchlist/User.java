package entertainment.githubsearchlist;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("login")
    public String user;
    public int followers;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }
}
