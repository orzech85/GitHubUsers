package eu.flexibleit.githubusers;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GitHubService {
    @GET("users")
    Call<List<User>> listUsers(@Query("since") int id);
}
