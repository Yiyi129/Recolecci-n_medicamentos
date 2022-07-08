package com.example.recoleccionmedicamentos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity {
    Button access;
    EditText psw, email;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        email = findViewById(R.id.txtEmail);
        psw = findViewById(R.id.txtPsw);
        access = findViewById(R.id.btnLogin);

        access.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email1 = email.getText().toString().trim();
                String psw1 = psw.getText().toString().trim();

                if(email1.isEmpty() && psw1.isEmpty()){
                    Toast.makeText(MainActivity.this, "Ingresa los datos", Toast.LENGTH_SHORT).show();
                }else{
                    loginUser(email1, psw1);
                }
            }
        });
    }

    private void loginUser(String email1, String psw1){
        auth.signInWithEmailAndPassword(email1, psw1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    ProcessLogin();
                    Toast.makeText(MainActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "ERROR AL INICIAR SESIÓN", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void onStart(){
        super.onStart();
        FirebaseUser usuario = auth.getCurrentUser();
        if (usuario != null){
            startActivity(new Intent(MainActivity.this, principal.class));
            finish();
        }
    }

    private void ProcessLogin() {
        SafetyNet.getClient(MainActivity.this).verifyWithRecaptcha("6LdHplQfAAAAACgJJ7HHlVfAAeHpRcoUSBpZEcHs")
                .addOnSuccessListener(new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse recaptchaTokenResponse) {
                        String captchaToken = recaptchaTokenResponse.getTokenResult();

                        if(captchaToken != null){
                            if(!captchaToken.isEmpty()){
                                processLoginStep(captchaToken, email.getText().toString(), psw.getText().toString());
                                //seguimiento a otra interfaz
                                startActivity(new Intent(MainActivity.this, principal.class));
                            }else{
                                Toast.makeText(MainActivity.this, "Captcha Inválido", Toast.LENGTH_SHORT);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Falla al cargar el Captcha", Toast.LENGTH_SHORT);
                    }
                });
    }

    private void processLoginStep(String token, String username, String password) {
        Log.d("CAPTCHA TOKEN", ""+token);
    }

}