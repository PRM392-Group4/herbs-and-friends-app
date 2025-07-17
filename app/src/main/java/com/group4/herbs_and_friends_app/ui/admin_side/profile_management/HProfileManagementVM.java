package com.group4.herbs_and_friends_app.ui.admin_side.profile_management;

import androidx.lifecycle.ViewModel;

import com.group4.herbs_and_friends_app.data.model.User;
import com.group4.herbs_and_friends_app.data.repository.AuthRepository;

import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HProfileManagementVM extends ViewModel {
    private final AuthRepository authRepository;

    @Inject
    public HProfileManagementVM(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void fetchUser(String uid, Consumer<User> onUserLoaded, Runnable onFailure) {
        authRepository.getUserByUid(uid, onUserLoaded, onFailure);
    }
}