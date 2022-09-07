package com.mienut.tst;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
//import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

public class Search extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;

    FirebaseUser user;
    FirebaseAuth fAuth;
    NavigationView navigationView;
    DatabaseReference myRef2,notref;

    int backButtonCount = 0;
    public static Context context;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    double lat,lon;
    private static final String TAG = "Something";
    List<Address> addresses;

    ArrayList<String> arrayListDemo = new ArrayList<String>();

    String city, locality, flat, state;
    private RecyclerView mRecycleView;
    private PopularAdaptar mAdapter;
    private DatabaseReference mDaataRef;
    private List<Popular> mPopular;
    private String type = "Milk";
    private String cust = "Customer";
    boolean stage = true;
    FirebaseFirestore fStore;
    DocumentReference userref;
    TextView  txtt, subTitle, title;

    String xtr, user_id,searched_item;

    int rnr=1;
    boolean callprd=true;

    UUID uniqueKey;
    Map<String,Object> userInfo ;
    Timestamp ts;



    //int LangCode= FirebaseTranslateLanguage.EN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        TextView txt = findViewById(R.id.text);
        txt.setSelected(true);
        Intent intent = getIntent();
        xtr = intent.getStringExtra("type");
        searched_item = intent.getStringExtra("searched");

        user_id = fAuth.getCurrentUser().getUid();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Search.this);

        txtt=findViewById(R.id.txtt);

        //loadLocale();
        fStore = FirebaseFirestore.getInstance();
        getLastLocation();


        FloatingActionButton fab = findViewById(R.id.flot);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i5 = new Intent(Search.this, Cart.class);
                startActivity(i5);
            }
        });



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        title = headerView.findViewById(R.id.username);
        subTitle = headerView.findViewById(R.id.usertype);

        if(user!=null) {
            userref = fStore.collection("users").document(user_id);
            userref.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()) {
                        title.setText(documentSnapshot.getString("fName"));
                        subTitle.setText(documentSnapshot.getString("type"));

                    } else {
                        Log.d("tag", "onEvent: Document do not exists");
                    }
                }
            });
        }


        if(xtr!=null && xtr.equals(cust)) {
            //nav();
            Menu menu = navigationView.getMenu();
            MenuItem nav_camara = menu.findItem(R.id.nav_slideshow);
            nav_camara.setVisible(false);
        }
        // find MenuItem you want to change

        // add NavigationItemSelectedListener to check the navigation clicks
        navigationView.setNavigationItemSelectedListener(this);
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.nav_lang:
                        //toChangeLang();
                        break;
                    case R.id.nav_home:
                        Toast.makeText(Search.this, "Home",Toast.LENGTH_SHORT).show();
                        finish();break;
                    case R.id.nav_kid:
                        Intent i = new Intent(Search.this, MainActivity.class);
                        startActivity(i);break;
                    case R.id.nav_add:
                        Intent i1 = new Intent(Search.this, Product.class);
                        startActivity(i1);break;
                    case R.id.nav_prod:
                        Intent i8 = new Intent(Search.this, Product_seller.class);
                        startActivity(i8);
                        break;
                    case R.id.nav_acc:
                        Intent i9 = new Intent(Search.this, Order_accept.class);
                        startActivity(i9);
                        break;
                    case R.id.nav_share:
                        Intent intent2 = new Intent(); intent2.setAction(Intent.ACTION_SEND);
                        intent2.setType("text/plain");
                        intent2.putExtra(Intent.EXTRA_TEXT, "Download App" );
                        startActivity(Intent.createChooser(intent2, "Share via"));
                        break;
                    case R.id.nav_logout:
                        FirebaseAuth.getInstance().signOut();//logout
                        stage = false;
                        startActivity(new Intent(getApplicationContext(),Login.class));
                        finish();
                        break;
                    case R.id.nav_manage:
                        Intent i2 = new Intent(Search.this, Accept_prod.class);
                        startActivity(i2);

                        break;
                    case R.id.nav_track:
                        Intent i3 = new Intent(Search.this, MapsActivityCurrentPlace.class);
                        startActivity(i3);
                        break;


