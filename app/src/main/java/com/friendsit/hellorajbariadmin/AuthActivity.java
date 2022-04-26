package com.friendsit.hellorajbariadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class AuthActivity extends AppCompatActivity {
    private TextInputEditText emailEt,passwordEt;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        initial();

        checkStatus();
    }

    private void checkStatus() {
        if (sharedPreferences.getString("Status","").equals("SignIn")){
            startActivity(new Intent(AuthActivity.this,MainActivity.class));
        }
    }

    public void signInBtnClick(View view) {
        if (emailEt.getText().toString().trim().isEmpty()){
           emailEt.setError("Invalid Value");
           emailEt.requestFocus();
           return;
        }
        if (passwordEt.getText().toString().trim().length() < 6){
            passwordEt.setError("Invalid Value");
            passwordEt.requestFocus();
            return;
        }
        firebaseAuth();
    }

    private void firebaseAuth() {
        progress.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(emailEt.getText().toString().trim(),passwordEt.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            progress.setVisibility(View.GONE);
                            AlertDialog.Builder builder = new AlertDialog.Builder(AuthActivity.this);
                            builder.setTitle("Save Password Alert");
                            builder.setMessage("Do You Want To Save Password ?");
                            builder.setCancelable(false);

                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    editor.putString("Status","SignIn").commit();
                                    startActivity(new Intent(AuthActivity.this,MainActivity.class));
                                }
                            });

                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startActivity(new Intent(AuthActivity.this,MainActivity.class));
                                }
                            });
                            builder.show();

                        }else{
                            progress.setVisibility(View.GONE);
                            Toast.makeText(AuthActivity.this,task.getException().getMessage().toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initial() {
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("MySp",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        progress = findViewById(R.id.progress);
    }
}