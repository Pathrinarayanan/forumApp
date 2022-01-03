package com.example.forumapp.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.forumapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UserActivity extends AppCompatActivity {

    private Button tag_btn[] = new Button[9];
    private Button create_account;
    private EditText username_field, password_field;
    String tag_value, email, username, password;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase db;
    DatabaseReference reference;
    String userid;
    TextView selected_tag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Intent intent = getIntent();

        username_field = findViewById(R.id.txtUsername);
        create_account = findViewById(R.id.btnCreateAccount);
        password_field = findViewById(R.id.txtPassword);


        reference = FirebaseDatabase.getInstance().getReference();

        email = intent.getStringExtra("email");
        mAuth = FirebaseAuth.getInstance();



        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = username_field.getText().toString();
                password = password_field.getText().toString();


                if (TextUtils.isEmpty(username)) {
                    username_field.setError("Required");

                } else if (TextUtils.isEmpty(password)) {
                    password_field.setError("Required");

                } else if (password.length() < 6) {

                    password_field.setError("Length Must Be 6 or more");
                } else {


                    registerUser(username, password, email);
                }
                ;
            }
        });
    }

    public void change_tag_value() {
        selected_tag.setText(tag_value);
    }




    private void registerUser(final String username, String password, final String email) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    FirebaseUser user = mAuth.getCurrentUser();

                    reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());


                    if (user != null) {

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("username", username);
                        hashMap.put("email", email);
                        hashMap.put("id", user.getUid());
                        hashMap.put("imageURL", "default");
                        hashMap.put("status", "offline");
                        hashMap.put("search", username.toLowerCase());


                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {


                                if (task.isSuccessful()) {

                                    Toast.makeText(UserActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(UserActivity.this, com.example.forumapp.MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                }
                            }
                        });
                    }

                }

            }
        });
    }
}