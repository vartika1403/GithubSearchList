package entertainment.githubsearchlist;

import java.util.Comparator;

public class SortByFollower implements Comparator<User> {
    @Override
    public int compare(User user1, User user2) {
        return user1.followers - user2.followers;
    }
}
