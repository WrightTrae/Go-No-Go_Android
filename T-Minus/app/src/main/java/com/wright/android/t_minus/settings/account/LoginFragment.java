package com.wright.android.t_minus.settings.account;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.wright.android.t_minus.TextFieldUtils;
import com.wright.android.t_minus.R;

import static com.wright.android.t_minus.main_tabs.map.CustomMapFragment.TAG;

public class LoginFragment extends Fragment {
    private LoginListener mListener;
    private FirebaseAuth mAuth;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEmailView = view.findViewById(R.id.email);

        mPasswordView = view.findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        view.findViewById(R.id.email_sign_in_button).setOnClickListener((v)->attemptLogin());
        view.findViewById(R.id.email_sign_up_button).setOnClickListener((v)->mListener.OperationSwitch(this));

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

    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) && TextFieldUtils.isPasswordInvalid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!TextFieldUtils.isEmailValid(email)) {
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
            loginUser(email, password);
        }
    }

    private void setmProgressView(Boolean state){
        mProgressView.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    private void loginUser(String email, String password){
        if(getActivity()==null){
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), task -> {
            setmProgressView(false);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            if (task.isSuccessful()) {
                if (mAuth.getCurrentUser() == null){
                    showUnexpectedError(alertDialogBuilder);
                    return;
                }
                mListener.LogInButton(mAuth.getCurrentUser());
            } else {
                if(task.getException() == null){
                    showUnexpectedError(alertDialogBuilder);
                }
                try
                {
                    throw task.getException();
                }
                catch (FirebaseAuthInvalidUserException e){
                    alertDialogBuilder
                            .setTitle("Invalid User")
                            .setMessage("The email or password entered does not match with any existing users.")
                            .setNeutralButton("ok", null).show();
                }
                catch (Exception e)
                {
                    showUnexpectedError(alertDialogBuilder);
                    Log.d(TAG, "onComplete ERROR: " + e.getMessage());
                }
            }
        });
    }

    private void showUnexpectedError(AlertDialog.Builder builder){
        builder
                .setTitle("Unexpected Error")
                .setMessage("An unexpected error has occurred please try again later.")
                .setNeutralButton("ok", null).show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
