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

    public void registerUser(String email, String password, String confirmPassword, Runnable onSuccess, Runnable onFailure, Runnable onEmailExists, Runnable onInvalidPassword) {
        if (!password.equals(confirmPassword) || !isValidPassword(password)) {
            onInvalidPassword.run();
            return;
        }

        authRepository.checkIfEmailExists(email, exists -> {
            if (exists) {
                onEmailExists.run();
            } else {
                authRepository.createUser(email, password, onSuccess, onFailure);

            }
        }, onFailure);
    }

    private boolean isValidPassword(String password) {
        return password.matches("^[A-Za-z0-9]+$");
    }
}
