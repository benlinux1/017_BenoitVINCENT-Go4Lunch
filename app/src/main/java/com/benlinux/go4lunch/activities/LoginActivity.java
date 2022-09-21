package com.benlinux.go4lunch.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.benlinux.go4lunch.R;
import com.benlinux.go4lunch.databinding.ActivityLoginBinding;
import com.benlinux.go4lunch.ui.userManager.UserManager;


import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.FirebaseUiException;
import com.firebase.ui.auth.IdpResponse;

import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;

import com.google.android.material.snackbar.Snackbar;


import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    // FOR DATA
    private final UserManager userManager = UserManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkIfUserIsConnected();
    }

    // Create callback for Firebase authentication result
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    // Firebase SignIn
    private void startSignInActivity(){
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build());

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.LoginTheme)
                .setLogo(R.drawable.logo_login)
                .setIsSmartLockEnabled(false)
                .setTosAndPrivacyPolicyUrls("https://www.privacypolicies.com/live/629a883b-14b9-4ee7-ade0-b6a2b6e9b2d1",
                        "https://www.privacypolicies.com/live/629a883b-14b9-4ee7-ade0-b6a2b6e9b2d1")
                .build();
        signInLauncher.launch(signInIntent);
    }

    // Handler for response after SignIn Activity close
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            startMainActivity();
            finish();
            Toast.makeText(getApplicationContext(), getString(R.string.connection_succeed), Toast.LENGTH_SHORT).show();
        } else {
            // ERRORS
            if (response == null) {
                showSnackBar(getString(R.string.error_authentication_canceled));
            } else if (response.getError()!= null) {
                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK){
                    showSnackBar(getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(getString(R.string.error_unknown_error));
                } else {
                    showSnackBar(getString(R.string.error_unknown_error));
                }
            }
        }
    }

    // Show Snack Bar with a message
    private void showSnackBar( String message){
        View container = findViewById(R.id.main_container);
        Snackbar.make(container, message, Snackbar.LENGTH_SHORT).show();
    }


    // Check user status & redirect to MainActivity if logged
    private void checkIfUserIsConnected(){
        if(userManager.isCurrentUserLogged()) {
            startMainActivity();
        } else {
            startSignInActivity();
        }
    }

    // Launch Main Activity
    private void startMainActivity() {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        ActivityCompat.startActivity(this, mainActivityIntent, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}