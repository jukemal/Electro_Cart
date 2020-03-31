package com.electro.electro_cart.ViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.electro.electro_cart.R;
import com.electro.electro_cart.models.Product;
import com.electro.electro_cart.models.User;
import com.electro.electro_cart.utils.EnumProductType;
import com.google.firebase.storage.FirebaseStorage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class StoreRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Product> products;
    private NavController navController;
    private User store;

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private static final int HEADER_LAYOUT = 0;
    private static final int DAILY_DEALS_LAYOUT = 1;
    private static final int TRENDING_PRODUCTS_LAYOUT = 2;
    private static final int RECENTLY_VIEWED_LAYOUT = 3;
    private static final int POPULAR_PRODUCTS_LAYOUT = 4;
    private static final int ALL_PRODUCTS_LAYOUT = 5;
    private static final int LAPTOP_PRODUCTS_LAYOUT = 6;
    private static final int PHONE_PRODUCTS_LAYOUT = 7;
    private static final int SINGLE_PRODUCT_LAYOUT = 8;

    public StoreRecyclerViewAdapter(Context context, List<Product> products, NavController navController,User store) {
        this.context = context;
        this.products = products;
        this.navController = navController;
        this.store=store;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;

        if (viewType == HEADER_LAYOUT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_store_header, parent, false);
            viewHolder = new HeaderViewHolder(view);
        } else if (viewType == DAILY_DEALS_LAYOUT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_recyclerview_row, parent, false);
            viewHolder = new DailyDealsViewHolder(view);
        } else if (viewType == TRENDING_PRODUCTS_LAYOUT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_recyclerview_row, parent, false);
            viewHolder = new TrandingProductViewHolder(view);
        } else if (viewType == RECENTLY_VIEWED_LAYOUT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_recyclerview_row, parent, false);
            viewHolder = new RecentlyViewedViewHolder(view);
        } else if (viewType == POPULAR_PRODUCTS_LAYOUT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_recyclerview_row, parent, false);
            viewHolder = new PopularProductViewHolder(view);
        } else if (viewType == ALL_PRODUCTS_LAYOUT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_recyclerview_row, parent, false);
            viewHolder = new AllProductViewHolder(view);
        } else if (viewType == LAPTOP_PRODUCTS_LAYOUT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_recyclerview_row, parent, false);
            viewHolder = new LaptopViewHolder(view);
        } else if (viewType == PHONE_PRODUCTS_LAYOUT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_recyclerview_row, parent, false);
            viewHolder = new PhoneViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_list, parent, false);
            viewHolder = new SingleProductLayoutViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == HEADER_LAYOUT) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;

            headerViewHolder.textView.setText(store.getName().toUpperCase());


        } else if (holder.getItemViewType() == DAILY_DEALS_LAYOUT) {
            DailyDealsViewHolder dailyDealsViewHolder = (DailyDealsViewHolder) holder;

            dailyDealsViewHolder.textView.setText("Daily Deals");

            List<Product> productList = products;

            Collections.shuffle(productList);

            HomeRowRecycleViewAdapter homeRowRecycleViewAdapter = new HomeRowRecycleViewAdapter(context, productList);

            dailyDealsViewHolder.recyclerView.setHasFixedSize(true);
            dailyDealsViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            dailyDealsViewHolder.recyclerView.setAdapter(homeRowRecycleViewAdapter);

            dailyDealsViewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("header", "Daily Deals");
                    bundle.putSerializable("productList", (Serializable) productList);
                    navController.navigate(R.id.action_to_navigation_generic_product_ist, bundle);
                }
            });
        } else if (holder.getItemViewType() == TRENDING_PRODUCTS_LAYOUT) {
            TrandingProductViewHolder trandingProductViewHolder = (TrandingProductViewHolder) holder;

            trandingProductViewHolder.textView.setText("Trending Products");

            List<Product> productList = products;

            Collections.shuffle(productList);

            HomeRowRecycleViewAdapter homeRowRecycleViewAdapter = new HomeRowRecycleViewAdapter(context, productList);

            trandingProductViewHolder.recyclerView.setHasFixedSize(true);
            trandingProductViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            trandingProductViewHolder.recyclerView.setAdapter(homeRowRecycleViewAdapter);

            trandingProductViewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("header", "Trending Products");
                    bundle.putSerializable("productList", (Serializable) productList);
                    navController.navigate(R.id.action_to_navigation_generic_product_ist, bundle);
                }
            });
        } else if (holder.getItemViewType() == RECENTLY_VIEWED_LAYOUT) {
            RecentlyViewedViewHolder recentlyViewedViewHolder = (RecentlyViewedViewHolder) holder;

            recentlyViewedViewHolder.textView.setText("Recently Viewed");

            List<Product> productList = products;

            Collections.shuffle(productList);

            HomeRowRecycleViewAdapter homeRowRecycleViewAdapter = new HomeRowRecycleViewAdapter(context, productList);

            recentlyViewedViewHolder.recyclerView.setHasFixedSize(true);
            recentlyViewedViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            recentlyViewedViewHolder.recyclerView.setAdapter(homeRowRecycleViewAdapter);

            recentlyViewedViewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("header", "Recently Viewed");
                    bundle.putSerializable("productList", (Serializable) productList);
                    navController.navigate(R.id.action_to_navigation_generic_product_ist, bundle);
                }
            });
        } else if (holder.getItemViewType() == POPULAR_PRODUCTS_LAYOUT) {
            PopularProductViewHolder popularProductViewHolder = (PopularProductViewHolder) holder;

            popularProductViewHolder.textView.setText("Popular Products");

            List<Product> productList = products;

            Collections.shuffle(productList);

            HomeRowRecycleViewAdapter homeRowRecycleViewAdapter = new HomeRowRecycleViewAdapter(context, productList);

            popularProductViewHolder.recyclerView.setHasFixedSize(true);
            popularProductViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            popularProductViewHolder.recyclerView.setAdapter(homeRowRecycleViewAdapter);

            popularProductViewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("header", "Popular Products");
                    bundle.putSerializable("productList", (Serializable) productList);
                    navController.navigate(R.id.action_to_navigation_generic_product_ist, bundle);
                }
            });
        } else if (holder.getItemViewType() == ALL_PRODUCTS_LAYOUT) {
            AllProductViewHolder allProductViewHolder = (AllProductViewHolder) holder;

            allProductViewHolder.textView.setText("All Products");

            List<Product> productList = products;

            Collections.shuffle(productList);

            HomeRowRecycleViewAdapter homeRowRecycleViewAdapter = new HomeRowRecycleViewAdapter(context, productList);

            allProductViewHolder.recyclerView.setHasFixedSize(true);
            allProductViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            allProductViewHolder.recyclerView.setAdapter(homeRowRecycleViewAdapter);

            allProductViewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("header", "All Products");
                    bundle.putSerializable("productList", (Serializable) productList);
                    navController.navigate(R.id.action_to_navigation_generic_product_ist, bundle);
                }
            });
        } else if (holder.getItemViewType() == LAPTOP_PRODUCTS_LAYOUT) {
            LaptopViewHolder laptopViewHolder = (LaptopViewHolder) holder;

            laptopViewHolder.textView.setText("All Laptops");

            List<Product> laptopList = new ArrayList<>();

            for (Product product : products) {
                if (product.getProductType() == EnumProductType.LAPTOP) {
                    laptopList.add(product);
                }
            }

            HomeRowRecycleViewAdapter homeRowRecycleViewAdapter = new HomeRowRecycleViewAdapter(context, laptopList);

            laptopViewHolder.recyclerView.setHasFixedSize(true);
            laptopViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            laptopViewHolder.recyclerView.setAdapter(homeRowRecycleViewAdapter);

            laptopViewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("header", "All Laptops");
                    bundle.putSerializable("productList", (Serializable) laptopList);
                    navController.navigate(R.id.action_to_navigation_generic_product_ist, bundle);
                }
            });

        } else if (holder.getItemViewType() == PHONE_PRODUCTS_LAYOUT) {
            PhoneViewHolder phoneViewHolder = (PhoneViewHolder) holder;

            phoneViewHolder.textView.setText("All Phones");

            List<Product> phones = new ArrayList<>();

            for (Product product : products) {
                if (product.getProductType() == EnumProductType.PHONE) {
                    phones.add(product);
                }
            }

            HomeRowRecycleViewAdapter homeRowRecycleViewAdapter = new HomeRowRecycleViewAdapter(context, phones);

            phoneViewHolder.recyclerView.setHasFixedSize(true);
            phoneViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            phoneViewHolder.recyclerView.setAdapter(homeRowRecycleViewAdapter);

            phoneViewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "All Phones", Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("header", "All Phones");
                    bundle.putSerializable("productList", (Serializable) phones);
                    navController.navigate(R.id.action_to_navigation_generic_product_ist, bundle);
                }
            });
        } else {
            SingleProductLayoutViewHolder singleProductLayoutViewHolder = (SingleProductLayoutViewHolder) holder;

            final Product product1 = products.get(position - 8);

            Glide.with(context)
                    .load(storage.getReferenceFromUrl(product1.getImage_links().get(0)))
                    .transition(withCrossFade())
                    .fitCenter()
                    .error(R.drawable.error_loading)
                    .fallback(R.drawable.error_loading)
                    .into(singleProductLayoutViewHolder.imageView);

            singleProductLayoutViewHolder.textViewName.setText(product1.getName());

            singleProductLayoutViewHolder.textViewPrice.setText(String.valueOf(product1.getPrice()));

            singleProductLayoutViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("id", product1.getId());
                    Navigation.findNavController(view).navigate(R.id.action_to_navigation_product, bundle);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return products.size() + 8;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return HEADER_LAYOUT;
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
            case 7:
                return PHONE_PRODUCTS_LAYOUT;
            default:
                return SINGLE_PRODUCT_LAYOUT;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.image_header_store);
            textView=itemView.findViewById(R.id.name_store);

        }
    }

    public class DailyDealsViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView textView;
        Button button;

        public DailyDealsViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerView = itemView.findViewById(R.id.recyclerview_home_row);
            textView = itemView.findViewById(R.id.recyclerview_home_row_title);
            button = itemView.findViewById(R.id.btnMore);
        }
    }

    public class TrandingProductViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView textView;
        Button button;

        public TrandingProductViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerView = itemView.findViewById(R.id.recyclerview_home_row);
            textView = itemView.findViewById(R.id.recyclerview_home_row_title);
            button = itemView.findViewById(R.id.btnMore);
        }
    }

    public class RecentlyViewedViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView textView;
        Button button;

        public RecentlyViewedViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerView = itemView.findViewById(R.id.recyclerview_home_row);
            textView = itemView.findViewById(R.id.recyclerview_home_row_title);
            button = itemView.findViewById(R.id.btnMore);
        }
    }

    public class PopularProductViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView textView;
        Button button;

        public PopularProductViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerView = itemView.findViewById(R.id.recyclerview_home_row);
            textView = itemView.findViewById(R.id.recyclerview_home_row_title);
            button = itemView.findViewById(R.id.btnMore);
        }
    }

    public class AllProductViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView textView;
        Button button;

        public AllProductViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerView = itemView.findViewById(R.id.recyclerview_home_row);
            textView = itemView.findViewById(R.id.recyclerview_home_row_title);
            button = itemView.findViewById(R.id.btnMore);
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

    public class SingleProductLayoutViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textViewName;
        TextView textViewPrice;

        public SingleProductLayoutViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imgThumb_item_product);
            textViewName = itemView.findViewById(R.id.txt_name_item_product);
            textViewPrice = itemView.findViewById(R.id.txt_price_item_product);
        }
    }
}
