package entertainment.githubsearchlist;

public class Followers {

    public String user;
    public int followers;

    public Followers(String user, int followers) {
        this.user = user;
        this.followers = followers;
    }

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
