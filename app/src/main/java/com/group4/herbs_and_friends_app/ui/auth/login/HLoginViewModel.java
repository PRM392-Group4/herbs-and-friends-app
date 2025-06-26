package com.group4.herbs_and_friends_app.ui.auth.login;

import androidx.lifecycle.ViewModel;

import com.group4.herbs_and_friends_app.data.repository.AuthRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HLoginViewModel extends ViewModel {

    private final AuthRepository authRepository;

    @Inject
    public HLoginViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }
}