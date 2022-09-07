package com.mienut.tst;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;


import android.content.Context;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;
import me.drakeet.materialdialog.MaterialDialog;


public class Product extends AppCompatActivity {

    AutoCompleteTextView textIn, text;

    public static final String TAG = "TAG";
    Button buttonAdd, submit;
    LinearLayout container;
    TextView pname,price,quantity,brand,mtype,discp;


    StorageReference storageReference;
    String userID;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    int p=0;
    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();



    UUID uniqueKey;
    Map<String,Object> user = new HashMap<>();
    String prname,prrice,pproperty,pvalue,pquantity;
    Spinner dropdown2,dropdown3,dp,prodp;
    double longitude;
    double latitude;
    ImageView img;
    Uri imageUri,lodUri, uri;
    boolean mlk_typ = false;

    private final int SELECT_PHOTO = 101;
    private final int CAPTURE_PHOTO = 102;
    final private int REQUEST_CODE_WRITE_STORAGE = 1;

    private static final String[] NUMBER = new String[] {
            "One", "Two", "Three", "Four", "Five",
            "Six", "Seven", "Eight", "Nine", "Ten"
    };
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter2,adapter4,ad5,pd1,pd2,pd3;
    ArrayList<String> categories_values = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        storageReference = FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, NUMBER);

        /*textIn = (AutoCompleteTextView)findViewById(R.id.textin);
        textIn.setAdapter(adapter);
        text = (AutoCompleteTextView)findViewById(R.id.text);
        text.setAdapter(adapter);*/

        buttonAdd = (Button)findViewById(R.id.add);
        container = (LinearLayout) findViewById(R.id.container);

        submit =(Button)findViewById(R.id.submit);
        pname = (TextView)findViewById(R.id.pname);
        price = (TextView)findViewById(R.id.price);
        brand = (TextView)findViewById(R.id.brand);
        mtype = (TextView)findViewById(R.id.mtype);
        quantity = (TextView)findViewById(R.id.quantity);
        discp = (TextView) findViewById(R.id.discp);

        img= (ImageView)findViewById(R.id.prod_img);


        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open gallery
                //Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //startActivityForResult(openGalleryIntent, 1000);
                int hasWriteStoragePermission = 0;

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    hasWriteStoragePermission = checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }

                if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_CODE_WRITE_STORAGE);
                    }
                    //return;
                }

                listDialogue();

            }
        });

        dropdown3 = findViewById(R.id.spinner);
        //Loading categories
        loadCategory();


        dropdown2 = findViewById(R.id.pricep);

