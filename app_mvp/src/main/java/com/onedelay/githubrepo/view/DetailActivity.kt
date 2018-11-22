package com.onedelay.githubrepo.view

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.onedelay.githubrepo.R
import com.onedelay.githubrepo.contract.DetailContract
import com.onedelay.githubrepo.model.GitHubService
import com.onedelay.githubrepo.presenter.DetailPresenter
import kotlinx.android.synthetic.main.activity_detail.*

/**
 * 상세화면을 표시하는 Activity
 */
class DetailActivity : AppCompatActivity(), DetailContract.View {
    private var detailPresenter: DetailContract.UserActions? = null
    override var fullRepositoryName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val intent = intent
        fullRepositoryName = intent.getStringExtra(EXTRA_FULL_REPOSITORY_NAME)

        val gitHubService = (application as NewGitHubReposApplication).gitHubService
        detailPresenter = DetailPresenter(this, gitHubService)
        detailPresenter!!.prepare()
    }

    override fun showRepositoryInfo(response: GitHubService.RepositoryItem) {
        fullname.text = response.full_name
        detail.text = response.description
        repo_star.text = response.stargazers_count
        repo_fork.text = response.forks_count
        // 서버에서 이미지를 가져와 imageView 에 넣는다
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
        // 로고와 리포지토리 이름을 탭하면, 제작자의 GitHub 페이지를 브라우저로 연다
        val listener = View.OnClickListener { detailPresenter!!.titleClick() }
        fullname.setOnClickListener(listener)
        owner_image.setOnClickListener(listener)
    }

    /**
     * @throws Exception
     */
    override fun startBrowser(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    override fun showError(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
            .show()
    }

    companion object {
        private const val EXTRA_FULL_REPOSITORY_NAME = "EXTRA_FULL_REPOSITORY_NAME"

        /**
         * DetailActivity 를 시작하는 메소드
         *
         * @param fullRepositoryName 표시하고 싶은 리포지토리 이름(google/iosched 등)
         */
        fun start(context: Context, fullRepositoryName: String) {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(EXTRA_FULL_REPOSITORY_NAME, fullRepositoryName)
            context.startActivity(intent)
        }
    }

}
