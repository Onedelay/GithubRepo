package com.onedelay.githubrepo.view

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.onedelay.githubrepo.R
import com.onedelay.githubrepo.contract.RepositoryListContract
import com.onedelay.githubrepo.model.GitHubService
import com.onedelay.githubrepo.presenter.RepositoryListPresenter
import kotlinx.android.synthetic.main.activity_repository_list.*
import kotlinx.android.synthetic.main.content_repository_list.*

/**
 * 리포지토리 목록을 표시하는 Activity
 * MVP 의 View 역할을 가진다
 */
class RepositoryListActivity : AppCompatActivity(), RepositoryAdapter.OnRepositoryItemClickListener,
    RepositoryListContract.View {
    private var languageSpinner: Spinner? = null

    private var repositoryAdapter: RepositoryAdapter? = null

    private var repositoryListPresenter: RepositoryListContract.UserActions? = null

    override val selectedLanguage: String
        get() = languageSpinner!!.selectedItem as String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository_list)

        // View 를 설정
        setupViews()

        // ① Presenter 의 인스턴스를 생성
        val gitHubService = (application as NewGitHubReposApplication).gitHubService
        repositoryListPresenter = RepositoryListPresenter(this, gitHubService)
    }

    /**
     * 목록 등의 화면 요소를 만든다
     */
    private fun setupViews() {
        // 툴바 설정
        setSupportActionBar(toolbar)

        // Recycler View
        recycler_repos.layoutManager = LinearLayoutManager(this)
        repositoryAdapter = RepositoryAdapter(this as RepositoryAdapter.OnRepositoryItemClickListener)
        recycler_repos.adapter = repositoryAdapter

        // Spinner
        languageSpinner = findViewById<View>(R.id.language_spinner) as Spinner
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        adapter.addAll("java", "kotlin", "objective-c", "swift", "groovy", "python", "ruby", "c")
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner!!.adapter = adapter
        languageSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                //  스피너의 선택 내용이 바뀌면 호출된다
                val language = languageSpinner!!.getItemAtPosition(position) as String
                // ② Presenter 에 프로그래밍 언어를 선택했다고 알린다
                repositoryListPresenter!!.selectLanguage(language)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    /**
     * RecyclerView 에서 클릭됐다
     * @see RepositoryAdapter.OnRepositoryItemClickListener.onRepositoryItemClickListener
     */
    override fun onRepositoryItemClick(item: GitHubService.RepositoryItem) {
        repositoryListPresenter!!.selectRepositoryItem(item)
    }

    // =====RepositoryListContract.View 구현=====
    // 이곳에서 Presenter 로부터 지시를 받아 View 의 변경 등을 한다

    override fun startDetailActivity(fullRepositoryName: String) {
        DetailActivity.start(this, fullRepositoryName)
    }


    override fun showProgress() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progress_bar.visibility = View.GONE
    }

    override fun showRepositories(repositories: GitHubService.Repositories) {
        // ③ 리포지토리 목록을 Adapter 에 설정한다
        repositoryAdapter!!.setItemsAndRefresh(repositories.items)
    }

    override fun showError() {
        Snackbar.make(coordinator_layout, "읽을 수 없습니다", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
    }

}
