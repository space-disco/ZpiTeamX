package com.example.projekt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RejestracjaActivity extends AppCompatActivity {

    EditText etEmail, etHaslo,etHaslo_powtorz;
    Button btnRejestracja;
    FirebaseAuth mFirebaseAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejestracja);

        mFirebaseAuth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.email);
        etHaslo = findViewById(R.id.haslo);
        etHaslo_powtorz = findViewById(R.id.haslo_powtorz);
        progressBar = findViewById(R.id.progressBar_rejestracja);
        btnRejestracja = findViewById(R.id.rejestracja);

        btnRejestracja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = etEmail.getText().toString().trim();
                String haslo = etHaslo.getText().toString().trim();
                String haslo_powtorz = etHaslo_powtorz.getText().toString().trim();


                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Email jest wymagany");
                    return;
                }
                if (TextUtils.isEmpty(haslo)) {
                    etHaslo.setError("Hasło jest wymagane");
                    return;
                } if (TextUtils.isEmpty(haslo_powtorz)) {
                    etHaslo.setError("Hasło jest wymagane");
                    return;
                }
                if (etHaslo.length() < 6) {
                    etHaslo_powtorz.setError("hasło musi być dłuższe niż 6 znaków!");
                    return;
                }if (!haslo.equals(haslo_powtorz)) {
                    etHaslo_powtorz.setError("Hasła muszą być identyczne!");
                    return;
                }


                progressBar.setVisibility(View.VISIBLE);
                mFirebaseAuth.createUserWithEmailAndPassword(email, haslo).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sendVerificationEmail();
                            Toast.makeText(RejestracjaActivity.this, "Konto stworzone, prosze potwierdzic adres email!", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Użytkownicy");


                            usersRef.child(String.valueOf(email.hashCode())).setValue(new Uzytkownik(email));






                            startActivity(new Intent(RejestracjaActivity.this, LoginActivity.class));
                        } else {
                            Toast.makeText(RejestracjaActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }


    private void sendVerificationEmail()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email sent


                            // after email is sent just logout the user and finish this activity
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(RejestracjaActivity.this, LoginActivity.class));
                            finish();
                        }
                        else
                        {
                            // email not sent, so display message and restart the activity or do whatever you wish to do

                            //restart this activity
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());

                        }
                    }
                });
    }
}
