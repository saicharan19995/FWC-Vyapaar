package com.mienut.tst;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Farmer extends AppCompatActivity {

    EditText edittext;
    Calendar myCalendar;
    EditText mFarmerName,mVillage,mTaluk,mDistrict,mSoilType;
    Spinner mType;
    ArrayList<String> cropList = new ArrayList<String>();
    String type;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer);

        myCalendar = Calendar.getInstance();
        edittext= (EditText) findViewById(R.id.date);
//        mFarmerName = (EditText) findViewById(R.id.fname);
        mVillage = (EditText) findViewById(R.id.village);
        mTaluk = (EditText) findViewById(R.id.taluk);
        mDistrict = (EditText) findViewById(R.id.district);
        mSoilType = (EditText) findViewById(R.id.soil);
        mType = (Spinner) findViewById(R.id.croptype);

        String enteredDate = String.valueOf(edittext.getText());
        String village = String.valueOf(mVillage.getText());
        String taluk = String.valueOf(mTaluk.getText());
        String district = String.valueOf(mDistrict.getText());
        String soil = String.valueOf(mSoilType.getText());



        toLoadCropList();

        mType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type= adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
        edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(Farmer.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        if(!type.equals("Select Crop")){
            fAuth = FirebaseAuth.getInstance();
            String userID = fAuth.getCurrentUser().getUid();
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("crop_details").child(userID);
            myRef.child("village").setValue(village);
            myRef.child("taluk").setValue(taluk);
            myRef.child("soil").setValue(soil);
            myRef.child("district").setValue(district);
            myRef.child("enteredDate").setValue(enteredDate);
        }

    }


    private void toLoadCropList() {
        DatabaseReference myRef1 = FirebaseDatabase.getInstance().getReference("Crops");
        cropList.add("Select Crop");
        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    cropList.add(ds.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cropList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mType.setAdapter(arrayAdapter);

    }

    private void updateLabel() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        edittext.setText(sdf.format(myCalendar.getTime()));
    }
}