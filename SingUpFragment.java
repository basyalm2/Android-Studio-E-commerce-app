package com.example.sajhakarobar;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class SingUpFragment extends Fragment {


    public SingUpFragment() {
        // Required empty public constructor
    }

    private TextView HaveAnAccount;
    private FrameLayout parentFrameLayout;

    private EditText email;
    private EditText fullname;
    private EditText password;
    private EditText confirmPassword;

    private ImageButton closeBtn;
    private Button signUpBtn;

    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    public static boolean disableCloseBtn = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sing_up, container, false);

        HaveAnAccount = view.findViewById(R.id.yes_account);

        parentFrameLayout = getActivity().findViewById(R.id.register_framelayout);

        email = view.findViewById(R.id.signUP_email);
        fullname = view.findViewById(R.id.signup_fullName);
        password = view.findViewById(R.id.signUp_password);
        confirmPassword = view.findViewById(R.id.signUp_confirmPasword);

        closeBtn = view.findViewById(R.id.sign_up_closebtn);
        signUpBtn = view.findViewById(R.id.signUp_button);

        progressBar = view.findViewById(R.id.signup_progressbar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (disableCloseBtn){
            closeBtn.setVisibility(View.GONE);
        }else{
            closeBtn.setVisibility(View.VISIBLE);
        }


        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignINFragment());
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainIntent();
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        fullname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailAndPassword();

            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_out_from_right);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void checkInputs(){
        if (!TextUtils.isEmpty(email.getText())) {
            if (!TextUtils.isEmpty(fullname.getText())) {
                if (!TextUtils.isEmpty(password.getText()) && password.length() >= 6) {
                    if (!TextUtils.isEmpty(confirmPassword.getText())) {
                        signUpBtn.setEnabled(true);
                        signUpBtn.setTextColor(getResources().getColor(R.color.text));
                    } else {
                        signUpBtn.setEnabled(false);
                        signUpBtn.setTextColor(getResources().getColor(R.color.text1));
                    }
                } else {
                    signUpBtn.setEnabled(false);
                    signUpBtn.setTextColor(getResources().getColor(R.color.text1));
                }

            } else {
                signUpBtn.setEnabled(false);
                signUpBtn.setTextColor(getResources().getColor(R.color.text1));
            }
        }else {
            signUpBtn.setEnabled(false);
            signUpBtn.setTextColor(getResources().getColor(R.color.text1));
            }
        }

    private void checkEmailAndPassword(){
        if(email.getText().toString().matches(emailPattern)){
            if(password.getText().toString().equals(confirmPassword.getText().toString())){

                progressBar.setVisibility(View.VISIBLE);
                signUpBtn.setEnabled(false);
                signUpBtn.setTextColor(getResources().getColor(R.color.text1));

                firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){

                                    Map<String, Object> userdata = new HashMap<>();
                                    userdata.put("fullname", fullname.getText().toString());

                                    firebaseFirestore.collection("USERS").document(firebaseAuth.getUid())
                                            .set(userdata)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Map<String, Object> listSize = new HashMap<>();
                                                        listSize.put("list_size", (long)0);
                                                        firebaseFirestore.collection("USERS").document(firebaseAuth.getUid()).collection("USER_DATA").document("MY_WISHLIST")
                                                                .set(listSize).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    mainIntent();
                                                                }else{
                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                    signUpBtn.setEnabled(true);
                                                                    signUpBtn.setTextColor(getResources().getColor(R.color.text));
                                                                    String error = task.getException().getMessage();
                                                                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });

                                                    }else{
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });



                                }else{
                                    progressBar.setVisibility(View.INVISIBLE);

                                    signUpBtn.setEnabled(true);
                                    signUpBtn.setTextColor(getResources().getColor(R.color.text));

                                    String error = task.getException().getMessage();
                                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            else{
                confirmPassword.setError("Password doesn't match");

            }
        }else{
            email.setError("Invalid Email");

        }

    }
    private void mainIntent(){
        if (disableCloseBtn){
            disableCloseBtn = false;
        }
        else {
            Intent mainIntent = new Intent(getActivity(), Main2Activity.class);
            startActivity(mainIntent);
        }
            getActivity().finish();
    }
}
