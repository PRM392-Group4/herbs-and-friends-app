package com.group4.herbs_and_friends_app.di;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4.herbs_and_friends_app.data.repository.AuthRepository;
import com.group4.herbs_and_friends_app.data.repository.ProductRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    // =========================
    // === Firebase Services
    // =========================

    @Provides
    @Singleton
    public FirebaseFirestore provideFirestore() {
        return FirebaseFirestore.getInstance();
    }

    @Provides
    @Singleton
    public FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    // =========================
    // === Repositories
    // =========================

    @Provides
    @Singleton
    public AuthRepository provideAuthRepository(FirebaseAuth firebaseAuth) {
        return new AuthRepository(firebaseAuth);
    }

    @Provides
    @Singleton
    public ProductRepository provideProductRepository(FirebaseFirestore firestore) {
        return new ProductRepository(firestore);
    }
}
