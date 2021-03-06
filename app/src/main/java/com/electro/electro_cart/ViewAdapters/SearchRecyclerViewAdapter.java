package com.electro.electro_cart.ViewAdapters;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.electro.electro_cart.R;
import com.electro.electro_cart.models.Product;
import com.electro.electro_cart.models.Promotion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/*
Recyclerview for search interface.
 */
public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.SearchRecyclerViewHolder> implements Filterable {

    private Context context;
    private List<Product> productList;
    private List<Product> filteredProductList;

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final CollectionReference collectionReferenceProduct = db.collection("products");

    private final CollectionReference collectionReferenceFavourite = db.collection("users")
            .document(firebaseAuth.getCurrentUser().getUid())
            .collection("favourites");

    public SearchRecyclerViewAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        filteredProductList = new ArrayList<>(productList);
    }

    @NonNull
    @Override
    public SearchRecyclerViewAdapter.SearchRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_list, parent, false);

        return new SearchRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchRecyclerViewAdapter.SearchRecyclerViewHolder holder, int position) {
        if (productList.size() > 0) {
            final Product product = productList.get(position);

            final Promotion promotion=product.getPromotion();

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

            collectionReferenceFavourite.document(product.getId()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.e("Product Favourite", "DocumentSnapshot data: " + document.getData());

                                    if (document.get("favourite").toString().equals("true")) {
                                        holder.toggleButtonFavourite.setChecked(true);
                                    }
                                } else {
                                    Log.e("Product Favourite", "No such document");
                                    holder.toggleButtonFavourite.setChecked(false);

                                    Map<String, Object> map = new HashMap<>();
                                    map.put("favourite", "false");

                                    collectionReferenceFavourite.document(product.getId()).set(map)
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

            holder.toggleButtonFavourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        collectionReferenceFavourite.document(product.getId()).update("favourite", "true")
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
                        collectionReferenceFavourite.document(product.getId()).update("favourite", "false")
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

            holder.ratingBar.setRating(product.getRating());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("id", product.getId());
                    Navigation.findNavController(view).navigate(R.id.action_to_navigation_product, bundle);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    /*
    Get typed string from search box and updating the list.
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<Product> filteredList = new ArrayList<>();

                if (charSequence == null || charSequence.length() == 0) {
                    filteredList.addAll(filteredProductList);
                } else {
                    String charSequenceString = charSequence.toString().toLowerCase().trim();

                    for (Product product : filteredProductList) {
                        if (product.getName().toLowerCase().trim().contains(charSequenceString)) {
                            filteredList.add(product);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                productList.clear();
                productList.addAll((List) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }

    /*
    Innerclass for layout.
     */
    public class SearchRecyclerViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textViewName;
        TextView textViewPrice;
        TextView textViewPriceDiscount;

        ToggleButton toggleButtonFavourite;
        RatingBar ratingBar;

        TextView textViewPromotionBadge;

        public SearchRecyclerViewHolder(@NonNull View itemView) {
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
