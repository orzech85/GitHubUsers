package eu.flexibleit.githubusers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    public void LoadMore()
    {
        if (mOnLoadMoreListener != null) {
            mOnLoadMoreListener.onLoadMore();
        }
    }

    private OnLoadMoreListener mOnLoadMoreListener;

    public  UserAdapter(Context context)
    {
        mItems = new ArrayList<>();
        mContext = context;
    }

    private Context mContext;
    private List<User> mItems;

    public void add(User user) {
        mItems.add(user);
        notifyItemInserted(mItems.size() - 1);
    }

    public void removeLast() {
        mItems.remove(mItems.size() - 1);
        notifyItemRemoved(mItems.size());
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_login) TextView textView;
        @BindView(R.id.img_user) CircleImageView imageView;

        public UserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.progressBar) ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user, parent, false);

            return new UserViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_loading, parent, false);

            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof UserViewHolder) {
            ((UserViewHolder)holder).textView.setText(mItems.get(position).getLogin());

            Picasso.with(mContext)
                    .load(mItems.get(position).getAvatar_url())
                    .placeholder(R.color.colorPlaceholder)
                    .into(((UserViewHolder)holder).imageView);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(mItems.get(position).getHtml_url()));
                    mContext.startActivity(i);
                }
            });

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
