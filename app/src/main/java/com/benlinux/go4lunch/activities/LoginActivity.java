package com.benlinux.go4lunch.activities;

import android.content.Intent;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.benlinux.go4lunch.R;
import com.benlinux.go4lunch.databinding.ActivityLoginBinding;
import com.benlinux.go4lunch.data.userManager.UserManager;

import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;

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

        // Avoid login conflict error when facebook app is installed on device
        LoginManager.getInstance().setLoginBehavior(LoginBehavior.WEB_ONLY);

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
                new AuthUI.IdpConfig.FacebookBuilder().setPermissions(Arrays.asList("email", "public_profile")).build(),
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.TwitterBuilder().build());

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.LoginTheme)
                .setLogo(R.drawable.logo_login)
                .setIsSmartLockEnabled(false)
                .build();
        signInLauncher.launch(signInIntent);
    }

    // Handler for response after SignIn Activity close
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            userManager.createUser().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshot) {
                    startMainActivity();
                    finish();
                    Toast.makeText(getApplicationContext(), getString(R.string.connection_succeed), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // ERRORS
            if (response == null) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_authentication_canceled), Toast.LENGTH_SHORT).show();
            } else if (response.getError()!= null) {
                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK){
                    Toast.makeText(getApplicationContext(), getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
                }
            }
        }
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