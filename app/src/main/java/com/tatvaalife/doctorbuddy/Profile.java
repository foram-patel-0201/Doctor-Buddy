package com.tatvaalife.doctorbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    TextView name,email;
    ImageView profileimage;
    EditText mBirthdate, mPhoneField;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    RadioGroup radioButton;

    private String userId, phone, profileImageUrl,bio, userSex,birthdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            mBirthdate = findViewById(R.id.birthdate);
            mPhoneField = findViewById(R.id.phoneNumber);
            radioButton = findViewById(R.id.radioGroup);


        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        getUserInfo();


        name = findViewById(R.id.name);
        name.setText(personName);


        email = findViewById(R.id.email);
        email.setText(personEmail);


    }


    private void getUserInfo() {
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("birthdate")!=null){
                        birthdate = map.get("birthdate").toString();
                        mBirthdate.setText(birthdate);
                    }

                    if(map.get("phone")!=null){
                        phone = map.get("phone").toString();
                        mPhoneField.setText(phone);
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void onSubmit(View view){
        Toast.makeText(getApplicationContext(), "Submitted", Toast.LENGTH_SHORT).show();
        saveUserInformation();
    }

    private void saveUserInformation() {
        phone = mPhoneField.getText().toString();
        birthdate = mBirthdate.getText().toString();

        int selectId = radioButton.getCheckedRadioButtonId();

        final RadioButton radioButton = (RadioButton) findViewById(selectId);

        if(radioButton.getText() == null){
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        Map userInfo = new HashMap<>();
        userInfo.put("name", name);
        userInfo.put("birthdate",birthdate);
        userInfo.put("phone",phone);
        currentUserDb.updateChildren(userInfo);

    }
}