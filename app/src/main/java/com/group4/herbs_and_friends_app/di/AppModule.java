package com.group4.herbs_and_friends_app.di;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.group4.herbs_and_friends_app.data.repository.AuthRepository;
import com.group4.herbs_and_friends_app.data.repository.CategoryRepository;
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
        return FirebaseFirestore.getInstance("herbs");
    }

    @Provides
    @Singleton
    public FirebaseStorage provideFirebaseStorage() { return FirebaseStorage.getInstance("gs://modern-environs-437216-m4.firebasestorage.app"); }

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
    public ProductRepository provideProductRepository(FirebaseFirestore firestore, FirebaseStorage storage) {
        return new ProductRepository(firestore, storage);
    }

    @Provides
    @Singleton
    public CategoryRepository provideCategoryRepository(FirebaseFirestore firestore) {
        return new CategoryRepository(firestore);
    }
}
