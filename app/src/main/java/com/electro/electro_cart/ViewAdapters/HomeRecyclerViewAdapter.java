package com.electro.electro_cart.ViewAdapters;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.electro.electro_cart.R;
import com.electro.electro_cart.models.Product;
import com.electro.electro_cart.models.Promotion;
import com.electro.electro_cart.utils.EnumProductType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/*
Recyclerview for home screen.
 */
public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Product> products;
    private NavController navController;

    /*
    Layouts for each section of the home page
     */
    private static final int SLIDER_LAYOUT = 0;
    private static final int DAILY_DEALS_LAYOUT = 1;
    private static final int TRENDING_PRODUCTS_LAYOUT = 2;
    private static final int RECENTLY_VIEWED_LAYOUT = 3;
    private static final int POPULAR_PRODUCTS_LAYOUT = 4;
    private static final int ALL_PRODUCTS_LAYOUT = 5;
    private static final int LAPTOP_PRODUCTS_LAYOUT = 6;
    private static final int PHONE_PRODUCTS_LAYOUT = 7;
    private static final int SINGLE_PRODUCT_LAYOUT = 8;

    int[] sliderImages = {R.drawable.image_slide_1, R.drawable.image_slide_2, R.drawable.image_slide_3, R.drawable.image_slide_4, R.drawable.image_slide_5};

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final CollectionReference collectionReferenceProduct = db.collection("products");

    private final CollectionReference collectionReferenceFavourite = db.collection("users")
            .document(firebaseAuth.getCurrentUser().getUid())
            .collection("favourites");

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
        } else if (viewType==DAILY_DEALS_LAYOUT){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_recyclerview_row, parent, false);
            viewHolder = new DailyDealsViewHolder(view);
        }else if (viewType==TRENDING_PRODUCTS_LAYOUT){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_recyclerview_row, parent, false);
            viewHolder = new TrandingProductViewHolder(view);
        }else if (viewType==RECENTLY_VIEWED_LAYOUT){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_recyclerview_row, parent, false);
            viewHolder = new RecentlyViewedViewHolder(view);
        }else if (viewType==POPULAR_PRODUCTS_LAYOUT){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_recyclerview_row, parent, false);
            viewHolder = new PopularProductViewHolder(view);
        }else if (viewType==ALL_PRODUCTS_LAYOUT){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_recyclerview_row, parent, false);
            viewHolder = new AllProductViewHolder(view);
        }else if (viewType==LAPTOP_PRODUCTS_LAYOUT){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_recyclerview_row, parent, false);
            viewHolder = new LaptopViewHolder(view);
        } else if (viewType==PHONE_PRODUCTS_LAYOUT){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_recyclerview_row, parent, false);
            viewHolder = new PhoneViewHolder(view);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_list, parent, false);
            viewHolder = new SingleProductLayoutViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == SLIDER_LAYOUT) {
            /*
            Slider Layout
             */

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

        }else if (holder.getItemViewType()==DAILY_DEALS_LAYOUT){
            /*
            Daily Deals Layout
             */

            DailyDealsViewHolder dailyDealsViewHolder=(DailyDealsViewHolder)holder;

            dailyDealsViewHolder.textView.setText("Daily Deals");

            final List<Product> productList=products;

            Collections.shuffle(productList);

            HomeRowRecycleViewAdapter homeRowRecycleViewAdapter=new HomeRowRecycleViewAdapter(context,productList);

            dailyDealsViewHolder.recyclerView.setHasFixedSize(true);
            dailyDealsViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            dailyDealsViewHolder.recyclerView.setAdapter(homeRowRecycleViewAdapter);

            dailyDealsViewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle=new Bundle();
                    bundle.putString("header","Daily Deals");
                    bundle.putSerializable("productList", (Serializable) productList);
                    navController.navigate(R.id.action_to_navigation_generic_product_ist,bundle);
                }
            });

        }else if (holder.getItemViewType()==TRENDING_PRODUCTS_LAYOUT){
            /*
            Trending Products Layout
             */

            TrandingProductViewHolder trandingProductViewHolder=(TrandingProductViewHolder) holder;

            trandingProductViewHolder.textView.setText("Trending Products");

            final List<Product> productList=products;

            Collections.shuffle(productList);

            HomeRowRecycleViewAdapter homeRowRecycleViewAdapter=new HomeRowRecycleViewAdapter(context,productList);

            trandingProductViewHolder.recyclerView.setHasFixedSize(true);
            trandingProductViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            trandingProductViewHolder.recyclerView.setAdapter(homeRowRecycleViewAdapter);

            trandingProductViewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle=new Bundle();
                    bundle.putString("header","Trending Products");
                    bundle.putSerializable("productList", (Serializable) productList);
                    navController.navigate(R.id.action_to_navigation_generic_product_ist,bundle);
                }
            });


        }else if (holder.getItemViewType()==RECENTLY_VIEWED_LAYOUT){
            /*
            Recently Viewed Layout
             */

            RecentlyViewedViewHolder recentlyViewedViewHolder=(RecentlyViewedViewHolder) holder;

            recentlyViewedViewHolder.textView.setText("Recently Viewed");

            final List<Product> productList=products;

            Collections.shuffle(productList);

            HomeRowRecycleViewAdapter homeRowRecycleViewAdapter=new HomeRowRecycleViewAdapter(context,productList);

            recentlyViewedViewHolder.recyclerView.setHasFixedSize(true);
            recentlyViewedViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            recentlyViewedViewHolder.recyclerView.setAdapter(homeRowRecycleViewAdapter);

            recentlyViewedViewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle=new Bundle();
                    bundle.putString("header","Recently Viewed");
                    bundle.putSerializable("productList", (Serializable) productList);
                    navController.navigate(R.id.action_to_navigation_generic_product_ist,bundle);
                }
            });

        }else if (holder.getItemViewType()==POPULAR_PRODUCTS_LAYOUT){
            /*
            Popular Products Layout
             */

            PopularProductViewHolder popularProductViewHolder=(PopularProductViewHolder) holder;

            popularProductViewHolder.textView.setText("Popular Products");

            final List<Product> productList=products;

            Collections.shuffle(productList);

            HomeRowRecycleViewAdapter homeRowRecycleViewAdapter=new HomeRowRecycleViewAdapter(context,productList);

            popularProductViewHolder.recyclerView.setHasFixedSize(true);
            popularProductViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            popularProductViewHolder.recyclerView.setAdapter(homeRowRecycleViewAdapter);

            popularProductViewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle=new Bundle();
                    bundle.putString("header","Popular Products");
                    bundle.putSerializable("productList", (Serializable) productList);
                    navController.navigate(R.id.action_to_navigation_generic_product_ist,bundle);
                }
            });

        }else if (holder.getItemViewType()==ALL_PRODUCTS_LAYOUT){
            /*
            All Products Layout
             */

            AllProductViewHolder allProductViewHolder=(AllProductViewHolder) holder;

            allProductViewHolder.textView.setText("All Products");

            final List<Product> productList=products;

            Collections.shuffle(productList);

            HomeRowRecycleViewAdapter homeRowRecycleViewAdapter=new HomeRowRecycleViewAdapter(context,productList);

            allProductViewHolder.recyclerView.setHasFixedSize(true);
            allProductViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            allProductViewHolder.recyclerView.setAdapter(homeRowRecycleViewAdapter);

            allProductViewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle=new Bundle();
                    bundle.putString("header","All Products");
                    bundle.putSerializable("productList", (Serializable) productList);
                    navController.navigate(R.id.action_to_navigation_generic_product_ist,bundle);
                }
            });

        }else if (holder.getItemViewType()==LAPTOP_PRODUCTS_LAYOUT){
            /*
            Laptop Layout
             */

            LaptopViewHolder laptopViewHolder=(LaptopViewHolder) holder;

            laptopViewHolder.textView.setText("All Laptops");

            final List<Product> laptopList=new ArrayList<>();

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
                    Bundle bundle=new Bundle();
                    bundle.putString("header","All Laptops");
                    bundle.putSerializable("productList", (Serializable) laptopList);
                    navController.navigate(R.id.action_to_navigation_generic_product_ist,bundle);
                }
            });

        }else if (holder.getItemViewType()==PHONE_PRODUCTS_LAYOUT){
            /*
            Phone Layout
             */

            PhoneViewHolder phoneViewHolder=(PhoneViewHolder)holder;

            phoneViewHolder.textView.setText("All Phones");

            final List<Product> phones=new ArrayList<>();

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

        }else {
            /*
            Product List Layout
             */

            SingleProductLayoutViewHolder singleProductLayoutViewHolder = (SingleProductLayoutViewHolder) holder;

            final Product product1 = products.get(position - 8);

            final Promotion promotion=product1.getPromotion();

            Glide.with(context)
                    .load(storage.getReferenceFromUrl(product1.getImage_links().get(0)))
                    .transition(withCrossFade())
                    .fitCenter()
                    .error(R.drawable.error_loading)
                    .fallback(R.drawable.error_loading)
                    .into(singleProductLayoutViewHolder.imageView);

            singleProductLayoutViewHolder.textViewName.setText(product1.getName());

            if (promotion==null){
                singleProductLayoutViewHolder.textViewPrice.setText(String.valueOf(product1.getPrice())+" LKR");
            }else {
                int price=product1.getPrice()*(100-promotion.getDiscountPercentage())/100;

                singleProductLayoutViewHolder.textViewPrice.setText(String.valueOf(price)+" LKR");

                singleProductLayoutViewHolder.textViewPriceDiscount.setText(String.valueOf(product1.getPrice())+" LKR");

                singleProductLayoutViewHolder.textViewPriceDiscount.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

                singleProductLayoutViewHolder.textViewPriceDiscount.setVisibility(View.VISIBLE);

                singleProductLayoutViewHolder.textViewPromotionBadge.setVisibility(View.VISIBLE);
            }

            collectionReferenceFavourite.document(product1.getId()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.e("Product Favourite", "DocumentSnapshot data: " + document.getData());

                                    if (document.get("favourite").toString().equals("true")) {
                                        singleProductLayoutViewHolder.toggleButtonFavourite.setChecked(true);
                                    }
                                } else {
                                    Log.e("Product Favourite", "No such document");
                                    singleProductLayoutViewHolder.toggleButtonFavourite.setChecked(false);

                                    Map<String, Object> map = new HashMap<>();
                                    map.put("favourite", "false");

                                    collectionReferenceFavourite.document(product1.getId()).set(map)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("Product Favourite", "DocumentSnapshot successfully written!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("Product Favourite", "Error writing document", e);
                                                }
                                            });

                                }
                            } else {
                                Log.e("Product Favourite", "get failed with ", task.getException());
                            }
                        }
                    });

            singleProductLayoutViewHolder.toggleButtonFavourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        collectionReferenceFavourite.document(product1.getId()).update("favourite", "true")
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.e("Product Favourite", "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("Product Favourite", "Error updating document", e);
                                    }
                                });
                    } else {
                        collectionReferenceFavourite.document(product1.getId()).update("favourite", "false")
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.e("Product Favourite", "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("Product Favourite", "Error updating document", e);
                                    }
                                });
                    }
                }
            });

            singleProductLayoutViewHolder.ratingBar.setRating(product1.getRating());

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

    /*
    Returns Layout type depending on the position.
     */
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
            case 7:
                return PHONE_PRODUCTS_LAYOUT;
            default:
                return SINGLE_PRODUCT_LAYOUT;
        }
    }

    @Override
    public int getItemCount() {
        return products.size()+8;
    }

    /*
    Innerclass for Slider layout.
     */
    public class SliderViewHolder extends RecyclerView.ViewHolder {

        CarouselView carouselView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);

            carouselView = itemView.findViewById(R.id.carouselView);
        }
    }

    /*
    Innerclass for Daily Deals layout.
     */
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

    /*
    Innerclass for Trending Products layout.
     */
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

    /*
    Innerclass for Recently Viewed layout.
     */
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

    /*
    Innerclass for Popular Products layout.
     */
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

    /*
    Innerclass for All Products layout.
     */
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

    /*
    Innerclass for Laptop layout.
     */
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

    /*
    Innerclass for Phone layout.
     */
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

    /*
    Innerclass for Single Product layout.
     */
    public class SingleProductLayoutViewHolder extends RecyclerView.ViewHolder {


        ImageView imageView;
        TextView textViewName;
        TextView textViewPrice;
        TextView textViewPriceDiscount;

        ToggleButton toggleButtonFavourite;
        RatingBar ratingBar;

        TextView textViewPromotionBadge;

        public SingleProductLayoutViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imgThumb_item_product);
            textViewName = itemView.findViewById(R.id.txt_name_item_product);
            textViewPrice = itemView.findViewById(R.id.txt_price_item_product);
            textViewPriceDiscount = itemView.findViewById(R.id.textPriceDiscount);
            toggleButtonFavourite = itemView.findViewById(R.id.set_favourite);
            ratingBar = itemView.findViewById(R.id.product_rating_product);
            textViewPromotionBadge = itemView.findViewById(R.id.promotion_badge);
        }
    }
}
