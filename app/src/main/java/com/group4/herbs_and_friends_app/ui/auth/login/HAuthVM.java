package com.group4.herbs_and_friends_app.ui.auth.login;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group4.herbs_and_friends_app.data.model.User;
import com.group4.herbs_and_friends_app.data.model.enums.LoginMethod;
import com.group4.herbs_and_friends_app.data.repository.AuthRepository;

import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HAuthVM extends ViewModel {

    // =================================
    // === Fields
    // =================================

    private final AuthRepository authRepository;
    private final MutableLiveData<User> nextDestination = new MutableLiveData<>();

    // =================================
    // === Constructors
    // =================================

    @Inject
    public HAuthVM(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    // =================================
    // === Methods
    // =================================

    public void login(LoginMethod method, String emailOrIdToken, String passwordOrNull,
                      Runnable onSuccess, Runnable onInvalid, Runnable onError) {
        authRepository.login(method, emailOrIdToken, passwordOrNull, onSuccess, onInvalid, onError);
    }

    public void fetchUser(String uid, Consumer<User> onUserLoaded, Runnable onFailure) {
        authRepository.getUserByUid(uid, onUserLoaded, onFailure);
    }

    /**
     * Get the next destination to the UI.
     *
     * @return LiveData of the next destination
     */
    public LiveData<User> getNextDestinationLive() {
        return nextDestination;
    }

    /**
     * After login successful, emit the next destination to the UI.
     *
     * @param uid User ID
     */
    public void fetchUserAndEmitNextDestination(String uid) {

        // If null, meaning logout
        if (uid == null) {
            nextDestination.postValue(null);
            return;
        }

        // Otherwise, fetch the user and emit to the next destination
        authRepository.getUserByUid(uid,
                user -> nextDestination.postValue(user),
                () -> Log.e("HAuthVM", "User not found in Firestore"));
    }
}