//        toLoadUnits();



        dp = findViewById(R.id.moving);
        String[] items3 = new String[]{"Stall(Static)","Cart(Moving)"};
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items3);
        dp.setAdapter(adapter3);


        prodp = findViewById(R.id.spinner2);



        DatabaseReference loca = database.getReference("Address").child(userID);



        loca.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                {
                    Intent i3 = new Intent(Product.this, Address.class);
                    startActivity(i3);
                }
                else{
                    address_retriver pd = dataSnapshot.getValue(address_retriver.class);
                    latitude = pd.getLatitute();
                    longitude = pd.getLongitutde();}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(Product.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        dropdown3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String type= adapterView.getItemAtPosition(i).toString();
                if(type.equals("Select Category")){

                }
                else{
                    toGetProducts(type);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        prodp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String type= adapterView.getItemAtPosition(i).toString();
                if(type.equals("Select Category")){

                }
                else{
                    toLoadUnits(type);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        dropdown3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                switch (i){
//                    case 0:
//                        //brand.setVisibility(View.VISIBLE);
//                        //mtype.setVisibility(View.VISIBLE);
//                        //dp.setVisibility(View.GONE);
//                        mlk_typ=true;
//                        prodp.setAdapter(pd1);
//                        clear_text();
//                        break;
//                    case 1:
//                        //brand.setVisibility(View.GONE);
//                        //mtype.setVisibility(View.GONE);
//                        //dp.setVisibility(View.VISIBLE);
//                        mlk_typ=false;
//                        dropdown2.setAdapter(adapter4);
//                        prodp.setAdapter(pd2);
//                        clear_text();
//                        break;
//                    case 2:
//                        //brand.setVisibility(View.GONE);
//                        //mtype.setVisibility(View.GONE);
//                        //dp.setVisibility(View.GONE);
//                        mlk_typ=false;
//                        dropdown2.setAdapter(ad5);
//                        prodp.setAdapter(pd3);
//                        clear_text();
//                        break;
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });


        /*prodp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        if(mlk_typ){
                            dropdown2.setAdapter(adapter2);}
                        break;
                    case 1:
                        if(mlk_typ){
                            dropdown2.setAdapter(adapter2);}
                        break;
                    case 2:
                        if(mlk_typ){
                            dropdown2.setAdapter(adapter2);}
                        break;
                    case 3:
                        if(mlk_typ){
                            dropdown2.setAdapter(ad5);}
                        break;
                    case 4:
                        if(mlk_typ){
                            dropdown2.setAdapter(ad5);}
                        break;
                    case 5:
                        if(mlk_typ){
                            dropdown2.setAdapter(ad5);}
                        break;
                    case 6:
                        if(mlk_typ){
                            dropdown2.setAdapter(adapter2);}
                        break;
                    case 7:
                        if(mlk_typ){
                            dropdown2.setAdapter(adapter2);}
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/



/*
        buttonAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater =
                        (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addView = layoutInflater.inflate(R.layout.row, null);
                AutoCompleteTextView textOut = (AutoCompleteTextView)addView.findViewById(R.id.textout);
                AutoCompleteTextView tex = (AutoCompleteTextView)addView.findViewById(R.id.tex);
                textOut.setAdapter(adapter);
                tex.setAdapter(adapter);
                if(p==0){
                    if(TextUtils.isEmpty(textIn.getText())){
                        textIn.setError("Required.");
                        return;
                    }

                    if(TextUtils.isEmpty(text.getText())){
                        text.setError("Required.");
                        return;
                    }
                    p++;
                }
                textOut.setText(textIn.getText().toString());
                tex.setText(text.getText().toString());
                user.put(textIn.getText().toString(),text.getText().toString());
                textIn.setText("");
                text.setText("");
                Button buttonRemove = (Button)addView.findViewById(R.id.remove);

                final View.OnClickListener thisListener = new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        AutoCompleteTextView childTextView;
                        childTextView = (AutoCompleteTextView) addView.findViewById(R.id.textout);
                        String childTextViewValue = childTextView.getText().toString();
                        user.remove(childTextViewValue);
                        ((LinearLayout)addView.getParent()).removeView(addView);
                        listAllAddView(true);
                    }
                };

                buttonRemove.setOnClickListener(thisListener);
                container.addView(addView);
                listAllAddView(true);
            }
        });
        */


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                prname = pname.getText().toString().trim();
                prrice = price.getText().toString().trim();
//                pproperty = textIn.getText().toString().trim();
                //              pvalue = text.getText().toString().trim();
                pquantity = quantity.getText().toString().trim();

                final String type = String.valueOf(prodp.getSelectedItem());
                if(type.equals("Select Product")){
                    textIn.setError("Required.");
                    return;
                }
                final String unit = String.valueOf(dropdown2.getSelectedItem());

                final String mob = String.valueOf(dp.getSelectedItem());
                System.out.println(">>>>>>>>>>>>>>>>>>>>> "+dp.getSelectedItem());

                /*if(p==0){
                    if(TextUtils.isEmpty(textIn.getText())){
                        textIn.setError("Required.");
                        return;
                    }

                    if(TextUtils.isEmpty(text.getText())){
                        text.setError("Required.");
                        return;
                    }
                    p++;
                }*/
                if(TextUtils.isEmpty(pname.getText())){
                    pname.setError("Required.");
                    return;
                }

                if(TextUtils.isEmpty(price.getText())){
                    price.setError("Required.");
                    return;
                }
                if(TextUtils.isEmpty(quantity.getText())){
                    quantity.setError("Required.");
                    return;
                }
                if(TextUtils.isEmpty(discp.getText())){
                    discp.setError("Required.");
                    return;
                }
                if(imageUri==null){
                    pname.setError("Upload Image");
                    return;
                }




                FirebaseFirestore db = FirebaseFirestore.getInstance();
                //DocumentReference documentReference = fStore.collection("products").document(userID);
                //Map<String,Object> user = new HashMap<>();

                //if(imageUri!=null)
                //{uploadImageToFirebase(imageUri);}



                uniqueKey = UUID.randomUUID();
                final StorageReference fileRef = storageReference.child("products/"+fAuth.getCurrentUser().getUid()+"/"+uniqueKey+".jpg");
                fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //Picasso.get().load(uri).into(img);


                                System.out.println("EEEEEEEEEEE  "+uri);
                                lodUri=uri;



                                user.put("imageuri",lodUri.toString());
                                user.put("prod_name",prname);
                                user.put("prod_price",prrice);
                                //user.put(pproperty ,pvalue);
                                user.put("unit",unit);
                                user.put("type",type);
                                user.put("mobility",mob);
                                user.put("usrid",userID);
                                user.put("stars",0);
                                user.put("people",0);
                                user.put("quantity",Long.parseLong(pquantity));
                                if(!TextUtils.isEmpty(mtype.getText())){
                                    user.put("milk_type",mtype.getText().toString().trim());
                                }
                                if(!TextUtils.isEmpty(discp.getText())){
                                    user.put("discription",discp.getText().toString().trim());
                                }

                                if(!TextUtils.isEmpty(brand.getText())){
                                    user.put("brand",brand.getText().toString().trim());
                                }
                                //if(!TextUtils.isEmpty(String.valueOf(dropdown2.getSelectedItem()))){
                                //  user.put("mobility",String.valueOf(dropdown2.getSelectedItem()));
                                //}



                                user.put("latitude",latitude);
                                user.put("longitude",longitude);
                                user.put("pid",String.valueOf(uniqueKey));
                                //user.put("id",uniqueKey);
                                DatabaseReference myRef = database.getReference("products/"+uniqueKey);
                                myRef.setValue(user);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(Product.this, "Failed.", Toast.LENGTH_SHORT).show();
                    }
                });





                db.collection("users").document("prd").collection("products")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());





                                AlertDialog.Builder dialog=new AlertDialog.Builder(Product.this);
                                dialog.setMessage("Product Saved");
                                dialog.setTitle("Product");
                                dialog.setPositiveButton("Okay",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                Toast.makeText(getApplicationContext(),"Okay",Toast.LENGTH_LONG).show();
                                                clear_text();

                                            }
                                        });

                                AlertDialog alertDialog=dialog.create();
                                alertDialog.show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
            }
        });

    }

    private void toLoadUnits(String type) {
        String category =  String.valueOf(dropdown3.getSelectedItem());
//        String type =  String.valueOf(prodp.getSelectedItem());
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Category").child(category).child(type);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String main_unit = dataSnapshot.getValue(String.class);
                String[] items2 = new String[]{"per "+main_unit,"per 1/2 "+main_unit,"per 1/4 "+main_unit, main_unit};
                ArrayAdapter<String> myadapter = new ArrayAdapter<String>(Product.this, android.R.layout.simple_spinner_dropdown_item, items2);
                dropdown2.setAdapter(myadapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void toGetProducts(String type) {
        final ArrayList<String> types = new ArrayList<String>();
        types.add("Select Product");
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Category").child(type);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    types.add(ds.getKey());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, types);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prodp.setAdapter(arrayAdapter);


    }

    private void loadCategory() {

        DatabaseReference myRef1 = FirebaseDatabase.getInstance().getReference("Category");
        categories_values.add("Select Category");
        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    categories_values.add(ds.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories_values);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown3.setAdapter(arrayAdapter);
    }

    private void listAllAddView(boolean x){
        int childCount = container.getChildCount();
        for(int i=0; i<childCount; i++){
            View thisChild = container.getChildAt(i);
            AutoCompleteTextView childTextView = (AutoCompleteTextView) thisChild.findViewById(R.id.textout);
            String childTextViewValue = childTextView.getText().toString();
            AutoCompleteTextView childTextView2 = (AutoCompleteTextView) thisChild.findViewById(R.id.tex);
            String childTextViewValue2 = childTextView.getText().toString();
            //if(x==true)
            //user.remove(childTextViewValue);
        }}



    private void clear_text()
    {
        //textIn.setText("");
        //text.setText("");
        pname.setText("");
        price.setText("");
        quantity.setText("");
        mtype.setText("");
        brand.setText("");
        discp.setText("");
        img.setImageResource(R.drawable.upload);
    }



    public void listDialogue(){
        final ArrayAdapter<String> arrayAdapter
                = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        arrayAdapter.add("Take Photo");
        arrayAdapter.add("Select Gallery");

        ListView listView = new ListView(this);
        listView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (8 * scale + 0.5f);
        listView.setPadding(0, dpAsPixels, 0, dpAsPixels);
        listView.setDividerHeight(0);
        listView.setAdapter(arrayAdapter);

        final MaterialDialog alert = new MaterialDialog(this).setContentView(listView);

        alert.setPositiveButton("Cancel", new View.OnClickListener() {
            @Override public void onClick(View v) {
                alert.dismiss();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){

                    alert.dismiss();
                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //Uri uri  = Uri.parse("file:///sdcard/photo.jpg");
                    String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "propic.jpg";
                    uri = Uri.parse(root);
                    //i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(i, CAPTURE_PHOTO);

                }else {

                    alert.dismiss();
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, SELECT_PHOTO);

                }
            }
        });

        alert.show();

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                imageUri = data.getData();
                Picasso.get().load(imageUri).into(img);

            }
        }*/
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {

                    imageUri = data.getData();
                    String selectedImagePath = getPath(imageUri);
                    File f = new File(selectedImagePath);
                    Picasso.get().load(imageUri).into(img);

                }
                break;

            case CAPTURE_PHOTO:
                if (resultCode == RESULT_OK) {

                    Bitmap bmp = data.getExtras().getParcelable("data");

                    img.setImageBitmap(bmp);
                    Uri tempUri = getImageUri(getApplicationContext(), bmp);
                    imageUri = tempUri;
                }

                break;
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public String getPath(Uri uri) {
        // just some safety built in
        if (uri == null) {
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null,
                null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }


    private void uploadImageToFirebase(Uri imageUri) {
        // uplaod image to firebase storage
        final StorageReference fileRef = storageReference.child("products/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(img);

                        user.put("imageuri",uri.toString());
                        System.out.println("EEEEEEEEEEE  "+uri);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Product.this, "Failed.", Toast.LENGTH_SHORT).show();
            }
        });

    }



}