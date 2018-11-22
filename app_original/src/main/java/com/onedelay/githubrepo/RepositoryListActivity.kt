package com.onedelay.githubrepo

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_repository_list.*
import kotlinx.android.synthetic.main.content_repository_list.*
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

class RepositoryListActivity : AppCompatActivity(), RepositoryAdapter.OnRepositoryItemClickListener {
    private val repositoryAdapter = RepositoryAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository_list)

        setupViews()
    }

    override fun onRepositoryItemClick(item: GitHubService.Companion.RepositoryItem) {
        DetailActivity.start(this, item.full_name)
    }

    /**
     * 목록 등 화면 요소를 만든다
     */
    private fun setupViews() {
        // 툴바 설정
        setSupportActionBar(toolbar)

        // RecyclerView
        recycler_repos.layoutManager = LinearLayoutManager(this)
        recycler_repos.adapter = repositoryAdapter

        // Spinner
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        adapter.addAll("java", "kotlin", "objective-c", "swift", "groovy", "python", "ruby", "c")
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        language_spinner.adapter = adapter
        language_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                // Do nothing
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 선택시 뿐만 아니라 처음에도 호출 됨
                val language = language_spinner.getItemAtPosition(position) as String
                loadRepositories(language)
            }

        }
    }

    /**
     * 지난 1주일간 만들어진 라이브러리의 인기순으로 가져온다
     * @param language 가져올 프로그래밍 언어
     */
    private fun loadRepositories(language: String) {
        // 로딩 중이므로 진행바 표시
        progress_bar.visibility = View.VISIBLE

        // 일주일전 날짜의 문자열
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -7)
        val text = android.text.format.DateFormat.format("yyyy-MM-dd", calendar).toString()

        // 서버 요청
        val application = application as GitHubReposApplication

        // 지난 일주일간 생성되고 언어가 language 인 것을 요청한다
        val observable = application
            .gitHubService
            .listRepos("language:$language created:>$text")

        // IO 스레드로 통신하고, 메인스레드에서 결과를 수신하도록 한다
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Subscriber<GitHubService.Companion.Repositories>() {
                override fun onNext(repositories: GitHubService.Companion.Repositories?) {
                    // 로딩이 끝났으므로 진행바를 표시하지 않는다
                    progress_bar.visibility = View.GONE
                    // 가져온 아이템을 표시하고자 RecyclerView 에 아이템을 설정하고 갱신한다
                    repositoryAdapter.setItemsAndRefresh(repositories?.items ?: listOf())
                }

                override fun onCompleted() {
                    // Do nothing
                }

                override fun onError(e: Throwable?) {
                    // 통신 실패 시에 호출된다
                    // 여기서는 스낵바를 표시한다(아래에 표시되는 바)
                    Snackbar.make(coordinator_layout, "읽어올 수 없습니다.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
            })
    }
}