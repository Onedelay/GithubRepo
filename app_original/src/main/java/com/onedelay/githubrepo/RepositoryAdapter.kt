package com.onedelay.githubrepo

import android.graphics.Bitmap
import android.support.v4.graphics.drawable.RoundedBitmapDrawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import kotlinx.android.synthetic.main.repo_item.view.*

/**
 * RecyclerView 에서 리포지토리의 목록을 표시하기 위한 Adapter 클래스
 * 이 클래스로 RecyclerView 의 아이템의 뷰를 생성하고, 뷰에 데이터를 넣는다
 */
class RepositoryAdapter(private val onRepositoryItemClickListener: OnRepositoryItemClickListener) :
    RecyclerView.Adapter<RepoViewHolder>() {
    private var items: List<GitHubService.Companion.RepositoryItem> = listOf()

    /**
     * 리포지토리의 아이템이 탭되면 호출
     */
    interface OnRepositoryItemClickListener {
        fun onRepositoryItemClick(item: GitHubService.Companion.RepositoryItem)
    }

    /**
     * 리포지토리의 데이터를 설정해서 갱신한다
     */
    fun setItemsAndRefresh(items: List<GitHubService.Companion.RepositoryItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    private fun getItemAt(position: Int) = items[position]

    /**
     * RecyclerView 의 아이템 뷰 생성과 뷰를 유지할 ViewHolder 를 생성
     */
    override fun onCreateViewHolder(parent: ViewGroup, position: Int) = RepoViewHolder.create(parent)

    /**
     * onCreateViewHolder 로 만든 ViewHolder 의 뷰에
     * setItemsAndRefresh(items)으로 설정된 데이터를 넣는다
     */
    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        val item = getItemAt(position)
        holder.bind(item)
        // 뷰가 클릭되면 클릭된 아이템을 Listener 에게 알린다
        holder.itemView.setOnClickListener {
            onRepositoryItemClickListener.onRepositoryItemClick(item)
        }
    }

    override fun getItemCount() = items.size
}

class RepoViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        fun create(parent: ViewGroup) =
            RepoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.repo_item, parent, false))
    }

    fun bind(item: GitHubService.Companion.RepositoryItem) {
        itemView.repo_name.text = item.name
        itemView.repo_detail.text = item.description
        itemView.repo_star.text = item.stargazers_count

        Glide.with(itemView.context)
            .asBitmap()
            .load(item.owner.avatar_url)
            .into(object : BitmapImageViewTarget(itemView.repo_image) {
                override fun setResource(resource: Bitmap?) {
                    // 이미지를 동그랗게 만든다
                    val circularBitmapDrawable: RoundedBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(itemView.context.resources, resource)
                    circularBitmapDrawable.isCircular = true
                    itemView.repo_image.setImageDrawable(circularBitmapDrawable)
                }
            })
    }
}