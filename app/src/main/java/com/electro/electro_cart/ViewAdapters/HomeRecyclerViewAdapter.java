package com.electro.electro_cart.ViewAdapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.electro.electro_cart.R;
import com.electro.electro_cart.models.Product;
import com.electro.electro_cart.utils.EnumProductType;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Product> products;
    private NavController navController;

    private static final int SLIDER_LAYOUT = 0;
    private static final int DAILY_DEALS_LAYOUT = 1;
    private static final int TRENDING_PRODUCTS_LAYOUT = 2;
    private static final int RECENTLY_VIEWED_LAYOUT = 3;
    private static final int POPULAR_PRODUCTS_LAYOUT = 4;
    private static final int ALL_PRODUCTS_LAYOUT = 5;
    private static final int LAPTOP_PRODUCTS_LAYOUT = 6;
    private static final int PHONE_PRODUCTS_LAYOUT = 7;

    int[] sliderImages = {R.drawable.image_slide_1, R.drawable.image_slide_2, R.drawable.image_slide_3, R.drawable.image_slide_4, R.drawable.image_slide_5};

    public HomeRecyclerViewAdapter(Context context, List<Product> products,NavController navController) {
        this.context = context;
        this.products = products;
        this.navController=navController;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;

        if (viewType == SLIDER_LAYOUT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout, parent, false);
            viewHolder = new SliderViewHolder(view);
        } else if (viewType==LAPTOP_PRODUCTS_LAYOUT){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_recyclerview_row, parent, false);
            viewHolder = new LaptopViewHolder(view);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_recyclerview_row, parent, false);
            viewHolder = new PhoneViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == SLIDER_LAYOUT) {
            SliderViewHolder sliderViewHolder=(SliderViewHolder)holder;

            sliderViewHolder.carouselView.setPageCount(sliderImages.length);

            sliderViewHolder.carouselView.setImageListener(new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {
                    imageView.setImageResource(sliderImages[position]);
                }
            });

            sliderViewHolder.carouselView.setImageClickListener(new ImageClickListener() {
                @Override
                public void onClick(int position) {
                    Toast.makeText(context, "Clicked item: " + position, Toast.LENGTH_SHORT).show();
                }
            });
        }else if (holder.getItemViewType()==LAPTOP_PRODUCTS_LAYOUT){
            LaptopViewHolder laptopViewHolder=(LaptopViewHolder) holder;

            laptopViewHolder.textView.setText("All Laptops");

            List<Product> laptopList=new ArrayList<>();

            for(Product product:products){
                if (product.getProductType()== EnumProductType.LAPTOP){
                    laptopList.add(product);
                }
            }

            HomeRowRecycleViewAdapter homeRowRecycleViewAdapter=new HomeRowRecycleViewAdapter(context,laptopList);

            laptopViewHolder.recyclerView.setHasFixedSize(true);
            laptopViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            laptopViewHolder.recyclerView.setAdapter(homeRowRecycleViewAdapter);

            laptopViewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "All Laptops", Toast.LENGTH_SHORT).show();
                    Bundle bundle=new Bundle();
                    bundle.putString("header","All Laptops");
                    bundle.putSerializable("productList", (Serializable) laptopList);
                    navController.navigate(R.id.action_to_navigation_generic_product_ist,bundle);
                }
            });

        }else {
                PhoneViewHolder phoneViewHolder=(PhoneViewHolder)holder;

                phoneViewHolder.textView.setText("All Phones");

                List<Product> phones=new ArrayList<>();

                for(Product product:products){
                    if (product.getProductType()== EnumProductType.PHONE){
                        phones.add(product);
                    }
                }

                HomeRowRecycleViewAdapter homeRowRecycleViewAdapter=new HomeRowRecycleViewAdapter(context,phones);

                phoneViewHolder.recyclerView.setHasFixedSize(true);
                phoneViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                phoneViewHolder.recyclerView.setAdapter(homeRowRecycleViewAdapter);

                phoneViewHolder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context, "All Phones", Toast.LENGTH_SHORT).show();
                        Bundle bundle=new Bundle();
                        bundle.putString("header","All Phones");
                        bundle.putSerializable("productList", (Serializable) phones);
                        navController.navigate(R.id.action_to_navigation_generic_product_ist,bundle);
                    }
                });
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return SLIDER_LAYOUT;
            case 1:
                return DAILY_DEALS_LAYOUT;
            case 2:
                return TRENDING_PRODUCTS_LAYOUT;
            case 3:
                return RECENTLY_VIEWED_LAYOUT;
            case 4:
                return POPULAR_PRODUCTS_LAYOUT;
            case 5:
                return ALL_PRODUCTS_LAYOUT;
            case 6:
                return LAPTOP_PRODUCTS_LAYOUT;
            default:
                return PHONE_PRODUCTS_LAYOUT;
        }
    }

    @Override
    public int getItemCount() {
        return 8;
    }

    public class SliderViewHolder extends RecyclerView.ViewHolder {

        CarouselView carouselView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);

            carouselView = itemView.findViewById(R.id.carouselView);
        }
    }

    public class LaptopViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView textView;
        Button button;

        public LaptopViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerView = itemView.findViewById(R.id.recyclerview_home_row);
            textView = itemView.findViewById(R.id.recyclerview_home_row_title);
            button = itemView.findViewById(R.id.btnMore);
        }
    }

    public class PhoneViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView textView;
        Button button;

        public PhoneViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerView = itemView.findViewById(R.id.recyclerview_home_row);
            textView = itemView.findViewById(R.id.recyclerview_home_row_title);
            button = itemView.findViewById(R.id.btnMore);
        }
    }
}
