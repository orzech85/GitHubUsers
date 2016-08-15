package eu.flexibleit.githubusers;

import org.junit.Test;

import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.*;

public class GitHubServiceTest {
    @Test
    public void response_isCorrect() throws Exception {
        MockWebServer mockWebServer = new MockWebServer();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("").toString())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mockWebServer.enqueue(new MockResponse().setBody("[\n" +
                "  {\n" +
                "    \"login\": \"mojombo\",\n" +
                "    \"id\": 1,\n" +
                "    \"avatar_url\": \"https://avatars.githubusercontent.com/u/1?v=3\",\n" +
                "    \"gravatar_id\": \"\",\n" +
                "    \"url\": \"https://api.github.com/users/mojombo\",\n" +
                "    \"html_url\": \"https://github.com/mojombo\",\n" +
                "    \"followers_url\": \"https://api.github.com/users/mojombo/followers\",\n" +
                "    \"following_url\": \"https://api.github.com/users/mojombo/following{/other_user}\",\n" +
                "    \"gists_url\": \"https://api.github.com/users/mojombo/gists{/gist_id}\",\n" +
                "    \"starred_url\": \"https://api.github.com/users/mojombo/starred{/owner}{/repo}\",\n" +
                "    \"subscriptions_url\": \"https://api.github.com/users/mojombo/subscriptions\",\n" +
                "    \"organizations_url\": \"https://api.github.com/users/mojombo/orgs\",\n" +
                "    \"repos_url\": \"https://api.github.com/users/mojombo/repos\",\n" +
                "    \"events_url\": \"https://api.github.com/users/mojombo/events{/privacy}\",\n" +
                "    \"received_events_url\": \"https://api.github.com/users/mojombo/received_events\",\n" +
                "    \"type\": \"User\",\n" +
                "    \"site_admin\": false\n" +
                "  }]"));

        GitHubService service = retrofit.create(GitHubService.class);

        Call<List<User>> call = service.listUsers(0);
        retrofit2.Response<List<User>> response = call.execute();

        assertEquals(response.body().get(0).getLogin(), "mojombo");

        mockWebServer.shutdown();
    }
}
