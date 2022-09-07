package com.mienut.tst;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import me.drakeet.materialdialog.MaterialDialog;

public class Product_edit extends AppCompatActivity {

    TextView name,price,disc,qunty;
    DatabaseReference mDaataRef;
    ImageView picture;
    ImageButton e_price,e_name,e_qnty,e_disc;
    String str,name_ed,price_ed,qnty_ed,disc_ed=" ",pid;
    Button edit_image, delete_add;


    private final int SELECT_PHOTO = 101;
    private final int CAPTURE_PHOTO = 102;
    final private int REQUEST_CODE_WRITE_STORAGE = 1;

    Uri imageUri, uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_edit);


        Intent intent = getIntent();
        str = intent.getStringExtra("id");

        name = findViewById(R.id.product_title);
        price =findViewById(R.id.product_price);
        disc = findViewById(R.id.discription);
        qunty = findViewById(R.id.quantity);
        picture = findViewById(R.id.picture);
        e_disc = findViewById(R.id.discription_edit);
        e_name = findViewById(R.id.name_edit);
        e_qnty = findViewById(R.id.qnty_edit);
        e_price = findViewById(R.id.price_edit);
        mDaataRef = FirebaseDatabase.getInstance().getReference("products").child(str);
        edit_image = findViewById(R.id.price_picture);
        delete_add = findViewById(R.id.delete_add);
        showProduct();


        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        e_disc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText resetValue = new EditText(view.getContext());

                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setTitle("Edit Discription");
                passwordResetDialog.setMessage(disc_ed);
                passwordResetDialog.setView(resetValue);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String newValue = resetValue.getText().toString();
                        Map<String, Object> hopperUpdates = new HashMap<>();
                        hopperUpdates.put("discription", newValue);
                        mDaataRef.updateChildren(hopperUpdates);

                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                passwordResetDialog.create().show();
            }
        });

        edit_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(imageUri!=null) {

                    final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                    passwordResetDialog.setTitle("Upload Image");

                    passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String newValue = imageUri.toString();
                            Map<String, Object> hopperUpdates = new HashMap<>();
                            hopperUpdates.put("imageuri", newValue);
                            mDaataRef.updateChildren(hopperUpdates);

                        }
                    });

                    passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    passwordResetDialog.create().show();
                }

            }
        });

        e_qnty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText resetValue = new EditText(view.getContext());
                resetValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setTitle("Edit Quantity");
                passwordResetDialog.setMessage(qnty_ed);
                passwordResetDialog.setView(resetValue);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String newValue = resetValue.getText().toString();
                        Map<String, Object> hopperUpdates = new HashMap<>();
                        hopperUpdates.put("quantity", Long.parseLong(newValue));
                        mDaataRef.updateChildren(hopperUpdates);

                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                passwordResetDialog.create().show();

            }
        });
        e_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText resetValue = new EditText(view.getContext());

                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setTitle("Edit Product Name");
                passwordResetDialog.setMessage(name_ed);
                passwordResetDialog.setView(resetValue);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String newValue = resetValue.getText().toString();
                        Map<String, Object> hopperUpdates = new HashMap<>();
                        hopperUpdates.put("prod_name", newValue);
                        mDaataRef.updateChildren(hopperUpdates);

                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                passwordResetDialog.create().show();

            }
        });
        e_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText resetValue = new EditText(view.getContext());
                resetValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setTitle("Edit Product Price");
                passwordResetDialog.setMessage(price_ed);
                passwordResetDialog.setView(resetValue);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String newValue = resetValue.getText().toString();
                        Map<String, Object> hopperUpdates = new HashMap<>();
                        hopperUpdates.put("prod_price", newValue);
                        mDaataRef.updateChildren(hopperUpdates);

                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                passwordResetDialog.create().show();

            }
        });

        delete_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setTitle("Delete Product");
                passwordResetDialog.setMessage("Are you Sure");

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deL_prod();
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                passwordResetDialog.create().show();



            }
        });
    }

    private void deL_prod() {
        DatabaseReference cartref = FirebaseDatabase.getInstance().getReference("cart");
        cartref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postsnap : dataSnapshot.getChildren()) {
                    for(DataSnapshot chil: postsnap.getChildren())
                    {
                        if(pid.equals(chil.getKey().toString()))
                        {
                            DatabaseReference rem = FirebaseDatabase.getInstance().getReference("cart").child(postsnap.getKey()).child(chil.getKey());
                            rem.setValue(null);
                        }
                    }
                }

                mDaataRef.setValue(null);
                Intent i = new Intent(Product_edit.this, Product_seller.class);
                startActivity(i);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void showProduct() {


        mDaataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    Popular popular = dataSnapshot.getValue(Popular.class);

                    name.setText(popular.getProd_name() + " " + popular.getUnit());
                    name_ed = popular.getProd_name();
                    price.setText("RS " + popular.getProd_price());
                    price_ed = popular.getProd_price();
                    qunty.setText(String.valueOf(popular.getQuantity()));
                    disc.setText(popular.getDiscription());
                    qnty_ed = String.valueOf(popular.getQuantity());
                    pid = popular.getPid();
                    Picasso.get().load(Uri.parse(popular.getImageuri())).into(picture);
                }
                    }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(Product_edit.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

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
                    String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "propic.jpg";
                    uri = Uri.parse(root);
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
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {

                    imageUri = data.getData();
                    String selectedImagePath = getPath(imageUri);
                    File f = new File(selectedImagePath);
                    Picasso.get().load(imageUri).into(picture);

                }
                break;

            case CAPTURE_PHOTO:
                if (resultCode == RESULT_OK) {

                    Bitmap bmp = data.getExtras().getParcelable("data");

                    picture.setImageBitmap(bmp);
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
        if (uri == null) {
            return null;
        }
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
        return uri.getPath();
    }

}
