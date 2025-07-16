package com.group4.herbs_and_friends_app.data.repository;

import android.util.Log;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.data.mail.GmailSender;
import com.group4.herbs_and_friends_app.data.mail.PasswordUtils;
import com.group4.herbs_and_friends_app.data.model.User;
import com.group4.herbs_and_friends_app.data.model.enums.LoginMethod;
import com.group4.herbs_and_friends_app.di.ResourceManager;

import java.security.SecureRandom;
import java.util.Date;
import java.util.function.Consumer;

import javax.annotation.Nullable;
import javax.inject.Inject;

public class AuthRepository {

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;
    private final ResourceManager resourceManager;

    @Inject
    public AuthRepository(FirebaseAuth firebaseAuth, FirebaseFirestore firestore, ResourceManager resourceManager) {
        this.firebaseAuth = firebaseAuth;
        this.firestore = firestore;
        this.resourceManager = resourceManager;
    }

    public void login(LoginMethod method, String emailOrIdToken, String passwordOrNull,
                      Runnable onSuccess, Runnable onInvalidCredentialsOrFailure, Runnable onError) {

        switch (method) {
            case GOOGLE:
                AuthCredential credential = GoogleAuthProvider.getCredential(emailOrIdToken, null);
                firebaseAuth.signInWithCredential(credential)
                        .addOnSuccessListener(authResult -> {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                String uid = firebaseUser.getUid();
                                String email = firebaseUser.getEmail();
                                String name = firebaseUser.getDisplayName();

                                firestore.collection("users")
                                        .document(uid)
                                        .get()
                                        .addOnSuccessListener(documentSnapshot -> {
                                            if (!documentSnapshot.exists()) {
                                                String randomPassword = generateRandomPassword(10);
                                                saveNewUserToFirestore(uid, email, name, randomPassword,
                                                        () -> {
                                                            // Sau khi lưu Firestore thành công → link email/password
                                                            linkEmailPasswordAccount(firebaseUser, email, randomPassword, onSuccess, onInvalidCredentialsOrFailure);
                                                        },
                                                        onInvalidCredentialsOrFailure,
                                                        true,
                                                        null);
                                            } else {
                                                onSuccess.run();
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("LOGIN", "Firestore get failed", e);
                                            onError.run();
                                        });
                            } else {
                                Log.e("LOGIN", "FirebaseUser is null");
                                onInvalidCredentialsOrFailure.run();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("LOGIN", "Sign-in with credential failed", e);
                            onInvalidCredentialsOrFailure.run();
                        });
                break;

            case EMAIL_PASSWORD:
                firebaseAuth.signInWithEmailAndPassword(emailOrIdToken, passwordOrNull)
                        .addOnSuccessListener(authResult -> {
                            FirebaseUser firebaseUser = authResult.getUser();
                            if (firebaseUser != null) {
                                String uid = firebaseUser.getUid();

                                // Lấy thông tin người dùng từ Firestore
                                firestore.collection("users")
                                        .document(uid)
                                        .get()
                                        .addOnSuccessListener(documentSnapshot -> {
                                            if (documentSnapshot.exists()) {
                                                // Có thể set User vào ViewModel hoặc lưu cache
                                                onSuccess.run();
                                            } else {
                                                // User chưa tồn tại trong Firestore
                                                Log.e("LOGIN", "User exists in FirebaseAuth but not in Firestore");
                                                onInvalidCredentialsOrFailure.run();
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("LOGIN", "Firestore fetch error", e);
                                            onError.run();
                                        });

                            } else {
                                Log.e("LOGIN", "FirebaseUser is null after sign-in");
                                onInvalidCredentialsOrFailure.run();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("LOGIN", "FirebaseAuth sign-in failed", e);
                            onInvalidCredentialsOrFailure.run();
                        });
                break;

        }
    }

    public void createUser(String email, String password, Runnable onSuccess, Runnable onFailure) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        String uid = firebaseUser.getUid();
                        saveNewUserToFirestore(uid, email, null, password, onSuccess, onFailure, false, null);
                    } else {
                        Log.e("REGISTER", "User is null after registration");
                        onFailure.run();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("REGISTER", "FirebaseAuth.createUserWithEmailAndPassword failed", e);
                    onFailure.run();
                });
    }

