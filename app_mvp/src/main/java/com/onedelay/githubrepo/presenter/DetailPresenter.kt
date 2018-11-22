package com.onedelay.githubrepo.presenter

import com.onedelay.githubrepo.contract.DetailContract
import com.onedelay.githubrepo.model.GitHubService
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import rx.schedulers.Schedulers

class DetailPresenter(private val detailView: DetailContract.View, private val gitHubService: GitHubService) :
    DetailContract.UserActions {
    private var repositoryItem: GitHubService.RepositoryItem? = null

    /**
     * 하나의 리포지토리에 관한 정보를 가져온다
     * 기본적으로 API 액세스 방법은 RepositoryListActivity#loadRepositories(String)와 같다
     */
    private fun loadRepositories() {
        val fullRepoName = detailView.fullRepositoryName ?: ""
        // 리포지토리 이름을 /로 분할한다
        val repoData = fullRepoName.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val owner = repoData[0]
        val repoName = repoData[1]
        gitHubService
            .detailRepo(owner, repoName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                repositoryItem = response
                detailView.showRepositoryInfo(response)
            }, { detailView.showError("읽을 수 없습니다.") })
    }

    override fun prepare() {
        loadRepositories()
    }

    override fun titleClick() {
        try {
            detailView.startBrowser(repositoryItem!!.html_url)
        } catch (e: Exception) {
            detailView.showError("링크를 열수 없습니다.")
        }
    }
}
