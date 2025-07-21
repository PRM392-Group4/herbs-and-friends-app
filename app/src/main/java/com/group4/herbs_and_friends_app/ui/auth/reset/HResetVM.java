package com.group4.herbs_and_friends_app.ui.auth.reset;

import androidx.lifecycle.ViewModel;

import com.group4.herbs_and_friends_app.data.repository.AuthRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HResetVM extends ViewModel {

    private final AuthRepository authRepository;

    @Inject
    public HResetVM(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void resetPassword(String email, Runnable onSuccess, Runnable onEmailNotFound, Runnable onFailure) {
        authRepository.resetPassword(email, onSuccess, onEmailNotFound, onFailure);
    }
}