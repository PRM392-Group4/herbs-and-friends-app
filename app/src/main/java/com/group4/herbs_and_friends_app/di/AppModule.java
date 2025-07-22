package com.group4.herbs_and_friends_app.di;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.group4.herbs_and_friends_app.data.communication.FCMClient;
import com.group4.herbs_and_friends_app.data.communication.NotificationConsumer;
import com.group4.herbs_and_friends_app.data.communication.NotificationPublisher;
import com.group4.herbs_and_friends_app.data.repository.AuthRepository;
import com.group4.herbs_and_friends_app.data.repository.CartRepository;
import com.group4.herbs_and_friends_app.data.repository.CategoryRepository;
import com.group4.herbs_and_friends_app.data.repository.CouponRepository;
import com.group4.herbs_and_friends_app.data.repository.DevicePushNotificationTokenRepository;
import com.group4.herbs_and_friends_app.data.repository.OrderRepository;
import com.group4.herbs_and_friends_app.data.repository.ProductRepository;
import com.group4.herbs_and_friends_app.di.PermissionManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    // =========================
    // === Firebase Services
    // =========================

    @Provides
    @Singleton
    public FirebaseDatabase provideNotificationDatabaseReference() {
        return  FirebaseDatabase.getInstance("https://modern-environs-437216-m4-default-rtdb.asia-southeast1.firebasedatabase.app");
    }
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
    public FirebaseMessaging provideFirebaseMessaging() { return FirebaseMessaging.getInstance(); }

    @Provides
    @Singleton
    public FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Provides
    @Singleton
    public FirebaseApp provideFirebaseApp() { return FirebaseApp.getInstance(); }

    // =========================
    // === Repositories
    // =========================
    @Provides
    @Singleton
    public AuthRepository provideAuthRepository(FirebaseAuth firebaseAuth, FirebaseFirestore firestore, ResourceManager resourceManager) {
        return new AuthRepository(firebaseAuth, firestore, resourceManager);
    }

    @Provides
    @Singleton
    public ProductRepository provideProductRepository(FirebaseFirestore firestore, FirebaseStorage storage) {
        return new ProductRepository(firestore, storage);
    }

    @Provides
    @Singleton
    public CartRepository provideCartRepository(FirebaseFirestore firestore, FirebaseAuth firebaseAuth) {
        return new CartRepository(firestore, firebaseAuth);
    }

    @Provides
    @Singleton
    public CategoryRepository provideCategoryRepository(FirebaseFirestore firestore) {
        return new CategoryRepository(firestore);
    }

    @Provides
    @Singleton
    public OrderRepository provideOrderRepository(FirebaseFirestore firestore, FirebaseAuth firebaseAuth, ProductRepository productRepository, CouponRepository couponRepository) {
        return new OrderRepository(firestore, firebaseAuth, productRepository, couponRepository);
    }

    @Provides
    @Singleton
    public CouponRepository provideCouponRepository(FirebaseFirestore firestore) {
        return new CouponRepository(firestore);
    }

    @Provides
    @Singleton
    public DevicePushNotificationTokenRepository notificationTokenRepository(FirebaseFirestore firestore) {
        return new DevicePushNotificationTokenRepository(firestore);
    }

    // =========================
    // === Utilities
    // =========================
    @Provides
    @Singleton
    public ResourceManager provideResourceManager(@ApplicationContext Context context) {
        return new ResourceManager(context);
    }

    @Provides
    @Singleton
    public PermissionManager providePermissionManager(@ApplicationContext Context context) {
        return new PermissionManager(context);
    }

    // =========================
    // === Communication
    // =========================

    @Provides
    @Singleton
    public NotificationPublisher provideNotificationPublisher(FirebaseDatabase firebaseRealtimeInstance,
                                                              DevicePushNotificationTokenRepository notificationTokenRepository) {
        return new NotificationPublisher(firebaseRealtimeInstance, notificationTokenRepository);
    }

    @Provides
    @Singleton
    public NotificationConsumer provideNotificationConsumer(FirebaseDatabase firebaseRealtimeInstance, 
                                                            FirebaseAuth firebaseAuthInstance, 
                                                            DevicePushNotificationTokenRepository notificationTokenRepository, 
                                                            FirebaseMessaging firebaseMessaging, 
                                                            PermissionManager permissionManager) {
        return new NotificationConsumer(firebaseRealtimeInstance, 
                                        firebaseAuthInstance, 
                                        notificationTokenRepository, 
                                        firebaseMessaging, 
                                        permissionManager);
    }
}