    private void saveNewUserToFirestore(String uid, String email, String name, String plainPassword,
                                        Runnable onSuccess, Runnable onFailure, boolean sendPasswordEmail, @Nullable Runnable afterSaveSuccess) {
        String hashedPassword = PasswordUtils.hashPassword(plainPassword);

        User newUser = new User();
        newUser.setUid(uid);
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setCreatedAt(new Date());
        newUser.setUpdateAt(new Date());
        newUser.setPassword(hashedPassword);
        newUser.setRole("customer");

        if (sendPasswordEmail) {
            sendPasswordEmail(email, name != null ? name : "User", plainPassword);
        } else {
            sendWelcomeEmailOnly(email, name != null ? name : "User");
        }

        firestore.collection("users")
                .document(uid)
                .set(newUser)
                .addOnSuccessListener(aVoid -> onSuccess.run())
                .addOnFailureListener(e -> {
                    Log.e("REGISTER", "Firestore write failed", e);
                    onFailure.run();
                });
    }

    private void sendWelcomeEmailOnly(String toEmail, String userName) {
        new Thread(() -> {
            try {
                String fromEmail = resourceManager.getString(R.string.gmail_sender_email);
                String appPassword = resourceManager.getString(R.string.gmail_app_password);

                String subject = "Welcome to Herbs & Friends!";
                String message = "Hello " + userName + ",\n\n"
                        + "Thank you for registering at Herbs & Friends!\n\n"
                        + "We're excited to have you on board. 😊\n\n"
                        + "Best regards,\n"
                        + "Herbs & Friends Team";

                GmailSender sender = new GmailSender(fromEmail, appPassword);
                sender.sendEmail(toEmail, subject, message);
                Log.d("EMAIL", "Welcome email sent to " + toEmail);
            } catch (Exception e) {
                Log.e("EMAIL", "Failed to send welcome email to " + toEmail, e);
            }
        }).start();
    }

    private void sendPasswordEmail(String toEmail, String userName, String plainPassword) {
        new Thread(() -> {
            try {
                String fromEmail = resourceManager.getString(R.string.gmail_sender_email);
                String appPassword = resourceManager.getString(R.string.gmail_app_password);

                String subject = "Your Account Info – Herbs & Friends";
                String message = "Hello " + userName + ",\n\n"
                        + "Here are your login credentials:\n"
                        + "Email: " + toEmail + "\n"
                        + "Password: " + plainPassword + "\n\n"
                        + "Please change your password after logging in.\n\n"
                        + "Best regards,\n"
                        + "Herbs & Friends Team";

                GmailSender sender = new GmailSender(fromEmail, appPassword);
                sender.sendEmail(toEmail, subject, message);
                Log.d("EMAIL", "Password email sent to " + toEmail);
            } catch (Exception e) {
                Log.e("EMAIL", "Failed to send password email to " + toEmail, e);
            }
        }).start();
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

    public void checkIfEmailExists(String email, Consumer<Boolean> onResult, Runnable onError) {
        firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    onResult.accept(!querySnapshot.isEmpty());
                })
                .addOnFailureListener(e -> {
                    Log.e("REGISTER", "Failed to check email existence", e);
                    onError.run();
                });
    }

    public void resetPassword(String email, Runnable onSuccess, Runnable onEmailNotFound, Runnable onFailure) {
        firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        FirebaseAuth.getInstance()
                                .sendPasswordResetEmail(email)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("RESET", "Password reset email sent");
                                    onSuccess.run();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("RESET", "Failed to send reset email", e);
                                    onFailure.run();
                                });
                    } else {
                        onEmailNotFound.run();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("RESET", "Failed to check email in Firestore", e);
                    onFailure.run();
                });
    }


    private void linkEmailPasswordAccount(FirebaseUser firebaseUser, String email, String password,
                                          Runnable onSuccess, Runnable onFailure) {
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        firebaseUser.linkWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    Log.d("LINK", "Linked email/password successfully");
                    onSuccess.run(); // Gọi onSuccess sau khi liên kết thành công
                })
                .addOnFailureListener(e -> {
                    Log.e("LINK", "Failed to link email/password", e);
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        Log.e("LINK", "Tài khoản đã tồn tại với phương thức khác");
                    }
                    onFailure.run();
                });
    }

    public void getUserByUid(String uid, Consumer<User> onUserLoaded, Runnable onFailure) {
        firestore.collection("users").document(uid)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        User user = snapshot.toObject(User.class);
                        onUserLoaded.accept(user);
                    } else {
                        onFailure.run();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("USER", "Failed to fetch user", e);
                    onFailure.run();
                });
    }

}
