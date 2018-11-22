package com.onedelay.githubrepo.contract;

import com.onedelay.githubrepo.model.GitHubService;

/**
 * 각각 역할을 가진 Contract(계약)를 정의해두는 인터페이스
 */
public interface DetailContract {

    /**
     * MVP 의 View 가 구현할 인터페이스
     * Presenter 가 View 를 조작할 때 이용한다
     */
    interface View {
        String getFullRepositoryName();

        void showRepositoryInfo(GitHubService.RepositoryItem response);

        void startBrowser(String url);

        void showError(String message);
    }

    /**
     * MVP 의 Presenter 가 구현할 인터페이스
     * View 를 클릭했을 때 등 View 가 Presenter 에 알리기 위해 이용한다
     */
    interface UserActions {
        void titleClick();

        void prepare();
    }
}
