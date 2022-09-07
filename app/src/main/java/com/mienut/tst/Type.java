package com.mienut.tst;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

public class Type extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;

    FirebaseUser user;
    FirebaseAuth fAuth;
    NavigationView navigationView;
    DatabaseReference myRef2,notref;

    int backButtonCount = 0;
    public static Context context;


    ArrayList<String> arrayListDemo = new ArrayList<String>();

    private String cust = "Customer";
    boolean stage = true;
    FirebaseFirestore fStore;
    DocumentReference userref;
    TextView  subTitle, title;

    String xtr, user_id,catClicked;

    Map<String,Object> userInfo ;
    Timestamp ts;
    ListView listView;



    //int LangCode= FirebaseTranslateLanguage.EN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        TextView txt = findViewById(R.id.text);
        txt.setSelected(true);
        Intent intent = getIntent();
        xtr = intent.getStringExtra("type");
        catClicked = intent.getStringExtra("cat");


        //loadLocale();
        fStore = FirebaseFirestore.getInstance();

        FloatingActionButton fab = findViewById(R.id.flot);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i5 = new Intent(Type.this, Cart.class);
                startActivity(i5);
            }
        });



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        title = headerView.findViewById(R.id.username);
        subTitle = headerView.findViewById(R.id.usertype);
        user_id = fAuth.getCurrentUser().getUid();


        if(user!=null) {
            userref = fStore.collection("users").document(user_id);
            userref.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()) {
                        title.setText(documentSnapshot.getString("fName"));
                        subTitle.setText(documentSnapshot.getString("type"));
                        xtr=documentSnapshot.getString("type");
                        if(xtr!=null && xtr.equals(cust)) {
                            //nav();
                            Menu menu = navigationView.getMenu();
                            MenuItem nav_camara = menu.findItem(R.id.nav_slideshow);
                            nav_camara.setVisible(false);
                        }

                    } else {
                        Log.d("tag", "onEvent: Document do not exists");
                    }
                }
            });
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
                        Toast.makeText(Type.this, "Home",Toast.LENGTH_SHORT).show();
                        finish();break;
                    case R.id.nav_kid:
                        Intent i = new Intent(Type.this, MainActivity.class);
                        startActivity(i);break;
                    case R.id.nav_add:
                        Intent i1 = new Intent(Type.this, Product.class);
                        startActivity(i1);break;
                    case R.id.nav_prod:
                        Intent i8 = new Intent(Type.this, Product_seller.class);
                        startActivity(i8);
                        break;
                    case R.id.nav_acc:
                        Intent i9 = new Intent(Type.this, Order_accept.class);
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
                        Intent i2 = new Intent(Type.this, Accept_prod.class);
                        startActivity(i2);

                        break;
                    case R.id.nav_track:
                        Intent i3 = new Intent(Type.this, MapsActivityCurrentPlace.class);
                        startActivity(i3);
                        break;


//                    case R.id.nav_abt:
//                        Intent ik = new Intent(Type.this, About.class);
//                        startActivity(ik);break;
                    case R.id.nav_cart:
                        Intent i5 = new Intent(Type.this, Cart.class);
                        startActivity(i5);

                        break;
                    case R.id.nav_feedback:

                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Type.this);
                        LayoutInflater inflater = Type.this.getLayoutInflater();
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
        toLoadTypes();

    }

    private void toLoadTypes() {
        final ArrayList<String> myList = new ArrayList<String>();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Category").child(catClicked);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String type = ds.getKey();
                    myList.add(type);
                    System.out.println("Values are"+type);
                }
                listView = (ListView) findViewById(R.id.type_display);
                ArrayAdapter adapter = new ArrayAdapter(Type.this, android.R.layout.simple_list_item_1, myList);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(Type.this,Main2Activity.class);
                        intent.putExtra("type", cust);
                        intent.putExtra("cat", catClicked);
                        intent.putExtra("type_selected", myList.get(position));
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

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

                Toast.makeText(Type.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
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

                menuItem.setIcon(Converter.convertLayoutToImage(Type.this,(int)count,R.drawable.ic_action_cart_24));

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
                Intent i5 = new Intent(Type.this, Cart.class);
                startActivity(i5);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}