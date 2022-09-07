package com.mienut.tst;

import android.app.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.res.Configuration;

import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public class Category extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;

    FirebaseUser user;
    FirebaseAuth fAuth;
    NavigationView navigationView;

    int backButtonCount = 0;
    public static Context context;
    FusedLocationProviderClient mFusedLocationClient;

    private String cust = "Customer";

    boolean stage = true;
    FirebaseFirestore fStore;
    DocumentReference userref;
    DatabaseReference feed, back;
    TextView subTitle, title;

    String xtr, user_id;
    ListView listView;
    List<Cat> lstCat = new ArrayList<>();

    List<String> myList = new ArrayList<>();
    ArrayAdapter adapter;

    UUID uniqueKey;
    Map<String,Object> userInfo ;
    Timestamp ts;

    //int LangCode= FirebaseTranslateLanguage.EN;

    RecyclerView myrv;
    RecyclerViewAdapter myAdapter;
    int span = 3;

    long daysBetween;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_display);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        TextView txt = findViewById(R.id.text);
        txt.setSelected(true);
        Intent intent = getIntent();
        xtr = intent.getStringExtra("type");

        user_id = fAuth.getCurrentUser().getUid();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        to_display_category();

//        For Crop Notifcation Activate this
//        CropNoti();

        feed = FirebaseDatabase.getInstance().getReference("transaction").child(user_id);
        feed.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postsnap : dataSnapshot.getChildren()) {
                    OrderByTIme tst = postsnap.getValue(OrderByTIme.class);
                    back = FirebaseDatabase.getInstance().getReference("order_summary").child(tst.getOrderid());
                    back.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Transaction tst = dataSnapshot.getValue(Transaction.class);
                            if(tst.getRating().equals("null") && !tst.getPayment().equals("Nil") && tst.getStatus().equals("Complete")){
                                Intent intent = new Intent(Category.this, Feedback.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("id", tst.getOrderid());
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(Category.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        //loadLocale();


        fStore = FirebaseFirestore.getInstance();

        FloatingActionButton fab = findViewById(R.id.flot);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i5 = new Intent(Category.this, Cart.class);
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
                       // toChangeLang();
                        break;
                    case R.id.nav_home:
                        Toast.makeText(Category.this, "Home",Toast.LENGTH_SHORT).show();break;
                    case R.id.nav_kid:
                        Intent i = new Intent(Category.this, MainActivity.class);
                        startActivity(i);break;
                    case R.id.nav_add:
                        Intent i1 = new Intent(Category.this, Product.class);
                        startActivity(i1);break;
                    case R.id.nav_prod:
                        Intent i8 = new Intent(Category.this, Product_seller.class);
                        startActivity(i8);
                        break;
                    case R.id.nav_acc:
                        Intent i9 = new Intent(Category.this, Order_accept.class);
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
                        Intent i2 = new Intent(Category.this, Accept_prod.class);
                        startActivity(i2);

                        break;
                    case R.id.nav_track:
                        Intent i3 = new Intent(Category.this, MapsActivityCurrentPlace.class);
                        startActivity(i3);
                        break;


//                    case R.id.nav_abt:
//                        Intent ik = new Intent(Category.this, About.class);
//                        startActivity(ik);break;
                    case R.id.nav_cart:
                        Intent i5 = new Intent(Category.this, Cart.class);
                        startActivity(i5);

                        break;
                    case R.id.nav_feedback:

                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Category.this);
                        LayoutInflater inflater = Category.this.getLayoutInflater();
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

    }

    private void to_display_category() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        if(width<1080){
            span = 2;
        }

        //For categories from database
        toaddCategories();

    }

    private void toaddCategories() {
        // Create a storage reference from our app

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Img_category");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    String category = ds.getKey();
                    String image_url = ds.getValue(String.class);

//                    Toast.makeText(Category.this,"Cat:"+category,Toast.LENGTH_SHORT).show();
                    lstCat.add(new Cat(category,image_url));
                }
                myrv = (RecyclerView) findViewById(R.id.recyclerview_id);
                myAdapter = new RecyclerViewAdapter(Category.this,lstCat);
                myrv.setLayoutManager(new GridLayoutManager(Category.this,span));
                myrv.setHasFixedSize(true);
                myrv.setAdapter(myAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }





    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(main2, menu);


        toaddSearchList();

        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem menuItem = menu.findItem(R.id.cart_action);

        final MenuItem search = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) search.getActionView();
        searchView.setBackgroundColor(Color.WHITE);
        searchView.setQueryHint("Search Here!");

        listView = findViewById(R.id.search_view);
        final LinearLayout linearLayout = findViewById(R.id.category_layout);
        listView.setVisibility(View.GONE);
        listView.setBackgroundColor(Color.WHITE);

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,myList);
        listView.setAdapter(adapter);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                linearLayout.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchView.setQuery( myList.get(position),false);
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //TODO:Intent for searched element
                Toast.makeText(Category.this,"Searched :"+s,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Category.this,Search.class);
                intent.putExtra("type", xtr);
                intent.putExtra("searched", s);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s!=null && !s.isEmpty()){
                    adapter.getFilter().filter(s);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Object obj = listView.getAdapter().getItem(position);
                            String value = obj.toString();
                            searchView.setQuery(value,false);
                        }
                    });
                }
                else{
                    adapter = new ArrayAdapter(Category.this, android.R.layout.simple_list_item_1,myList);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            searchView.setQuery( myList.get(position),false);
//                            Toast.makeText(Category.this, myList.get(position), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return true;
            }
        });


        linearLayout.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);

        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("cart").child(user_id);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count= dataSnapshot.getChildrenCount();

                menuItem.setIcon(Converter.convertLayoutToImage(Category.this,(int)count,R.drawable.ic_action_cart_24));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //MenuItem menuItem2 = menu.findItem(R.id.notification_action);
        //menuItem2.setIcon(Converter.convertLayoutToImage(MainActivity.this,2,R.drawable.ic_notifications_white_24dp));

        return true;
    }

    private void toaddSearchList() {

        //To load categories
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Category");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    String category = ds.getKey();
                    //To add Categories to search list
//                    if(!myList.contains(category)){
//                        myList.add(category);
//                    }
                    //To load sub category
                    myRef.child(category).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                if(!myList.contains(ds.getKey())){
                                    myList.add(ds.getKey());
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //To load products
        DatabaseReference myRef_product = FirebaseDatabase.getInstance().getReference("products");
        myRef_product.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    if(!myList.contains(ds.child("prod_name").getValue(String.class))){
                        myList.add(ds.child("prod_name").getValue(String.class));
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
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
                Intent i5 = new Intent(Category.this, Cart.class);
                startActivity(i5);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
