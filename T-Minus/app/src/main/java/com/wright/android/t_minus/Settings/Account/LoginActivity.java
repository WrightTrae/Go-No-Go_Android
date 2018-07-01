package com.wright.android.t_minus.Settings.Account;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wright.android.t_minus.R;
import com.wright.android.t_minus.Settings.PreferancesFragment;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoginListener{

    private LoginFragment loginFragment;
    private SignupFragment signupFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signupFragment = SignupFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.login_frame, signupFragment).commit();
    }

    @Override
    public void OperationSwitch(android.support.v4.app.Fragment fromFragment) {
        if (fromFragment instanceof SignupFragment){
            if(loginFragment == null){
                loginFragment = LoginFragment.newInstance();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.login_frame, loginFragment).commit();
        }else if(fromFragment instanceof LoginFragment){
            getSupportFragmentManager().beginTransaction().replace(R.id.login_frame, signupFragment).commit();
        }
    }

    @Override
    public void SignUpButton(@NonNull FirebaseUser user, @NonNull String _name) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(String.valueOf(user.getUid()));
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", _name);
        userMap.put("email", user.getEmail());
        userRef.setValue(userMap);
        setResult(PreferancesFragment.LOG_IN_CODE);
        finish();
    }

    @Override
    public void LogInButton(@NonNull FirebaseUser user) {
        setResult(PreferancesFragment.LOG_IN_CODE);
        finish();
    }
}

