package com.onedelay.githubrepo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.onedelay.githubrepo.R;
import com.onedelay.githubrepo.model.GitHubService;

import java.util.List;

/**
 * RecyclerView 에서 리포지토리 목록을 표시하기 위한 Adapter 클래스
 * 이 클래스에 의해 RecyclerView 아이템의 View 를 생성하고, View 에 데이터를 넣는다
 */
public class RepositoryAdapter extends RecyclerView.Adapter<RepositoryAdapter.RepoViewHolder> {
    private final OnRepositoryItemClickListener onRepositoryItemClickListener;
    private final Context context;
    private List<GitHubService.RepositoryItem> items;

    RepositoryAdapter(Context context, OnRepositoryItemClickListener onRepositoryItemClickListener) {
        this.context = context;
        this.onRepositoryItemClickListener = onRepositoryItemClickListener;
    }

    /**
     * 리포지토리의 데이터를 설정해서 갱신한다
     *
     * @param items
     */
    void setItemsAndRefresh(List<GitHubService.RepositoryItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    private GitHubService.RepositoryItem getItemAt(int position) {
        return items.get(position);
    }

    /**
     * RecyclerView 아이템의 View 생성과 View 를 유지할 ViewHolder 를 생성
     */
    @NonNull
    @Override
    public RepoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.repo_item, parent, false);
        return new RepoViewHolder(view);
    }

    /**
     * onCreateViewHolder 로 만든ViewHolder 의 View 에
     * setItemsAndRefresh(items)로 설정된 데이터를 넣는다
     */
    @Override
    public void onBindViewHolder(@NonNull final RepoViewHolder holder, final int position) {
        final GitHubService.RepositoryItem item = getItemAt(position);

        // View 가 클릭되면, 클릭된 아이템을 Listener 에 알린다
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRepositoryItemClickListener.onRepositoryItemClick(item);
            }
        });

        holder.repoName.setText(item.name);
        holder.repoDetail.setText(item.description);
        holder.starCount.setText(item.stargazers_count);
        Glide.with(context)
                .asBitmap()
                .load(item.owner.avatar_url)
                .into(new BitmapImageViewTarget(holder.repoImage) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        // 이미지를 동그랗게 만든다
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        holder.repoImage.setImageDrawable(circularBitmapDrawable);
                    }
                });

    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    interface OnRepositoryItemClickListener {
        /**
         * 리포지토리의 아이템이 탭되면 호출된다
         */
        void onRepositoryItemClick(GitHubService.RepositoryItem item);
    }

    /**
     * View 를 저장해 두는 클래스
     */
    static class RepoViewHolder extends RecyclerView.ViewHolder {
        private final TextView repoName;
        private final TextView repoDetail;
        private final ImageView repoImage;
        private final TextView starCount;

        RepoViewHolder(View itemView) {
            super(itemView);
            repoName = (TextView) itemView.findViewById(R.id.repo_name);
            repoDetail = (TextView) itemView.findViewById(R.id.repo_detail);
            repoImage = (ImageView) itemView.findViewById(R.id.repo_image);
            starCount = (TextView) itemView.findViewById(R.id.repo_star);
        }
    }
}
