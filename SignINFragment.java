package com.example.sajhakarobar;


import android.content.Intent;
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

import org.w3c.dom.Text;

import static com.example.sajhakarobar.RegisterActivity.onResetPasswordFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignINFragment extends Fragment {


    public SignINFragment() {
        // Required empty public constructor
    }

    private TextView dontHaveanAccount;
    private FrameLayout parentFrameLayout;

    private EditText email;
    private EditText password;

    private TextView forgotPassword;

    private ProgressBar progressBar;

    private ImageButton closeBtn;
    private Button signinBtn;

    private FirebaseAuth firebaseAuth;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    public static boolean disableCloseBtn = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        dontHaveanAccount = view.findViewById(R.id.no_account);
        parentFrameLayout = getActivity().findViewById(R.id.register_framelayout);

        forgotPassword = view.findViewById(R.id.forgot_password_btn);

        email = view.findViewById(R.id.sign_in_username_btn);
        password = view.findViewById(R.id.sign_in_password_btn);

        progressBar = view.findViewById(R.id.signin_Progressbar);

        closeBtn = view.findViewById(R.id.sign_in_close_btn);
        signinBtn = view.findViewById(R.id.sign_in_btn);

        firebaseAuth = FirebaseAuth.getInstance();

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

        dontHaveanAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResetPasswordFragment = true;
                setFragment(new SingUpFragment());
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResetPasswordFragment = true;
                setFragment(new ResetPasswordFragment());
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

        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailandPassword();
            }
        });

    }
    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_out_from_left);
        fragmentTransaction.replace(parentFrameLayout.getId(),fragment);
        fragmentTransaction.commit();

}
    private void checkInputs() {
        if(!TextUtils.isEmpty(email.getText())){
            if(!TextUtils.isEmpty(password.getText())){
                signinBtn.setEnabled(true);
                signinBtn.setTextColor(getResources().getColor(R.color.text));

            }else{
                signinBtn.setEnabled(false);
                signinBtn.setTextColor(getResources().getColor(R.color.text1));
            }

            }else{
            signinBtn.setEnabled(false);
            signinBtn.setTextColor(getResources().getColor(R.color.text1));

        }
        }
        private void checkEmailandPassword(){
            if(email.getText().toString().matches(emailPattern)){
                if(password.length()>=6){

                    progressBar.setVisibility(View.VISIBLE);
                    signinBtn.setEnabled(false);
                    signinBtn.setTextColor(getResources().getColor(R.color.text1));


                    firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        mainIntent();

                                        }else{
                                        progressBar.setVisibility(View.INVISIBLE);
                                        signinBtn.setEnabled(true);
                                        signinBtn.setTextColor(getResources().getColor(R.color.text));
                                        String error = task.getException().getMessage();
                                        Toast.makeText(getActivity(),error, Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });


                }else{
                    Toast.makeText(getActivity(),"Incorrect Email or Password", Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(getActivity(),"Incorrect Email or Password", Toast.LENGTH_SHORT).show();
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
