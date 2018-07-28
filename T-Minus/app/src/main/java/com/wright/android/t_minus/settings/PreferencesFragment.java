package com.wright.android.t_minus.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.wright.android.t_minus.R;
import com.wright.android.t_minus.business.apply.BusinessApplyActivity;
import com.wright.android.t_minus.TextFieldUtils;
import com.wright.android.t_minus.business.edit.BusinessEditActivity;
import com.wright.android.t_minus.network_connection.NetworkUtils;
import com.wright.android.t_minus.settings.account.LoginActivity;

import static android.app.Activity.RESULT_OK;

public class PreferencesFragment extends PreferenceFragment {
    public static final String PREFS = "SharedPreferences.PREFS";
    public static final String NOTIF_PREF = "notif_preference";
    public static final String PASSWORD_PREF = "password_pref";
    public static final String EMAIL_PREF = "email_pref";
    public static final int LOG_IN_CODE = 0x0010;
    public static final int APPLY_BUSINESS_CODE = 0x0100;
    private String dateFormat;
    private PreferencesActivity mActivity;
    private FirebaseAuth mAuth;

    public PreferencesFragment() {
    }

    public static PreferencesFragment newInstance() {
        Bundle args = new Bundle();
        PreferencesFragment fragment = new PreferencesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setActivity(PreferencesActivity _activity){
        mActivity = _activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        addPreferencesFromResource(R.xml.preferances);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(NetworkUtils.isConnected(getContext())){
            getPreferenceScreen().setEnabled(true);
            updateButtons();
        }else{
            if(getView() != null) {
                getPreferenceScreen().setEnabled(false);
                Snackbar.make(getView(), "No internet connection", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Reload", (View v) -> onStart()).show();
            }
        }
    }

    private void updateButtons(){
        if(getView()==null || mAuth == null){
            return;
        }
        FirebaseUser _currentUser = mAuth.getCurrentUser();
        Button signIn = getView().getRootView().findViewById(R.id.account_sign_in);
        Button signOut = getView().getRootView().findViewById(R.id.account_sign_out);
        signIn.setOnClickListener((View v)->{
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivityForResult(intent, LOG_IN_CODE);
        });
        signOut.setOnClickListener((View v)->{
            mAuth.signOut();
            updateButtons();
        });

        Boolean userSigned = _currentUser != null;
        signIn.setVisibility(userSigned ? View.GONE:View.VISIBLE);
        signOut.setVisibility(userSigned ? View.VISIBLE:View.GONE);
        if(userSigned) {
            getIfAdmin();
        }else {
            getView().getRootView().findViewById(R.id.account_view_businesses).setVisibility(View.GONE);
            getView().getRootView().findViewById(R.id.account_apply_business).setVisibility(View.GONE);
        }
    }

    private void getIfAdmin(){
        if(mAuth.getUid() == null){
            return;
        }
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isAdmin = false;
                boolean inProgress = false;
                if(dataSnapshot.hasChild("admin")){
                    isAdmin = (boolean) dataSnapshot.child("admin").getValue();
                }
                if(!isAdmin && dataSnapshot.hasChild("admin_in_progress")){
                    inProgress = (boolean) dataSnapshot.child("admin_in_progress").getValue();
                }
                setBusinessButtons(isAdmin, inProgress);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setBusinessButtons(boolean _isAdmin, boolean _inProgress){
        if(getView() == null || mAuth == null){
            return;
        }
        Button viewBusi = getView().getRootView().findViewById(R.id.account_view_businesses);
        Button applyBusi = getView().getRootView().findViewById(R.id.account_apply_business);

        viewBusi.setOnClickListener((View v)->{
            Intent intent = new Intent(getContext(), BusinessEditActivity.class);
            startActivity(intent);
        });
        applyBusi.setOnClickListener((View v)->{
            if(_inProgress){
                new AlertDialog.Builder(mActivity)
                        .setTitle("Verification In Progress")
                        .setMessage("Your business is currently being reviewed by the Go/No-Go team. Thank you for your patience.")
                        .setPositiveButton("Okay", null).show();
            }else {
                Intent intent = new Intent(getContext(), BusinessApplyActivity.class);
                startActivityForResult(intent, APPLY_BUSINESS_CODE);
            }
        });
        applyBusi.setVisibility(_isAdmin ? View.GONE : View.VISIBLE);
        viewBusi.setVisibility(_isAdmin&&!_inProgress ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Preference notifPref = findPreference(NOTIF_PREF);
        Preference emailPref = findPreference(EMAIL_PREF);
        Preference passwordPref = findPreference(PASSWORD_PREF);
        if(emailPref != null && passwordPref != null){
            if(mAuth.getCurrentUser() !=null){
                String email = mAuth.getCurrentUser().getEmail();
                emailPref.setDefaultValue(email);
            }else{
                PreferenceCategory accountPref = (PreferenceCategory)findPreference("account_settings");
//                accountPref.removeAll();
                getPreferenceScreen().removePreference(accountPref);
            }
            passwordPref.setOnPreferenceClickListener(mPrefClick);
            emailPref.setOnPreferenceChangeListener(mPrefChanged);
        }
        if(notifPref != null){
            notifPref.setOnPreferenceChangeListener(mPrefChanged);
        }

    }

    private final Preference.OnPreferenceClickListener mPrefClick = (Preference preference) -> {
        Dialog passwordDialog = new Dialog(getContext());
        View popup = LayoutInflater.from(getContext()).inflate(R.layout.fragment_password_change, null);
        popup.findViewById(R.id.change_save).setOnClickListener((View v) -> attemptPasswordChange(passwordDialog, popup));
        popup.findViewById(R.id.change_cancel).setOnClickListener((View v) -> passwordDialog.dismiss());
        passwordDialog.setTitle("Password Reset");
        passwordDialog.setContentView(popup);
        passwordDialog.setCancelable(true);
        passwordDialog.show();
        return false;
    };

    private void attemptPasswordChange(Dialog passwordDialog, View view) {
        if(mAuth.getCurrentUser() == null){
            return;
        }
        EditText currentPassword =  view.findViewById(R.id.change_current_password);
        EditText newPassword = view.findViewById(R.id.change_new_password);
        EditText retypePassword = view.findViewById(R.id.change_retype_password);

        currentPassword.setError(null);
        newPassword.setError(null);
        retypePassword.setError(null);

        // Store values at the time of the login attempt.
        String currentP = currentPassword.getText().toString();
        String newP = newPassword.getText().toString();
        String reP = retypePassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(reP) && TextFieldUtils.isPasswordInvalid(reP)) {
            retypePassword.setError(getString(R.string.error_invalid_password));
            focusView = retypePassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(newP) && TextFieldUtils.isPasswordInvalid(newP) && !newP.equals(reP)) {
            retypePassword.setError(getString(R.string.error_invalid_password));
            focusView = retypePassword;
            cancel = true;
        }

        //TODO: FIX THIS
        if (TextUtils.isEmpty(currentP) && TextFieldUtils.isPasswordInvalid(currentP)) {
            retypePassword.setError(getString(R.string.error_invalid_password));
            focusView = retypePassword;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            mAuth.getCurrentUser().updatePassword(newP).addOnSuccessListener((Void aVoid) -> {
                Toast.makeText(getContext(), "Password has been changed", Toast.LENGTH_SHORT).show();
                passwordDialog.dismiss();
            }).addOnFailureListener((@NonNull Exception e) -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Unexpected Error")
                        .setMessage(e.getMessage())
                        .setNeutralButton("ok", null).show();
            });
        }
    }

    private final Preference.OnPreferenceChangeListener mPrefChanged = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            switch (preference.getKey()){
                case NOTIF_PREF:
                    dateFormat = newValue.toString();
                    break;
                case EMAIL_PREF:
                    String email = (String) newValue;
                    if(mAuth.getCurrentUser()!=null){
                        if(TextFieldUtils.isEmailValid(email)) {
                            mAuth.getCurrentUser().updateEmail(email);
                            Toast.makeText(getContext(), "Email successfully changed.", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getContext(), "Email entered was not valid.", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }else {
                        Toast.makeText(getContext(), "Must be signed in to do this action.", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    break;
            }
            return true;
        }
    };

    public void writeCustomPrefs() {
        SharedPreferences customPrefs = mActivity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = customPrefs.edit();
        editor.putString(NOTIF_PREF, dateFormat);
        editor.apply();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            updateButtons();
        }
    }
}