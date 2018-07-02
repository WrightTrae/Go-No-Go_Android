// Trae Wright
// JAV2 - C201803
// PreferancesFragment
package com.wright.android.t_minus.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wright.android.t_minus.R;
import com.wright.android.t_minus.settings.account.EmailUtils;
import com.wright.android.t_minus.settings.account.LoginActivity;

public class PreferancesFragment extends PreferenceFragment {
    public static final String PREFS = "SharedPreferences.PREFS";
    public static final String NOTIF_PREF = "notif_preference";
    public static final String PASSWORD_PREF = "password_pref";
    public static final String EMAIL_PREF = "email_pref";
    public static final int LOG_IN_CODE = 0x0010;
    private String dateFormat;
    private PreferencesActivity mActivity;
    private FirebaseAuth mAuth;

    public PreferancesFragment() {
    }

    public static PreferancesFragment newInstance() {

        Bundle args = new Bundle();

        PreferancesFragment fragment = new PreferancesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setActivity(PreferencesActivity _mactivity){
        mActivity = _mactivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        mAuth = FirebaseAuth.getInstance();
        addPreferencesFromResource(R.xml.preferances);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateButtons();
    }

    private void updateButtons(){
        if(getView()==null || mAuth == null){
            return;
        }
        FirebaseUser _currentUser = mAuth.getCurrentUser();
        Preference notifPref = findPreference(NOTIF_PREF);
        Preference passwordPref = findPreference(PASSWORD_PREF);
        Button viewBusi = getView().getRootView().findViewById(R.id.account_view_businesses);
        Button applyBusi = getView().getRootView().findViewById(R.id.account_apply_business);
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
        notifPref.setShouldDisableView(!userSigned);
        passwordPref.setShouldDisableView(!userSigned);
        signIn.setVisibility(userSigned ? View.GONE:View.VISIBLE);
        signOut.setVisibility(userSigned ? View.VISIBLE:View.GONE);
        applyBusi.setVisibility(userSigned ? View.GONE:View.GONE);
        viewBusi.setVisibility(userSigned ? View.GONE:View.GONE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Preference notifPref = findPreference(NOTIF_PREF);
        Preference emailPref = findPreference(EMAIL_PREF);
        Preference passwordPref = findPreference(PASSWORD_PREF);
        if(emailPref!=null){
            if(mAuth.getCurrentUser() !=null){
                emailPref.setDefaultValue(mAuth.getCurrentUser().getEmail());
            }
            emailPref.setOnPreferenceChangeListener(mPrefChanged);
        }
        if (passwordPref!=null){
            passwordPref.setOnPreferenceClickListener(mPrefClick);
        }
        if(notifPref != null){
            notifPref.setOnPreferenceChangeListener(mPrefChanged);
        }

    }

    private final Preference.OnPreferenceClickListener mPrefClick = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
            alertDialogBuilder.setTitle(preference.getTitle());
            alertDialogBuilder
                    .setMessage("A email will be sent to you shortly, follow the instruction to reset your password.")
                    .setCancelable(false)
                    .setPositiveButton("reset password", (DialogInterface dialog, int id) -> {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user != null && user.getEmail() != null) {
                            mAuth.sendPasswordResetEmail(user.getEmail());
                        }
                    })
                    .setNegativeButton("cancel", null);
            alertDialogBuilder.create().show();
            return false;
        }
    };

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
                        if(EmailUtils.isEmailValid(email)) {
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
        switch (requestCode){
            case LOG_IN_CODE:
                updateButtons();
                break;
        }
    }
}
