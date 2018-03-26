package com.noise.android.talkingthing;


import android.app.ProgressDialog;
import android.content.Intent;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.StringBufferInputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Declaring the objects
    private Button Signin;
    private EditText Email;
    private EditText Password;
    private TextView Register;
    private TextView Forgotpassword;

    private ProgressDialog progressdialog;

    private FirebaseAuth firebaseauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        //Initializing objects
        Signin = (Button) findViewById(R.id.Signin);

        Email = (EditText) findViewById(R.id.Email);
        Password = (EditText) findViewById(R.id.Password);

        Register = (TextView) findViewById(R.id.Register);
        Forgotpassword = (TextView) findViewById(R.id.forgotpassword);

        Signin.setOnClickListener(this);
        Register.setOnClickListener(this);
        Forgotpassword.setOnClickListener(this);

        progressdialog = new ProgressDialog(this);

        firebaseauth = FirebaseAuth.getInstance();

        if(firebaseauth.getCurrentUser() != null){
            //home activity

            FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
            if(User.isEmailVerified() == false) {
                Toast.makeText(this, "Please check your mail for verification link", Toast.LENGTH_SHORT).show();
                User.sendEmailVerification();
                return;
            }
            Intent home;
            home = new Intent(getApplicationContext(),Home.class);
            home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(home);
            finish();
        }

    }

    private void putActive(){
        String Userid = firebaseauth.getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Userid);
        Map<String,Object> update = new HashMap<>();
        update.put("status","Active");

        ref.updateChildren(update);
    }

        //Sign in method
        private void Login () {
        String email = Email.getText().toString().trim().toLowerCase();
        String password = Password.getText().toString();

        firebaseauth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressdialog.dismiss();
                        if (task.isSuccessful()) {
                            //start stuff
                            putActive();
                            Intent home = new Intent(getApplicationContext(), Home.class);
                            startActivity(home);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Check credentials", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

        //Syntactic credential check

    private void SigninUser() {

            String email = Email.getText().toString().trim().toLowerCase();
            String password = Password.getText().toString();

            //Check emptiness of inputs
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please fill the email field", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please fill the password field", Toast.LENGTH_SHORT).show();
                return;
            }

            //Inputs are valid apparently
            progressdialog.setMessage("Signing in, please wait.");
            progressdialog.show();

            Login();

        }



    //Method to read all button presses
    @Override
    public void onClick(View view) {
        if (view == Signin) {
            SigninUser();
        } else if (view == Register) {
            Intent next = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(next);
        }

        else if(view == Forgotpassword){
            if (TextUtils.isEmpty(Email.getText().toString().trim().toLowerCase())){
                Toast.makeText(this, "Please fill in the email field", Toast.LENGTH_SHORT).show();
            }
            else {
                progressdialog.setMessage("Sending password reset email");
                firebaseauth.sendPasswordResetEmail(String.valueOf(Email.getText().toString().trim().toLowerCase())).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                            Toast.makeText(MainActivity.this, "A password reset email has been sent you", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(MainActivity.this, "Password reset email not sent", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