//                    case R.id.nav_abt:
//                        Intent ik = new Intent(Search.this, About.class);
//                        startActivity(ik);break;
                    case R.id.nav_cart:
                        Intent i5 = new Intent(Search.this, Cart.class);
                        startActivity(i5);

                        break;
                    case R.id.nav_feedback:

                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Search.this);
                        LayoutInflater inflater = Search.this.getLayoutInflater();
                        final View dialogView = inflater.inflate(R.layout.alert_dialog, null);
                        dialogBuilder.setView(dialogView);

                        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

                        dialogBuilder.setTitle("Feedback");
                        dialogBuilder.setMessage("Comments or Suggestions:");
                        dialogBuilder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                DatabaseReference myRef3 = FirebaseDatabase.getInstance().getReference("Feedback").child(user_id).child(UUID.randomUUID().toString());
                                Date date = new Date();
                                long time = date.getTime();
                                ts = new Timestamp(time);
                                userInfo = new HashMap<>();
                                userInfo.put("Time",ts.toString());
                                userInfo.put("Feedback",edt.getText().toString());
                                myRef3.setValue(userInfo);


                            }
                        });
                        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //pass
                            }
                        });
                        AlertDialog b = dialogBuilder.create();
                        b.show();
                        break;
                    default:
                        return true;
                }


                return true;

            }
        });


        notify_user();
        //showPopularProducts();

    }

   /* private void toChangeLang() {

        final String[] listitem = {"English","ಕನ್ನಡ","हिंदी","தமிழ்"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Search.this);
        mBuilder.setTitle("Choose Lang ..");
        mBuilder.setSingleChoiceItems(listitem, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    setLocale("en",FirebaseTranslateLanguage.EN);
                    recreate();
                }
                else if(i==1){
                    setLocale("kn",FirebaseTranslateLanguage.KN);
                    recreate();
                }
                else if(i==2){
                    setLocale("hi",FirebaseTranslateLanguage.HI);
                    recreate();
                }
                else if(i==3){
                    setLocale("ta",FirebaseTranslateLanguage.TA);
                    recreate();
                }

                //Remove Dialogue box
                dialogInterface.dismiss();

            }
        });

        AlertDialog mDialog = mBuilder.create();
        //Show alert msg
        mDialog.show();
    }

    //To set lang preferences
    private void setLocale(String lang,int langCode) {

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
        LangCode = langCode;

        SharedPreferences.Editor editor = getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My_Lang",lang);
        editor.putInt("My_Lang_Code",langCode);
        editor.apply();

    }
    //Load Lang saved in prefference
    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang","");
        int langcode = prefs.getInt("My_Lang_Code", 11);
        setLocale(language,langcode);
    }
*/
    private void notify_user() {

        //For notification

        notref = FirebaseDatabase.getInstance().getReference("transaction").child(user_id);

        notref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {
                    for (DataSnapshot postsnap : dataSnapshot.getChildren()) {
                        OrderByTIme tst = postsnap.getValue(OrderByTIme.class);
                        arrayListDemo.add(tst.getOrderid());
                    }

                    for (String ordid : arrayListDemo) {
                        myRef2 = FirebaseDatabase.getInstance().getReference("order_summary").child(ordid);
                        myRef2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Transaction st = dataSnapshot.getValue(Transaction.class);
                                if(st.getStatus().equals("Confirm") && st.getPayment().equals("Nil"))
                                {
                                    send_noti(st.getName(),"Order Accepted");
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(Search.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }
    public void send_noti(String title, String message) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(android.R.drawable.star_on);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_foreground));
        builder.setContentTitle(title);
        builder.setContentText(message);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Will display the notification in the notification bar
        notificationManager.notify(1, builder.build());
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem search = menu.findItem(R.id.search);
        search.setVisible(false);

        final MenuItem menuItem = menu.findItem(R.id.cart_action);
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("cart").child(user_id);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count= dataSnapshot.getChildrenCount();

                menuItem.setIcon(Converter.convertLayoutToImage(Search.this,(int)count,R.drawable.ic_action_cart_24));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    public void showPopularProducts()
    {

        //Code for displaying  searched items

        mPopular = new ArrayList<Popular>();
        mDaataRef = FirebaseDatabase.getInstance().getReference("products");
        mDaataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot postsnap:dataSnapshot.getChildren())
                {
                    final Popular plr =postsnap.getValue(Popular.class);
//                    System.out.println("Searching on:"+plr.getProd_name());
                    //TODO: search for users
//                    String uuid = plr.getUsrid();
//
//                    DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(uuid);
//                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            DocumentSnapshot document = task.getResult();
//                            String pName = (String) document.get("fName");
//                            if(pName.equals(searched_item)){
//                                System.out.println("Searched on:"+pName);
//                                mPopular.add(plr);
//                                plr.setMiles(distance_bt(lat, lon, plr.getLatitude(), plr.getLongitude(), "K"));
//                            }
//                        }
//                    });

                    //To search for product name
                    if (plr.getProd_name().equals(searched_item)) {
                        plr.setMiles(distance_bt(lat, lon, plr.getLatitude(), plr.getLongitude(), "K"));
                        System.out.println("************"+lat+"88888888888"+lon);
                        mPopular.add(plr);
                    }
                    //To search for product type
                    else if (plr.getType().equals(searched_item)) {
                        plr.setMiles(distance_bt(lat, lon, plr.getLatitude(), plr.getLongitude(), "K"));
                        mPopular.add(plr);
                    }
                }
                Collections.sort(mPopular, new Comparator<Popular>() {
                    @Override
                    public int compare(Popular u1, Popular u2) {
                        return (int) (u1.getMiles() - (u2.getMiles()));
                    }
                });
                mAdapter = new PopularAdaptar(mPopular);
                mRecycleView = findViewById(R.id.recycle_view);
                mRecycleView.setHasFixedSize(true);
                mRecycleView.setLayoutManager(new GridLayoutManager(Search.this, 2, GridLayoutManager.VERTICAL, false));
                mRecycleView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Search.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
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
                                    System.out.println("SEarchhhhhhhhhhhhhhhhh  "+lat+lon);
                                    if(callprd){
                                    showPopularProducts();
                                    callprd=false;}
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
          //  showPopularProducts();
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
                flat = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                Log.d(TAG, "getAddress:  address" + flat);
                Log.d(TAG, "getAddress:  city" + city);
                Log.d(TAG, "getAddress:  state" + state);
                Log.d(TAG, "getAddress:  postalCode" + postalCode);
                Log.d(TAG, "getAddress:  knownName" + knownName);
                txtt.setText(knownName+", "+city);
                System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC  "+city);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static double distance_bt(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit.equals("K")) {
                dist = dist * 1.609344;
            } else if (unit.equals("N")) {
                dist = dist * 0.8684;
            }
            return (dist);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
    @Override
    public void onBackPressed(){
        if (isTaskRoot()){
            if (backButtonCount >= 1){
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }else{
                Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
                backButtonCount++;
            }
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.cart_action:
                Intent i5 = new Intent(Search.this, Cart.class);
                startActivity(i5);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
