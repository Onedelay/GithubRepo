package com.onedelay.githubrepo.contract;

import com.onedelay.githubrepo.model.GitHubService;

/**
 * 각자의 역할이 가진 Contract(계약)를 정의해 둘 인터페이스
 */
public interface RepositoryListContract {

    /**
     * MVP 의 View 가 구현할 인터페이스
     * Presenter 가 View 를 조작할 때 이용한다
     */
    interface View {
        String getSelectedLanguage();
        void showProgress();
        void hideProgress();
        void showRepositories(GitHubService.Repositories repositories);
        void showError();
        void startDetailActivity(String fullRepositoryName);
    }

    /**
     * MVP 의 Presenter 가 구현할 인터페이스
     * View 를 클릭했을 때 등 View 가 Presenter 에 알릴 때 이용한다
     */
    interface UserActions {
        void selectLanguage(String language);
        void selectRepositoryItem(GitHubService.RepositoryItem item);
    }

}
