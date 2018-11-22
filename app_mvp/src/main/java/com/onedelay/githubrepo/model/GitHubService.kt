package com.onedelay.githubrepo.model

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Observable

/**
 * Retrofit 로 GitHub 의 API 를 이용하기 위한 클래스
 */
interface GitHubService {
    /**
     * GitHub의 리포지토리 검색 결과를 가져온다
     * https://developer.github.com/v3/search/
     *
     * @param query GitHub 의 API 로 검색할 내용
     * @return API 액세스 결과를 얻은 후의 콜백으로서 SearchResponse 를 가져올 수 있는 RxJava 의 Observable 로 반환한다
     */
    @GET("search/repositories?sort=stars&order=desc")
    fun listRepos(@Query("q") query: String): Observable<Repositories>

    /**
     * 리포지토리의 상세내용을 가져온다
     * https://developer.github.com/v3/repos/#get
     *
     * @return API 액세스 결과를 얻은 후의 콜백으로서 RepositoryItem 을 가져올 수 있는 RxJava 의 Observable 로 반환한다
     */
    @GET("repos/{repoOwner}/{repoName}")
    fun detailRepo(@Path(value = "repoOwner") owner: String, @Path(value = "repoName") repoName: String): Observable<RepositoryItem>

    /**
     * API 액세스 결과가 이 클래스에 들어간다
     * GitHub 의 리포지토리 목록이 들어있다
     *
     * @see GitHubService.listRepos
     */
    class Repositories(val items: List<RepositoryItem>)

    /**
     * API 액세스 결과가 이 클래스에 들어간다
     * GitHub 리포지터리의 데이터가 들어있다
     *
     * @see GitHubService.detailRepo
     */
    class RepositoryItem(
        val description: String,
        val owner: Owner,
        val language: String,
        val name: String,
        val stargazers_count: String,
        val forks_count: String,
        val full_name: String,
        val html_url: String
    )

    /**
     * GitHub 리포지토리에 대한 오너의 데이터가 들어있다
     *
     * @see GitHubService.detailRepo
     */
    class Owner(
        val received_events_url: String,
        val organizations_url: String,
        val avatar_url: String,
        val gravatar_id: String,
        val gists_url: String,
        val starred_url: String,
        val site_admin: String,
        val type: String,
        val url: String,
        val id: String,
        val html_url: String,
        val following_url: String,
        val events_url: String,
        val login: String,
        val subscriptions_url: String,
        val repos_url: String,
        val followers_url: String
    )
}
