package com.onedelay.githubrepo

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Observable

/**
 * Retrofit 으로 Github API 를 이용하기 위한 클래스
 */
interface GitHubService {
    /**
     * GitHub 의 리포지토리 검색 결과를 가져온다
     * https://developer.github.com/v3/search/
     * @param query GitHub API 로 검색할 내용
     * @return API 액세스 결과 취득 후의 콜백으로서 SearchResponse 를 가져올 수 있는 RxJava 의 Observable 로 반환
     */
    @GET("search/repositories?sort=stars&order=desc")
    fun listRepos(@Query("q") query: String): Observable<Repositories>

    /**
     * 리포지토리 상세 내역을 가져온다
     * https://developer.github.com/v3/repos/#get
     * @return API 액세스 결과 취득 후의 콜백으로서 RepositoryItem 을 가져올 수 있는 RxJava 의 Observable 로 반환
     */
    @GET("repos/{repoOwner}/{repoName}")
    fun detailRepo(@Path(value = "repoOwner") owner: String, @Path(value = "repoName") repoName: String): Observable<RepositoryItem>

    companion object {
        /**
         * API 액세스 결과가 이 클래스에 들어온다
         * Github 의 리포지토리 목록이 들어와있다.
         * @see GitHubService#listRepos(String)
         */
        data class Repositories(val items: List<RepositoryItem>)

        /**
         * API 액세스 결과가 이 클래스에 들어온다
         * GitHub 의 리포지토리 데이터가 들어와 있다
         * @see GitHubService#detailRepo(String, String)
         */
        data class RepositoryItem(
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
         * GitHub 의 리포지토리에 대한 오너의 데이터가 들어와 있다
         * @see GitHubService#detailRepo(String, String)
         */
        data class Owner(
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
}