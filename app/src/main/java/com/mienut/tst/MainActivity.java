package com.mienut.tst;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firestore.v1.WriteResult;
import com.squareup.picasso.Picasso;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {
    private static final int GALLERY_INTENT_CODE = 1023 ;
    TextView fullName,email,phone,address,type, upiname, upiid;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    ImageView changeProfileImage;
    FirebaseUser user;
    ImageView profileImage;
    StorageReference storageReference;
    DatabaseReference ref;
    Button btn, updaddr;
    String updated_type, typ;
    boolean flg=true;
    int iq=0,checkedItem=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phone = findViewById(R.id.profilePhone);
        fullName = findViewById(R.id.profileName);
        email    = findViewById(R.id.profileEmail);
        address = findViewById(R.id.profileaddress);
        type = findViewById(R.id.profileType);
        profileImage = findViewById(R.id.profileImage);
        changeProfileImage = findViewById(R.id.profileImage);
        btn = findViewById(R.id.resetType);
        updaddr = findViewById(R.id.updateAddr);
        upiid = findViewById(R.id.upiid);
        upiname = findViewById(R.id.upiname);

        if(flg) {

            fAuth = FirebaseAuth.getInstance();
            fStore = FirebaseFirestore.getInstance();
            storageReference = FirebaseStorage.getInstance().getReference();


            if (fAuth.getCurrentUser() != null) {
                StorageReference profileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
                profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                    }
                });

                userId = fAuth.getCurrentUser().getUid();
                user = fAuth.getCurrentUser();
            }

            if (userId != null) {
                final DocumentReference documentReference = fStore.collection("users").document(userId);
                documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot.exists()) {
                            phone.setText(documentSnapshot.getString("phone"));
                            fullName.setText(documentSnapshot.getString("fName"));
                            email.setText(documentSnapshot.getString("email"));
                            typ = documentSnapshot.getString("type");
                            type.setText(typ);
                        } else {
                            Log.d("tag", "onEvent: Document do not exists");
                        }
                    }
                });

                ref = FirebaseDatabase.getInstance().getReference("Address").child(userId);

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        address_retriver pd = dataSnapshot.getValue(address_retriver.class);
                        address.setText(pd.getFlat());
                        upiid.setText(pd.getUpiid());
                        upiname.setText(pd.getUpiname());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            }


            changeProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // open gallery
                    Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(openGalleryIntent, 1000);

                }
            });

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showAlertDialog();
                }
            });

            updaddr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i4 = new Intent(MainActivity.this, Address.class);
                    startActivity(i4);

                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();

                uploadImageToFirebase(imageUri);


            }
        }

    }

    private void uploadImageToFirebase(Uri imageUri) {
        // uplaod image to firebase storage
        final StorageReference fileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                        System.out.println("UUUUUURRRRRRI "+uri);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Failed.", Toast.LENGTH_SHORT).show();
            }
        });

    }



    private void showAlertDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Change Account Type");
        //String[] items = {"Customer", "Retailer", "Producer"};
        String[] items = {"Customer", "Producer"};
        for(String str:items){
            if(typ.equals(str))
            {

                checkedItem = iq;
            }
            iq++;
        }
        alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Toast.makeText(MainActivity.this, "Clicked on Customer", Toast.LENGTH_LONG).show();
                        updated_type = "Customer";
                        break;
                   /* case 1:
                        Toast.makeText(MainActivity.this, "Clicked on Retailer", Toast.LENGTH_LONG).show();
                        updated_type = "Retailer";
                        break;*/
                    case 1:
                        Toast.makeText(MainActivity.this, "Clicked on Producer", Toast.LENGTH_LONG).show();
                        updated_type = "Producer";
                        break;


                }

            }

        });
        alertDialog.setPositiveButton("Update Account",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (flg) {
                    DocumentReference documentReference = fStore.collection("users").document(userId);
                    documentReference.update("type", updated_type);



                    iq=0;
                    Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }


}
