package com.friendsit.hellorajbariadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.friendsit.hellorajbariadmin.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private String[] spinner = {"GovtOffice","Police","Hospital","FireService",
            "Ambulance","Doctors","BloodDonors","TouristSpots","Hotels","RentACar","Electricity"};
    private Context context = MainActivity.this;
    private String spinnerIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,spinner);
        binding.spinner.setAdapter(adapter);

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                spinnerIn = binding.spinner.getItemAtPosition(position).toString();

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        binding.saveBtn.setOnClickListener(view1 -> {
            savBtnOnClick();
        });
    }

    private void savBtnOnClick() {
        if (binding.nameEt.getText().toString().isEmpty()|| binding.phoneEt.getText().toString().isEmpty()){
            Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show();
        }else {
            binding.saveBtn.setVisibility(View.GONE);
            binding.prog.setVisibility(View.VISIBLE);
            pushDataToFirebase();
        }
    }

    private void pushDataToFirebase() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("AllData");
        databaseReference.keepSynced(true);
        DatabaseReference dataRef = databaseReference.child(spinnerIn).push();
        String pushKey = dataRef.getKey().toString();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("Cat",spinnerIn);
        hashMap.put("Nam",binding.nameEt.getText().toString());
        hashMap.put("Tit",binding.titleEt.getText().toString());
        hashMap.put("Pho",binding.phoneEt.getText().toString());
        hashMap.put("Ema",binding.emailEt.getText().toString());
        hashMap.put("Add",binding.addressEt.getText().toString());
        hashMap.put("Det",binding.detailsEt.getText().toString());
        hashMap.put("Key",pushKey);
        dataRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(context,"Data Added Successfully",Toast.LENGTH_SHORT).show();
                    binding.prog.setVisibility(View.GONE);
                    binding.saveBtn.setVisibility(View.VISIBLE);
                    binding.nameEt.setText("");
                    binding.titleEt.setText("");
                    binding.phoneEt.setText("");
                    binding.emailEt.setText("");
                    binding.addressEt.setText("");
                    binding.detailsEt.setText("");
                }else{
                    Toast.makeText(context,task.getException().getMessage().toString(),Toast.LENGTH_SHORT).show();
                    binding.prog.setVisibility(View.GONE);
                    binding.saveBtn.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}