package com.mienut.tst;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Producer_prods extends AppCompatActivity {

    CardView product_view,add_product;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producer_prods);
        product_view=findViewById(R.id.products_view);
        add_product=findViewById(R.id.add_product);
        this.setTitle("Products");
        add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Producer_prods.this, Product.class);
                startActivity(i);
            }
        });
        product_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Producer_prods.this, Product_seller.class);
                startActivity(i);
            }
        });
    }
}