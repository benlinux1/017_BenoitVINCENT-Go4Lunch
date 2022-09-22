package com.benlinux.go4lunch.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.benlinux.go4lunch.R;
import com.benlinux.go4lunch.ui.userManager.UserManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;


public class SettingsActivity extends AppCompatActivity {

    private ImageView userAvatar;
    private ImageView updateAvatar;
    private TextInputLayout userNameLayout;
    private EditText userName;
    private TextInputLayout userEmailLayout;
    private EditText userEmail;
    private Button deleteButton;
    private Button updateButton;
    private Toolbar mToolbar;

    // FOR DATA
    private final UserManager userManager = UserManager.getInstance();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setToolbar();
        setViews();
        updateUIWithUserData();
        setDeleteButtonListener();
    }

    private void setToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Close settings page and turn back to main activity if back button is clicked
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            MainActivity.navigate(this);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Define views
    private void setViews() {
        userAvatar = findViewById(R.id.settings_user_avatar);
        updateAvatar = findViewById(R.id.settings_user_avatar_update);
        userName = findViewById(R.id.settings_user_name_field);
        userEmail = findViewById(R.id.settings_user_email_field);
        deleteButton = findViewById(R.id.settings_delete_button);
        updateButton = findViewById(R.id.settings_update_button);
    }

    // Set User data in fields
    private void updateUIWithUserData(){
        if(userManager.isCurrentUserLogged()) {
            FirebaseUser user = userManager.getCurrentUser();

            if (user.getPhotoUrl() != null) {
                setProfilePicture(user.getPhotoUrl());
            } else {
               setNoPhoto(userAvatar);
            }
            setTextUserData(user);
        }
    }

    private void setProfilePicture(Uri profilePictureUrl){
        Glide.with(this)
            .load(profilePictureUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(userAvatar);
    }

    private void setNoPhoto(ImageView destination) {
        Glide.with(this)
            .load(R.mipmap.no_photo)
            .apply(RequestOptions.circleCropTransform())
            .into(destination);
    }

    private void setTextUserData(FirebaseUser user){
        //Get email & username from User
        String email = TextUtils.isEmpty(user.getEmail()) ? getString(R.string.info_no_email_found) : user.getEmail();
        String username = TextUtils.isEmpty(user.getDisplayName()) ? getString(R.string.info_no_username_found) : user.getDisplayName();

        //Update views with data
        userName.setText(username);
        userEmail.setText(email);
    }

    private void setDeleteButtonListener() {
        deleteButton.setOnClickListener(v -> {
            deleteAccountDialog();
        });
    }

    private void deleteAccountDialog() {
        new AlertDialog.Builder(this)
            .setMessage(R.string.popup_message_confirmation_delete_account)
            .setPositiveButton(R.string.popup_message_choice_yes, (dialogInterface, i) ->
                userManager.deleteUser(SettingsActivity.this)
                    .addOnSuccessListener(aVoid -> {
                        finish();
                        Intent loginActivityIntent = new Intent(this, LoginActivity.class);
                        ActivityCompat.startActivity(this, loginActivityIntent, null);
                        Toast.makeText(getApplicationContext(), getString(R.string.delete_account_succeed), Toast.LENGTH_SHORT).show();
                    })
                    // On failure, show error toast
                    .addOnFailureListener(aVoid -> {
                        Toast.makeText(getApplicationContext(), getString(R.string.delete_account_failed), Toast.LENGTH_SHORT).show();
                    }
                )
            )
            .setNegativeButton(R.string.popup_message_choice_no, null)
            .show();
    }
}
