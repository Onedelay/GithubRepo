package com.onedelay.githubrepo

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.graphics.drawable.RoundedBitmapDrawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.centerCrop
import com.bumptech.glide.request.target.BitmapImageViewTarget
import kotlinx.android.synthetic.main.activity_detail.*
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import rx.schedulers.Schedulers

/**
 * 상세화면을 표시하는 액티비티
 * Java to Kotlin 컨버팅했고, 일부 수정했음.
 */
class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val intent = intent
        val fullRepoName = intent.getStringExtra(EXTRA_FULL_REPOSITORY_NAME)

        loadRepositories(fullRepoName)
    }

    /**
     * 한 개의 리포지토리에 대한 정보를 가져온다
     * 기본적으로 API 액세스 방법은 RepositoryListActivity#loadRepositories(String)과 같다
     */
    private fun loadRepositories(fullRepoName: String) {
        // 리포지토리의 이름을 /로 분할한다
        val repoData = fullRepoName.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val owner = repoData[0]
        val repoName = repoData[1]
        val gitHubService = (application as GitHubReposApplication).gitHubService
        gitHubService.detailRepo(owner, repoName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response -> setupRepositoryInfo(response) }, {
                Snackbar.make(findViewById(android.R.id.content), "읽을 수 없습니다.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            })
    }

    private fun setupRepositoryInfo(response: GitHubService.Companion.RepositoryItem) {
        fullname.text = response.full_name
        detail.text = response.description
        repo_star.text = response.stargazers_count
        repo_fork.text = response.forks_count
        // 서버로부터 이미지를 가져와 imageView 에 넣는다
        Glide.with(this@DetailActivity)
            .asBitmap()
            .load(response.owner.avatar_url)
            .into(object : BitmapImageViewTarget(owner_image) {
                override fun setResource(resource: Bitmap?) {
                    val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, resource)
                    circularBitmapDrawable.isCircular = true
                    owner_image.setImageDrawable(circularBitmapDrawable)
                }
            })

        // 로고와 리포지토리 이름을 탭하면 작자의 GitHub 페이지를 브라우저로 연다
        val listener = View.OnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(response.html_url)))
            } catch (e: Exception) {
                Snackbar.make(findViewById(android.R.id.content), "링크를 열 수 없습니다", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }
        fullname.setOnClickListener(listener)
        owner_image.setOnClickListener(listener)
    }

    companion object {
        private const val EXTRA_FULL_REPOSITORY_NAME = "EXTRA_FULL_REPOSITORY_NAME"

        /**
         * DetailActivity 를 시작하는 메소드
         * @param fullRepositoryName 표시하고 싶은 리포지토리 이름(google/iosched 등)
         */
        fun start(context: Context, fullRepositoryName: String) {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(EXTRA_FULL_REPOSITORY_NAME, fullRepositoryName)
            context.startActivity(intent)
        }
    }
}
