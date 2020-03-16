package com.electro.electro_cart.ViewAdapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.electro.electro_cart.R;
import com.electro.electro_cart.models.CartItem;
import com.electro.electro_cart.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.ArCoreApk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ProductRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Product> products;
    private String id;
    private ProductRecycleViewAdapterClickInterface productRecycleViewAdapterClickInterface;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final CollectionReference collectionReferenceProduct = db.collection("products");

    private final CollectionReference collectionReferenceCart = db.collection("users")
            .document(firebaseAuth.getCurrentUser().getUid())
            .collection("cart");

    private final DocumentReference documentReferenceProduct;

    private static final int MAIN_LAYOUT = 0;
    private static final int BOUGHT_TOGETHER_LAYOUT = 1;
    private static final int RECOMMENDED_LAYOUT = 2;
    private static final int SPONSORED_LAYOUT = 3;
    private static final int QUESTION_LAYOUT = 4;
    private static final int BY_FEATURE_LAYOUT = 5;
    private static final int COMMENT_LAYOUT = 6;

    public ProductRecycleViewAdapter(Context context, List<Product> products, String id, ProductRecycleViewAdapterClickInterface productRecycleViewAdapterClickInterface) {
        this.context = context;
        this.products = products;
        this.id = id;
        this.productRecycleViewAdapterClickInterface = productRecycleViewAdapterClickInterface;

        documentReferenceProduct = collectionReferenceProduct.document(id);
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
                if (p.getId().equals(id)) {
                    product = p;
                    break;
                } else {
                    product = products.get(0);
                }
            }

            final MainLayoutViewHolder mainLayoutViewHolder = (MainLayoutViewHolder) holder;

            Glide.with(context)
                    .load(storage.getReferenceFromUrl(product.getImage_links().get(0)))
                    .transition(withCrossFade())
                    .fitCenter()
                    .error(R.drawable.error_loading)
                    .fallback(R.drawable.error_loading)
                    .into(mainLayoutViewHolder.imageViewImage);

            mainLayoutViewHolder.textName.setText(product.getName());

            final boolean isFavourite = product.isFavourite();

            mainLayoutViewHolder.toggleButtonFavourite.setChecked(isFavourite);

            mainLayoutViewHolder.toggleButtonFavourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    collectionReferenceProduct.document(id).update("favourite", isChecked);
                }
            });

            ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(context);

            if (!availability.isSupported()) {
                mainLayoutViewHolder.buttonAR.setEnabled(false);
            }

            mainLayoutViewHolder.buttonAR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent sceneViewerIntent = new Intent(Intent.ACTION_VIEW);
                    sceneViewerIntent.setData(Uri.parse("https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Avocado/glTF/Avocado.gltf?mode=ar_only"));
                    sceneViewerIntent.setPackage("com.google.ar.core");
                    context.startActivity(sceneViewerIntent);
                }
            });

            if (product.getDescription() == null) {
                mainLayoutViewHolder.textViewDescription.setVisibility(View.GONE);
            } else {
                mainLayoutViewHolder.textViewDescription.setText(product.getDescription());
            }

            mainLayoutViewHolder.textViewPrice.setText(String.valueOf(product.getPrice()) + " LKR");

            mainLayoutViewHolder.ratingBar.setRating(product.getRating());

            //------------------------------------------------------------------------------------------------------------

            //Cart

            mainLayoutViewHolder.floatingActionButtonDown.setEnabled(false);
            mainLayoutViewHolder.buttonRemoveFromCart.setVisibility(View.GONE);
            mainLayoutViewHolder.buttonAddToCart.setEnabled(false);

            collectionReferenceCart.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CartItem cartItem = document.toObject(CartItem.class);

                            if (cartItem.getProductID().equals(id)) {
                                mainLayoutViewHolder.textViewCartCount.setText(String.valueOf(cartItem.getItemCount()));
                                mainLayoutViewHolder.buttonAddToCart.setText("Update Cart");

                                mainLayoutViewHolder.floatingActionButtonDown.setEnabled(true);
                                mainLayoutViewHolder.buttonRemoveFromCart.setVisibility(View.VISIBLE);
                                mainLayoutViewHolder.buttonAddToCart.setEnabled(true);

                                break;
                            }
                        }
                    }
                }
            });

            mainLayoutViewHolder.floatingActionButtonUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mainLayoutViewHolder.textViewCartCount.setText(String.valueOf(Integer.parseInt(mainLayoutViewHolder.textViewCartCount.getText().toString()) + 1));
                    mainLayoutViewHolder.floatingActionButtonDown.setEnabled(true);
                    mainLayoutViewHolder.buttonAddToCart.setEnabled(true);
                }
            });

            mainLayoutViewHolder.floatingActionButtonDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Integer.parseInt(mainLayoutViewHolder.textViewCartCount.getText().toString()) <= 0) {
                        mainLayoutViewHolder.floatingActionButtonDown.setEnabled(false);
                        mainLayoutViewHolder.buttonAddToCart.setEnabled(false);
                    } else {
                        mainLayoutViewHolder.textViewCartCount.setText(String.valueOf(Integer.parseInt(mainLayoutViewHolder.textViewCartCount.getText().toString()) - 1));
                    }
                }
            });

            mainLayoutViewHolder.buttonAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Integer.parseInt(mainLayoutViewHolder.textViewCartCount.getText().toString())>=1) {
                        CartItem cartItem = CartItem.builder()
                                .ProductID(id)
                                .productReference(documentReferenceProduct)
                                .itemCount(Integer.parseInt(mainLayoutViewHolder.textViewCartCount.getText().toString()))
                                .build();

                        collectionReferenceCart.document(id).set(cartItem)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();
                                        Navigation.findNavController(view).navigate(R.id.action_to_navigation_cart);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Error Adding to Cart. Try Again", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

            mainLayoutViewHolder.buttonRemoveFromCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    collectionReferenceCart.document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Removed from Cart.", Toast.LENGTH_SHORT).show();
                            productRecycleViewAdapterClickInterface.RemoveFromCartClicked();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            });

            //----------------------------------------------------------------------------------------------------------
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

        ImageView imageViewImage;
        TextView textName;
        ToggleButton toggleButtonFavourite;
        Button buttonAR;
        TextView textViewDescription;
        TextView textViewPrice;
        RatingBar ratingBar;
        FloatingActionButton floatingActionButtonDown;
        FloatingActionButton floatingActionButtonUp;
        TextView textViewCartCount;
        Button buttonAddToCart;
        Button buttonRemoveFromCart;

        public MainLayoutViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewImage = itemView.findViewById(R.id.imageView_image_product);
            textName = itemView.findViewById(R.id.product_name_product);
            toggleButtonFavourite = itemView.findViewById(R.id.set_favourite_product);
            buttonAR = itemView.findViewById(R.id.btn_ar_product);
            textViewDescription = itemView.findViewById(R.id.product_description_product);
            textViewPrice = itemView.findViewById(R.id.product_price_product);
            ratingBar = itemView.findViewById(R.id.product_rating_product);
            floatingActionButtonDown = itemView.findViewById(R.id.floating_action_button_cart_down_product);
            floatingActionButtonUp = itemView.findViewById(R.id.floating_action_button_cart_up_product);
            textViewCartCount = itemView.findViewById(R.id.text_cart_num_product);
            buttonAddToCart = itemView.findViewById(R.id.btn_add_to_cart_product);
            buttonRemoveFromCart = itemView.findViewById(R.id.btn_remove_from_cart_product);
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
