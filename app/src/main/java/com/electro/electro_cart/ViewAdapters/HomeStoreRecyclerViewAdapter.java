package com.electro.electro_cart.ViewAdapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.electro.electro_cart.R;
import com.electro.electro_cart.models.Product;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class HomeStoreRecyclerViewAdapter extends RecyclerView.Adapter<HomeStoreRecyclerViewAdapter.HomeStoreRecyclerViewHolder> {

    Context context;
    List<Product> productList;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    public HomeStoreRecyclerViewAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }


    @NonNull
    @Override
    public HomeStoreRecyclerViewAdapter.HomeStoreRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_list, parent, false);

        return new HomeStoreRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeStoreRecyclerViewAdapter.HomeStoreRecyclerViewHolder holder, int position) {
        final Product product = productList.get(position);

        Glide.with(context)
                .load(storage.getReferenceFromUrl(product.getImage_links().get(0)))
                .transition(withCrossFade())
                .fitCenter()
                .error(R.drawable.error_loading)
                .fallback(R.drawable.error_loading)
                .into(holder.imageView);

        holder.textViewName.setText(product.getName());

        holder.textViewPrice.setText(String.valueOf(product.getPrice())+" LKR");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("id", product.getId());
                Navigation.findNavController(view).navigate(R.id.action_to_navigation_edit_product, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class HomeStoreRecyclerViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textViewName;
        TextView textViewPrice;

        public HomeStoreRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imgThumb_item_product);
            textViewName = itemView.findViewById(R.id.txt_name_item_product);
            textViewPrice = itemView.findViewById(R.id.txt_price_item_product);
        }
    }
}
