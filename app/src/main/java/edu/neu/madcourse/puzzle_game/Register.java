package edu.neu.madcourse.puzzle_game;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;

public class Register extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        usernameEditText = findViewById(R.id.usernameRegister);
        emailEditText = findViewById(R.id.emailRegister);
        passwordEditText = findViewById(R.id.passwordRegister);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(usernameEditText.getText().toString(), emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });
    }


    /**
     * Register user with email/password.
     *
     * @param username the username String.
     * @param email    the email String.
     * @param password the password String.
     */
    private void register(String username, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            firebaseUser = mAuth.getCurrentUser();
                            String userId = firebaseUser.getUid();
//                            reference = FirebaseDatabase.getInstance().getReference("Users")
//                                    .child(userId);

                            FirebaseDatabase.getInstance().getReference().child("users").child(task.getResult().getUser().getUid())
                                    .child("id").setValue(userId);

                            FirebaseDatabase.getInstance().getReference().child("users").child(task.getResult().getUser().getUid())
                                    .child("email").setValue(email);
                            FirebaseDatabase.getInstance().getReference().child("users").child(task.getResult().getUser().getUid())
                                    .child("username").setValue(username);

                            // Load default pic
                            ImageView imageView = findViewById(R.id.testImg);
                            Bitmap profileBitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                            ByteArrayOutputStream bao = new ByteArrayOutputStream();
                            profileBitmap.compress(Bitmap.CompressFormat.PNG, 100, bao); // bmp is bitmap from user image file
                            profileBitmap.recycle();
                            byte[] byteArray = bao.toByteArray();
                            String imageB64 = Base64.encodeToString(byteArray, Base64.URL_SAFE);

                            FirebaseDatabase.getInstance().getReference().child("users").child(task.getResult().getUser().getUid())
                                    .child("profileBitmap").setValue(imageB64);

                            login();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Moving to the Home Page intent if sign in succesfull.
     * //TODO:need to add sign in
     */
    private void login() {
        Intent intent = new Intent(Register.this, HomePage.class);
        startActivity(intent);
        finish();
    }
}
