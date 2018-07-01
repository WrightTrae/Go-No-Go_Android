package com.wright.android.t_minus.Settings.Account;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.wright.android.t_minus.R;

import static com.wright.android.t_minus.MainTabs.Map.CustomMapFragment.TAG;

public class SignupFragment extends Fragment {

    private LoginListener mListener;
    private FirebaseAuth mAuth;
    private EditText mNameView;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    public SignupFragment() {
        // Required empty public constructor
    }

    public static SignupFragment newInstance() {
        SignupFragment fragment = new SignupFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNameView = view.findViewById(R.id.signup_name);
        mEmailView = (AutoCompleteTextView) view.findViewById(R.id.signup_email);
        mPasswordView = (EditText) view.findViewById(R.id.signup_password);
        mPasswordView.setOnEditorActionListener((TextView textView, int id, KeyEvent keyEvent)->
        {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptSignIn();
                    return true;
                }
                return false;
        });

        view.findViewById(R.id.signup_register).setOnClickListener((v)-> attemptSignIn());
        view.findViewById(R.id.signup_email_sign_in_button).setOnClickListener((v)->mListener.OperationSwitch(this));

        mLoginFormView = view.findViewById(R.id.signup_login_form);
        mProgressView = view.getRootView().findViewById(R.id.email_login_progress);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginListener) {
            mListener = (LoginListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private void attemptSignIn() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mNameView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String name = mNameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !EmailUtils.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid Name
        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!EmailUtils.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            setmProgressView(true);
            loginUser(name, email, password);
        }
    }

    private void setmProgressView(Boolean state){
        mProgressView.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    private void loginUser(String name, String email, String password){
        if(getActivity()==null){
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), (@NonNull Task<AuthResult> task)-> {
                setmProgressView(false);
                if (task.isSuccessful()) {
                    mListener.SignUpButton(mAuth.getCurrentUser(), name);
                } else {
                    try
                    {
                        throw task.getException();
                    }
                    catch (FirebaseAuthUserCollisionException existEmail)
                    {
                        Log.d(TAG, "onComplete: exist_email");
                        if(getView() == null){
                            return;
                        }
                        Snackbar.make(getView(), "Email is already in use please sign in", Snackbar.LENGTH_SHORT).
                                setAction("Sign In", (v) -> ToSignIn()).show();
                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "onComplete: " + e.getMessage());
                    }
                }
        });
    }

    private void ToSignIn(){
        mListener.OperationSwitch(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
