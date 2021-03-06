package entertainment.githubsearchlist;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GithubUserAdapter extends RecyclerView.Adapter<GithubUserAdapter.MyViewHolder>
        implements Filterable {
    private static final String LOG_TAG = GithubUserAdapter.class.getSimpleName();

    private List<User> githubUserList;
    private List<User> githubfilterresult;

    public GithubUserAdapter(List<User> githubUserList) {
        this.githubUserList = githubUserList;
        this.githubfilterresult = githubUserList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        User user = githubUserList.get(position);
        holder.githubUser.setText(user.getUser());
        holder.githubUserFollowerCount.setText(" " + user.getFollowers());
    }

    @Override
    public int getItemCount() {
        return githubUserList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView githubUser, githubUserFollowerCount;

        public MyViewHolder(View itemView) {
            super(itemView);

            githubUser = itemView.findViewById(R.id.user_name_text);
            githubUserFollowerCount = itemView.findViewById(R.id.user_follower_count_text);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    githubfilterresult = githubUserList;
                } else {
                    List<User> filteredList = new ArrayList<>();
                    for (User user : githubUserList) {
                        if (user.getUser().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(user);
                        }

                        githubfilterresult = filteredList;
                    }
                }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = githubfilterresult;
                    return filterResults;
                }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                githubUserList = (ArrayList<User>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
