package com.mienut.tst;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

public class Address extends AppCompatActivity {


    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    double lat,lon;
    private static final String TAG = "Something";
    List<android.location.Address> addresses;

    EditText city, locality, flat, pincode, state, landmark, upi_name, upi_id, alphone;

    public static Context context;
    String userID;
    FirebaseAuth fAuth;
    Button save;
    FirebaseDatabase database;

    String prod= "Producer";
    String cust ="Customer";
    FirebaseFirestore fStore;
    String phone_number,name_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        city = findViewById(R.id.city);
        locality = findViewById(R.id.locality);
        flat = findViewById(R.id.flat_no);
        pincode = findViewById(R.id.pincode);
        state = findViewById(R.id.state);
        landmark = findViewById(R.id.landmark);
        upi_id = findViewById(R.id.upi_id);
        upi_name = findViewById(R.id.upi_name);
        alphone = findViewById(R.id.alternate_mobile_no);
        save= findViewById(R.id.save_btn);

        fStore = FirebaseFirestore.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        if(userID!=null) {
            DocumentReference documentReference = fStore.collection("users").document(userID);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()) {
                        phone_number = documentSnapshot.getString("phone");
                        name_user = documentSnapshot.getString("fName");
                    } else {
                        Log.d("tag", "onEvent: Document do not exists");
                    }
                }
            });

        }


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String ucity = city.getText().toString().trim();
                final String uloc = locality.getText().toString().trim();
                final String uflat = flat.getText().toString().trim();
                final String upin = pincode.getText().toString().trim();
                final String ustate = state.getText().toString().trim();
                final String upiname = upi_name.getText().toString().trim();
                final String upiid = upi_id.getText().toString().trim();
                //final String uaph = alphone.getText().toString().trim();
                final String uland = landmark.getText().toString().trim();


                DatabaseReference myRef = database.getReference("Address").child(userID);

                if(TextUtils.isEmpty(ucity)){
                    city.setError("Required.");
                    return;
                }

                if(TextUtils.isEmpty(uloc)){
                    locality.setError("Required.");
                    return;
                }
                if(TextUtils.isEmpty(ucity)){
                    city.setError("Required.");
                    return;
                }

                if(TextUtils.isEmpty(uflat)){
                    flat.setError("Required.");
                    return;
                }
                if(TextUtils.isEmpty(upin)){
                    pincode.setError("Required.");
                    return;
                }

                if(TextUtils.isEmpty(ustate)){
                    state.setError("Required.");
                    return;
                }
               /* if(TextUtils.isEmpty(uname)){
                    name.setError("Required.");
                    return;
                }

                if(TextUtils.isEmpty(uph)){
                    phone.setError("Required.");
                    return;
                }*/



                Map<String,Object> user = new HashMap<>();
                user.put("City",ucity);
                user.put("locality",uloc);
                user.put("flat",uflat);
                user.put("pincode",upin);
                user.put("state",ustate);
                user.put("landmark",uland);
                user.put("name",name_user);
                user.put("phone",phone_number);
                user.put("upiname",upiname);
                user.put("upiid",upiid);
                //user.put("alphone",uaph);
                user.put("latitute",lat);
                user.put("longitutde",lon);


                myRef.setValue(user);
                login_cust();

            }

            });


    }


    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    context = getApplicationContext();
                                    lat=location.getLatitude();
                                    lon=location.getLongitude();
                                    System.out.println("LOCCCCCCCCCCCCCCCCC  "+lat+lon);
                                    getAddress(context,lat,lon);
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            lat=mLastLocation.getLatitude();
            lon=mLastLocation.getLongitude();
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }

    }


    public void getAddress(Context context, double LATITUDE, double LONGITUDE){
        //Set Address
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);

            if (addresses != null && addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city1 = addresses.get(0).getLocality();
                String state1 = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                Log.d(TAG, "getAddress:  address" + address);
                Log.d(TAG, "getAddress:  city" + city1);
                Log.d(TAG, "getAddress:  state" + state1);
                Log.d(TAG, "getAddress:  postalCode" + postalCode);
                Log.d(TAG, "getAddress:  knownName" + knownName);
                if(city1!=null && state1!=null && postalCode!=null && address!=null && knownName!=null ) {
                    city.setText(city1);
                    state.setText(state1);
                    pincode.setText(postalCode);
                    flat.setText(address);
                    locality.setText(knownName);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void login_cust() {
        final DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    if(prod.equals(documentSnapshot.getString("type")))
                    {
                        startActivity(new Intent(getApplicationContext(),Producer.class));
                        finish();
                    }
                    else if(cust.equals(documentSnapshot.getString("type")))
                    {
                        Intent intent = new Intent(Address.this, Category.class);
                        intent.putExtra("type", cust);
                        startActivity(intent);
                        finish();
                    }
                    else{

                        startActivity(new Intent(getApplicationContext(),Category.class));
                        finish();
                    }
                }else {
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });
    }
}
