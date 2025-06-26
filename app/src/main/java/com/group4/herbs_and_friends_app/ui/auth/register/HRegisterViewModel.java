package com.group4.herbs_and_friends_app.ui.auth.register;

import androidx.lifecycle.ViewModel;

import com.group4.herbs_and_friends_app.data.repository.AuthRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HRegisterViewModel extends ViewModel {

    private final AuthRepository authRepository;

    @Inject
    public HRegisterViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }
}