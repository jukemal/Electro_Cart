package com.electro.electro_cart.ViewAdapters;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.electro.electro_cart.R;
import com.electro.electro_cart.models.Product;
import com.electro.electro_cart.models.Promotion;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/*
Recyclerview for displaying promotion list.
 */
public class PromotionRecyclerViewAdapter extends RecyclerView.Adapter<PromotionRecyclerViewAdapter.PromotionRecyclerViewHolder> {

    private Context context;
    private List<Product> products;
    private NavController navController;

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private FirebaseAnalytics firebaseAnalytics;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final CollectionReference collectionReferenceProduct = db.collection("products");

    public PromotionRecyclerViewAdapter(Context context, List<Product> products,NavController navController) {
        this.context = context;
        this.products = products;
        this.navController=navController;

        firebaseAnalytics=FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.setUserId(firebaseAuth.getUid());
    }

    @NonNull
    @Override
    public PromotionRecyclerViewAdapter.PromotionRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_promotion_list, parent, false);

        return new PromotionRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromotionRecyclerViewAdapter.PromotionRecyclerViewHolder holder, int position) {
        final Product product = products.get(position);

        /*
        Analytics for view_promotion event.
         */
        Bundle bundle=new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CURRENCY,"LKR");
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY,product.getProductType().toString());
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID,product.getId());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME,product.getName());
        bundle.putString(FirebaseAnalytics.Param.PRICE,String.valueOf(product.getPrice()));

        firebaseAnalytics.logEvent("view_promotion",bundle);

        final Promotion promotion= product.getPromotion();

        Glide.with(context)
                .load(storage.getReferenceFromUrl(product.getImage_links().get(0)))
                .transition(withCrossFade())
                .fitCenter()
                .error(R.drawable.error_loading)
                .fallback(R.drawable.error_loading)
                .into(holder.imageView);

        holder.textViewName.setText(product.getName());

        if (promotion==null){
            holder.textViewPrice.setText(String.valueOf(product.getPrice())+" LKR");
        }else {
            int price=product.getPrice()*(100-promotion.getDiscountPercentage())/100;

            holder.textViewPrice.setText(String.valueOf(price)+" LKR");

            holder.textViewPriceDiscount.setText(String.valueOf(product.getPrice())+" LKR");

            holder.textViewPriceDiscount.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

            holder.textViewPriceDiscount.setVisibility(View.VISIBLE);

            holder.textViewPromotionBadge.setVisibility(View.VISIBLE);
        }

        holder.ratingBar.setRating(product.getRating());

        /*
        Dialog box for showing promotion details.
         */
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogView= LayoutInflater.from(context).inflate(R.layout.layout_promotion_list_popup,null);

                /*
                Analytics for select_promotion event.
                 */
                Bundle bundle=new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CURRENCY,"LKR");
                bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY,product.getProductType().toString());
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID,product.getId());
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME,product.getName());
                bundle.putString(FirebaseAnalytics.Param.PRICE,String.valueOf(product.getPrice()));

                firebaseAnalytics.logEvent("select_promotion",bundle);

                MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(context);
                builder.setView(dialogView);
                final AlertDialog alertDialog=builder.show();

                ImageView imageViewPicture=dialogView.findViewById(R.id.image);

                Glide.with(context)
                        .load(storage.getReferenceFromUrl(product.getImage_links().get(0)))
                        .transition(withCrossFade())
                        .fitCenter()
                        .error(R.drawable.error_loading)
                        .fallback(R.drawable.error_loading)
                        .into(imageViewPicture);

                TextView textViewName=dialogView.findViewById(R.id.txt_name_item_product);

                textViewName.setText(product.getName());

                TextView textViewPrice=dialogView.findViewById(R.id.txt_price_item_product);

                int price=product.getPrice()*(100-promotion.getDiscountPercentage())/100;

                textViewPrice.setText(String.valueOf(price)+" LKR");

                TextView textViewPriceDiscount=dialogView.findViewById(R.id.textPriceDiscount);

                textViewPriceDiscount.setText(String.valueOf(product.getPrice())+" LKR");

                textViewPriceDiscount.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

                RatingBar ratingBar=dialogView.findViewById(R.id.product_rating_product);

                ratingBar.setRating(product.getRating());

                TextView textViewPromotionDescription=dialogView.findViewById(R.id.description);

                textViewPromotionDescription.setText(promotion.getDescription());

                TextView textViewDate=dialogView.findViewById(R.id.date);

                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MMM-dd", Locale.ENGLISH);

                textViewDate.setText("Grab This Item Before "+simpleDateFormat.format(promotion.getEndDate())+" for "+String.valueOf(promotion.getDiscountPercentage())+"% Off");

                Button buttonGo=dialogView.findViewById(R.id.btn);

                buttonGo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        Bundle bundle = new Bundle();
                        bundle.putString("id", product.getId());
                        navController.navigate(R.id.action_to_navigation_product, bundle);
                    }
                });

                ImageView buttonClose=dialogView.findViewById(R.id.close);

                buttonClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    /*
    Innerclass for the layout.
     */
    public class PromotionRecyclerViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textViewName;
        TextView textViewPrice;
        TextView textViewPriceDiscount;

        RatingBar ratingBar;

        TextView textViewPromotionBadge;

        public PromotionRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imgThumb_item_product);
            textViewName = itemView.findViewById(R.id.txt_name_item_product);
            textViewPrice = itemView.findViewById(R.id.txt_price_item_product);
            textViewPriceDiscount = itemView.findViewById(R.id.textPriceDiscount);
            ratingBar = itemView.findViewById(R.id.product_rating_product);
            textViewPromotionBadge = itemView.findViewById(R.id.promotion_badge);        }
    }
}
