package com.dileep.ecommerce.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dileep.ecommerce.Interface.ItemClickListener;
import com.dileep.ecommerce.R;

public class SellerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtSellerName, txtPhoneNumber, txtSellerCity, txtSellerProduct, txtSellerPrice;
    public ImageView imageView;
    public ItemClickListener listener;


    public SellerViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.seller_item_image);
        txtSellerName = (TextView) itemView.findViewById(R.id.seller_item_name);
        txtPhoneNumber = (TextView) itemView.findViewById(R.id.seller_item_phone);
        txtSellerCity = (TextView) itemView.findViewById(R.id.seller_item_city);
        txtSellerProduct = (TextView) itemView.findViewById(R.id.seller_item_product);
        txtSellerPrice =  (TextView) itemView.findViewById(R.id.seller_item_price);


    }

    public void setItemClickListener(ItemClickListener listener)
    {
        this.listener = listener;

    }


    @Override
    public void onClick(View view) {
        listener.onClick(view, getAdapterPosition(), false);

    }
}
