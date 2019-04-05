package com.app.xeross.xerfirebase.auth;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.xeross.xerfirebase.R;
import com.app.xeross.xerfirebase.api.UserHelper;
import com.app.xeross.xerfirebase.base.BaseActivity;
import com.app.xeross.xerfirebase.models.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by XeroSs on 31/03/2019.
 */
public class ProfileActivity extends BaseActivity {

    // -------------------------------------------------------------------------------------------- //
    // ------------------------------------------ FIELDS ------------------------------------------ //
    // -------------------------------------------------------------------------------------------- //

    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;
    private static final int UPDATE_USERNAME = 30;
    @BindView(R.id.profile_activity_imageview_profile)
    ImageView imageViewProfile;
    @BindView(R.id.profile_activity_edit_text_username)
    EditText textInputEditTextUsername;
    @BindView(R.id.profile_activity_text_view_email)
    TextView textViewEmail;
    @BindView(R.id.profile_activity_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.checkbox_mentor)
    CheckBox checkbox;

    // -------------------------------------------------------------------------------------------- //
    // ----------------------------------------- ACTIVITY ----------------------------------------- //
    // -------------------------------------------------------------------------------------------- //

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.confToolbar();
        this.updateUIWhenCreating();
    }

    // Layout activity
    @Override
    public int getFragmentLayout() {
        return R.layout.activity_profile;
    }

    // -------------------------------------------------------------------------------------------- //
    // --------------------------------------- ENCLENCHEURS --------------------------------------- //
    // -------------------------------------------------------------------------------------------- //

    // Interraction avec le boutton de suppression de compte
    @OnClick(R.id.profile_activity_button_delete)
    public void onClickDeleteButton() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.popup_message_confirmation_delete_account)
                .setPositiveButton(R.string.popup_message_choice_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteUserFromFirebase();
                    }
                })
                .setNegativeButton(R.string.popup_message_choice_no, null)
                .show();
    }

    // Interraction avec le boutton de mise à jour
    @OnClick(R.id.profile_activity_button_update)
    public void onClickUpdateButton() {
        this.updateUsernameInFirebase();
    }

    // Interraction avec la checkbox
    @OnClick(R.id.checkbox_mentor)
    public void onClickCheckBoxIsMentor() {
        this.updateUserIsMentor();
    }

    // Interraction avec le boutton de déconnexion
    @OnClick(R.id.profile_activity_button_sign_out)
    public void onClickSignOutButton() {
        this.signOutUserFromFirebase();
    }

    // -------------------------------------------------------------------------------------------- //
    // ----------------------------------------- METHODES ----------------------------------------- //
    // -------------------------------------------------------------------------------------------- //


    // Suppression du compte de l'utilisateur
    private void deleteUserFromFirebase() {
        if (this.getCurrentUser() != null) {
            AuthUI.getInstance()
                    .delete(this)
                    .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK));

            UserHelper.deleteUser(this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener());

        }
    }

    // Deconnexion du compte
    private void signOutUserFromFirebase() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

    // Mise à jour du nom de l'utilisateur
    private void updateUsernameInFirebase() {

        this.progressBar.setVisibility(View.VISIBLE);
        String username = this.textInputEditTextUsername.getText().toString();

        if (this.getCurrentUser() != null) {
            if (!username.isEmpty() && !username.equals(getString(R.string.info_no_username_found))) {
                UserHelper.updateUsername(username,
                        this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_USERNAME));
            }
        }
    }

    // Mise à jour de la checkBox
    private void updateUserIsMentor() {
        if (this.getCurrentUser() != null) {
            UserHelper.updateIsMentor(this.getCurrentUser().getUid(), this.checkbox.isChecked()).addOnFailureListener(this.onFailureListener());
        }
    }

    // Mise à jour de l'UI pour l'utilisateur
    private void updateUIWhenCreating() {

        if (this.getCurrentUser() != null) {

            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageViewProfile);
            } else {
                Glide.with(this)
                        .load(R.drawable.image_not_found)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageViewProfile);
            }

            String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();

            this.textViewEmail.setText(email);

            UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User currentUser = documentSnapshot.toObject(User.class);
                    String username = TextUtils.isEmpty(currentUser.getUsername()) ? getString(R.string.info_no_username_found) : currentUser.getUsername();
                    checkbox.setChecked(currentUser.getIsMentor());
                    textInputEditTextUsername.setText(username);
                }
            });
        }
    }

    // -
    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin) {
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin) {
                    case UPDATE_USERNAME:
                        progressBar.setVisibility(View.INVISIBLE);
                        break;
                    case SIGN_OUT_TASK:
                        finish();
                        break;
                    case DELETE_USER_TASK:
                        finish();
                        break;
                    default:
                        break;
                }
            }
        };
    }
}


