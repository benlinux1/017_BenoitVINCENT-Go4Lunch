package com.benlinux.go4lunch.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.benlinux.go4lunch.R;
import com.benlinux.go4lunch.data.userManager.UserManager;
import com.benlinux.go4lunch.ui.models.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class SettingsActivity extends AppCompatActivity {

    private ImageView userAvatar;

    private EditText userName;
    private TextView userEmail;
    private Button deleteButton;
    private Button updateButton;
    private Toolbar mToolbar;
    private SwitchCompat switchNotifications;
    private ImageView updateAvatarButton;

    // FOR DATA
    private final UserManager userManager = UserManager.getInstance();

    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    private Uri uriImageSelected;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setToolbar();
        setViews();
        updateUIWithUserData();
        setNotificationSwitch();
        setDeleteButtonListener();
        setUpdateButtonListener();
        setUpdateAvatarButtonListener();
    }

    private void setToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");
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
    @SuppressLint("ClickableViewAccessibility")
    private void setViews() {
        userAvatar = findViewById(R.id.settings_user_avatar);
        updateAvatarButton = findViewById(R.id.settings_user_avatar_update);
        userName = findViewById(R.id.settings_user_name_field);
        userEmail = findViewById(R.id.settings_user_email_field);
        deleteButton = findViewById(R.id.settings_delete_button);
        updateButton = findViewById(R.id.settings_update_button);
        switchNotifications = findViewById(R.id.settings_notification_switch);
    }

    // Set User data in fields
    private void updateUIWithUserData(){
        // If user is logged
        if(userManager.isCurrentUserLogged()){
            getUserData();
        }
    }

    private void getUserData(){
        Task<User> getData = userManager.getUserData();
        getData.addOnSuccessListener(user -> {
            // Set user name
            String username = TextUtils.isEmpty(user.getName()) ? getString(R.string.info_no_username_found) : user.getName();
            userName.setText(username);
            // User email
            String email = TextUtils.isEmpty(user.getEmail()) ? getString(R.string.info_no_email_found) : user.getEmail();
            userEmail.setText(email);
            // Set notification switch
            switchNotifications.setChecked(Boolean.TRUE.equals(user.isNotified()));
            // Set Avatar picture
            setProfilePicture(user.getAvatar());

        });
        getData.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("EXCEPTION", e.toString());
            }
        });
    }

    private void setNotificationSwitch(){
        switchNotifications.setOnCheckedChangeListener((compoundButton, checked) -> {
            userManager.updateIsNotified(checked);
        });
    }


    private void setProfilePicture(@Nullable String profilePictureUrl){
        if (profilePictureUrl != null) {
            Glide.with(this)
                .load(profilePictureUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(userAvatar);
        } else {
            Glide.with(this)
                .load(R.mipmap.no_photo)
                .apply(RequestOptions.circleCropTransform())
                .into(userAvatar);
        }
    }

    private void setDeleteButtonListener() {
        deleteButton.setOnClickListener(v -> {
            deleteAccountDialog();
        });
    }

    private void setUpdateButtonListener() {
        updateButton.setOnClickListener(v -> {
            userManager.updateUsername(userName.getText().toString());
            if (uriImageSelected != null) {
                userManager.updateUserAvatarUrl(uriImageSelected);
            }
            Toast.makeText(this, "Account update successful !", Toast.LENGTH_SHORT).show();
        });
    }

    private void setUpdateAvatarButtonListener() {
        updateAvatarButton.setOnClickListener(v -> {
            updateAvatarPicture();
        });
    }

    // Easy permission result for photo access
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    // When photo access is granted
    @AfterPermissionGranted(RC_IMAGE_PERMS)
    private void updateAvatarPicture() {
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, "Please allow pictures access", RC_IMAGE_PERMS, PERMS);
            return;
        }
        Toast.makeText(this, "You can choose a picture !", Toast.LENGTH_SHORT).show();
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        actionPick.launch(pickPhotoIntent);
    }


    // Create callback when user pick a photo on his device
    private final ActivityResultLauncher<Intent> actionPick = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    onPickPhotoResult(result);
                }
            }
    );

    // Handle result of photo picking activity
    private void onPickPhotoResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) { //SUCCESS
            assert result.getData() != null;
            this.uriImageSelected = result.getData().getData();
            Glide.with(this) //SHOWING PREVIEW OF IMAGE
                .load(this.uriImageSelected)
                .apply(RequestOptions.circleCropTransform())
                .into(userAvatar);
        } else {
            Toast.makeText(this, "No image chosen", Toast.LENGTH_SHORT).show();
        }
    }


    // Alert Dialog to delete account from firestore & firebase
    private void deleteAccountDialog() {
        new AlertDialog.Builder(this)
            .setMessage(R.string.popup_message_confirmation_delete_account)
            .setPositiveButton(R.string.popup_message_choice_yes, (dialogInterface, i) ->
                userManager.deleteUserFromFirestore(SettingsActivity.this)
                    // On success, go to login activity
                    .addOnSuccessListener(aVoid -> {
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
