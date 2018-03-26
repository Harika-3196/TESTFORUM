package com.noise.android.talkingthing;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Sourabh on 05/03/2018.
 */

public class Emailpopup extends Activity implements View.OnClickListener{

    Button ok = findViewById(R.id.ok_button);
    EditText email = findViewById(R.id.editText);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ok.setOnClickListener(this);

        setContentView(R.layout.change_email);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.075));
    }

    @Override
    public void onClick(View v) {
        if(v == ok){
            FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
            User.updateEmail(email.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(Emailpopup.this, "Email updated", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            else if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(Emailpopup.this, "Email address in use", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });
        }
    }
}
