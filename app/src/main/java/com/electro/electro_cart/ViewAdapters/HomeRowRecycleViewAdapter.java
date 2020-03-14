package com.electro.electro_cart.ViewAdapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.electro.electro_cart.R;
import com.electro.electro_cart.models.Product;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import java.util.List;

public class HomeRowRecycleViewAdapter extends RecyclerView.Adapter<HomeRowRecycleViewAdapter.HomeRowRecycleViewHolder> {

    Context context;
    List<Product> products;

    public HomeRowRecycleViewAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public HomeRowRecycleViewAdapter.HomeRowRecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_recyclerview_item, parent, false);

        return new HomeRowRecycleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeRowRecycleViewAdapter.HomeRowRecycleViewHolder holder, int position) {
        final Product product = products.get(position);

        Glide.with(context)
                .load("https://fdn2.gsmarena.com/vv/bigpic/samsung-galaxy-s20-ultra-.jpg")
                .transition(withCrossFade())
                .fitCenter()
                .placeholder(R.drawable.loading)
                .error(R.drawable.error_loading)
                .fallback(R.drawable.error_loading)
                .into(holder.imageView);

        holder.textView.setText(product.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putString("id",product.getId());
                Navigation.findNavController(view).navigate(R.id.action_to_navigation_product,bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size() <= 8 ? products.size() : 8;
    }

    public class HomeRowRecycleViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        public HomeRowRecycleViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imgThumb_item);
            textView = itemView.findViewById(R.id.txtTitle_item);
        }
    }
}
