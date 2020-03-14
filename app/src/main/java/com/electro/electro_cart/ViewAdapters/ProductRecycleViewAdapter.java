package com.electro.electro_cart.ViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.electro.electro_cart.R;
import com.electro.electro_cart.models.Product;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ProductRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Product> products;
    String id;
    ProductRecycleViewAdapterClickListener productRecycleViewAdapterClickListener;

    private static final int MAIN_LAYOUT = 0;
    private static final int BOUGHT_TOGETHER_LAYOUT = 1;
    private static final int RECOMMENDED_LAYOUT = 2;
    private static final int SPONSORED_LAYOUT = 3;
    private static final int QUESTION_LAYOUT = 4;
    private static final int BY_FEATURE_LAYOUT = 5;
    private static final int COMMENT_LAYOUT = 6;

    public ProductRecycleViewAdapter(Context context, List<Product> products, String id, ProductRecycleViewAdapterClickListener productRecycleViewAdapterClickListener) {
        this.context = context;
        this.products = products;
        this.id = id;
        this.productRecycleViewAdapterClickListener = productRecycleViewAdapterClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;

        if (viewType == MAIN_LAYOUT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_main, parent, false);

            viewHolder = new MainLayoutViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_recyclerview_row, parent, false);
            viewHolder = new RecommendedLayoutViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == MAIN_LAYOUT) {
            Product product = null;

            for (Product p : products) {
                if (p.getId() == id) {
                    product = p;
                    break;
                } else {
                    product = products.get(0);
                }
            }

            final MainLayoutViewHolder mainLayoutViewHolder = (MainLayoutViewHolder) holder;

            mainLayoutViewHolder.textName.setText(product.getName());

            final boolean isFavourite = product.isFavourite();

            mainLayoutViewHolder.toggleButtonFavourite.setChecked(isFavourite);

            final Product finalProduct = product;
            mainLayoutViewHolder.toggleButtonFavourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    //productRecycleViewAdapterClickListener.setFavourite(id,isChecked);
                }
            });
        } else {
            RecommendedLayoutViewHolder recommendedLayoutViewHolder = (RecommendedLayoutViewHolder) holder;

            recommendedLayoutViewHolder.textView.setText("Recommended Products");

            Collections.shuffle(products);

            HomeRowRecycleViewAdapter homeRowRecycleViewAdapter = new HomeRowRecycleViewAdapter(context, products);

            recommendedLayoutViewHolder.recyclerView.setHasFixedSize(true);
            recommendedLayoutViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            recommendedLayoutViewHolder.recyclerView.setAdapter(homeRowRecycleViewAdapter);

            recommendedLayoutViewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Recommended Products", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return MAIN_LAYOUT;
        else return RECOMMENDED_LAYOUT;
    }

    @Override
    public int getItemCount() {
        return 7;
    }

    public class MainLayoutViewHolder extends RecyclerView.ViewHolder {

        CarouselView carouselView;
        TextView textName;
        ToggleButton toggleButtonFavourite;

        public MainLayoutViewHolder(@NonNull View itemView) {
            super(itemView);

            carouselView = itemView.findViewById(R.id.carouselView_product);
            textName = itemView.findViewById(R.id.product_name_product);
            toggleButtonFavourite = itemView.findViewById(R.id.set_favourite_product);
        }
    }

    public class RecommendedLayoutViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView textView;
        Button button;

        public RecommendedLayoutViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerView = itemView.findViewById(R.id.recyclerview_home_row);
            textView = itemView.findViewById(R.id.recyclerview_home_row_title);
            button = itemView.findViewById(R.id.btnMore);
        }
    }
}
