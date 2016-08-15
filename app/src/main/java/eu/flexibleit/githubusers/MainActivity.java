package eu.flexibleit.githubusers;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.coordinatorLayout) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.btn_resfesh) Button mRefreshButton;
    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;

    private GitHubService service;
    private UserAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private int mCurrentId = 0;
    private Pattern mLinkPattern = Pattern.compile("since=(.*?)>");

    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        service = GitHubServiceGenerator.createService(GitHubService.class);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new UserAdapter(this);
        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getUsersList();
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRefreshButton.setVisibility(View.GONE);
                getUsersList();
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    mAdapter.LoadMore();
                    isLoading = true;
                }
            }
        });

        getUsersList();
    }

    private void getUsersList()
    {
        mAdapter.add(null);
        service.listUsers(mCurrentId).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                setLoaded();

                if (response.isSuccessful()) {

                    List<User> newUsers = response.body();

                    for (User item : newUsers) {
                        mAdapter.add(item);
                    }

                    Matcher matcher = mLinkPattern.matcher(response.headers().get("Link"));

                    if (matcher.find()) {
                        mCurrentId = Integer.parseInt(matcher.group(1));
                    }

                } else {
                    Snackbar.make(mCoordinatorLayout, getString(R.string.incorrect_response), Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                setLoaded();
                showRefreshButton();

                Snackbar.make(mCoordinatorLayout, getString(R.string.connection_failure), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void setLoaded() {
        mAdapter.removeLast();
        isLoading = false;
    }

    private void showRefreshButton()
    {
        if (mAdapter.getItemCount() == 0) {
            mRefreshButton.setVisibility(View.VISIBLE);
        }
    }
}
