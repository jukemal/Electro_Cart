package com.electro.electro_cart.ViewAdapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.electro.electro_cart.R;
import com.electro.electro_cart.models.CartItem;
import com.electro.electro_cart.models.Product;
import com.electro.electro_cart.models.Promotion;
import com.electro.electro_cart.models.Question;
import com.electro.electro_cart.models.Rating;
import com.electro.electro_cart.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.ArCoreApk;
import com.google.firebase.Timestamp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 *Recyclerview for product page.
 */
public class ProductRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Product> products;
    private String id;
    private NavController navController;

    private ProductRecycleViewAdapterClickInterface productRecycleViewAdapterClickInterface;

    private FirebaseAnalytics firebaseAnalytics;

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final CollectionReference collectionReferenceProduct = db.collection("products");

    private final DocumentReference documentReferenceCurrentUser=db.collection("users")
            .document(firebaseAuth.getCurrentUser().getUid());

    private final CollectionReference collectionReferenceCart = db.collection("users")
            .document(firebaseAuth.getCurrentUser().getUid())
            .collection("cart");

    private final CollectionReference collectionReferenceFavourite = db.collection("users")
            .document(firebaseAuth.getCurrentUser().getUid())
            .collection("favourites");

    private final CollectionReference collectionReferenceQuestions;

    private final CollectionReference collectionReferenceRatings;

    private final DocumentReference documentReferenceProduct;

    /*
    Contains these layout types.
     */
    private static final int MAIN_LAYOUT = 0;
    private static final int BOUGHT_TOGETHER_LAYOUT = 1;
    private static final int RECOMMENDED_LAYOUT = 2;
    private static final int SPONSORED_LAYOUT = 3;
    private static final int QUESTION_LAYOUT = 4;
    private static final int RANDOM_ITEM_LAYOUT = 5;
    private static final int RATING_LAYOUT = 6;

    public ProductRecycleViewAdapter(Context context, List<Product> products, String id, NavController navController, ProductRecycleViewAdapterClickInterface productRecycleViewAdapterClickInterface) {
        this.context = context;
        this.products = products;
        this.id = id;
        this.productRecycleViewAdapterClickInterface = productRecycleViewAdapterClickInterface;
        this.navController = navController;

        firebaseAnalytics=FirebaseAnalytics.getInstance(context);

        /*
        Analytics Setting user.
         */
        firebaseAnalytics.setUserId(firebaseAuth.getUid());

        documentReferenceProduct = collectionReferenceProduct.document(id);
        collectionReferenceQuestions=documentReferenceProduct.collection("questions");
        collectionReferenceRatings=documentReferenceProduct.collection("ratings");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;

        if (viewType == MAIN_LAYOUT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_main, parent, false);

            viewHolder = new MainLayoutViewHolder(view);
        }
        else if (viewType == BOUGHT_TOGETHER_LAYOUT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_bought_together, parent, false);

            viewHolder = new BoughtTogetherLayoutViewHolder(view);
        }
        else if (viewType == RECOMMENDED_LAYOUT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_recyclerview_row, parent, false);
            viewHolder = new RecommendedLayoutViewHolder(view);
        } else if (viewType == SPONSORED_LAYOUT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_recyclerview_row, parent, false);
            viewHolder = new SponsoredLayoutViewHolder(view);
        }
        else if (viewType == QUESTION_LAYOUT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_question, parent, false);
            viewHolder = new QuestionLayoutViewHolder(view);
        }
        else if (viewType == RANDOM_ITEM_LAYOUT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_recyclerview_row, parent, false);
            viewHolder = new RandomLayoutViewHolder(view);
        }
        else if (viewType == RATING_LAYOUT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rating, parent, false);
            viewHolder = new RatingLayoutViewHolder(view);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_recyclerview_row, parent, false);
            viewHolder = new SponsoredLayoutViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Product product = null;

        for (Product p : products) {
            if (p.getId().equals(id)) {
                product = p;
                break;
            } else {
                product = products.get(0);
            }
        }

        if (holder.getItemViewType() == MAIN_LAYOUT) {
            /*
            Main Layout
             */

            final MainLayoutViewHolder mainLayoutViewHolder = (MainLayoutViewHolder) holder;

            Glide.with(context)
                    .load(storage.getReferenceFromUrl(product.getImage_links().get(0)))
                    .transition(withCrossFade())
                    .fitCenter()
                    .error(R.drawable.error_loading)
                    .fallback(R.drawable.error_loading)
                    .into(mainLayoutViewHolder.imageViewImage);

            mainLayoutViewHolder.textName.setText(product.getName());

            //------------------------------------------------------------------------------------------------------------

            //Favourite

            Product finalProduct2 = product;
            collectionReferenceFavourite.document(product.getId()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.e("Product Favourite", "DocumentSnapshot data: " + document.getData());

                                    if (document.get("favourite").toString().equals("true")) {
                                        mainLayoutViewHolder.toggleButtonFavourite.setChecked(true);
                                    }
                                } else {
                                    Log.e("Product Favourite", "No such document");
                                    mainLayoutViewHolder.toggleButtonFavourite.setChecked(false);

                                    Map<String, Object> map = new HashMap<>();
                                    map.put("favourite", "false");

                                    collectionReferenceFavourite.document(finalProduct2.getId()).set(map)
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

            Product finalProduct1 = product;

            mainLayoutViewHolder.toggleButtonFavourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        /*
                        Analytics for add_to_favourite_event.
                         */
                        Bundle bundle=new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.CURRENCY,"LKR");
                        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY,finalProduct1.getProductType().toString());
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID,finalProduct1.getId());
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME,finalProduct1.getName());
                        bundle.putString(FirebaseAnalytics.Param.PRICE,String.valueOf(finalProduct1.getPrice()));

                        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_WISHLIST,bundle);

                        collectionReferenceFavourite.document(finalProduct1.getId()).update("favourite", "true")
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
                        collectionReferenceFavourite.document(finalProduct1.getId()).update("favourite", "false")
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

            //Favourite End
            //------------------------------------------------------------------------------------------------------------
            //Promotion

            final Promotion promotion=product.getPromotion();

            if (promotion==null){
                mainLayoutViewHolder.textViewPrice.setText(String.valueOf(product.getPrice()) + " LKR");
            }else {
                int price=product.getPrice()*(100-promotion.getDiscountPercentage())/100;

                mainLayoutViewHolder.textViewPrice.setText(String.valueOf(price)+" LKR");

                mainLayoutViewHolder.textViewPriceDiscount.setText(String.valueOf(product.getPrice())+" LKR");

                mainLayoutViewHolder.textViewPriceDiscount.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

                mainLayoutViewHolder.textViewPriceDiscount.setVisibility(View.VISIBLE);

                mainLayoutViewHolder.textViewPromotionBadge.setVisibility(View.VISIBLE);
            }

            //Promotion End
            //------------------------------------------------------------------------------------------------------------
            // AR

            ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(context);

            if (!availability.isSupported()) {
                mainLayoutViewHolder.buttonAR.setEnabled(false);
            }

            mainLayoutViewHolder.buttonAR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent sceneViewerIntent = new Intent(Intent.ACTION_VIEW);
                    sceneViewerIntent.setData(Uri.parse("https://raw.githubusercontent.com/jukemal/3D_Models/master/laptop.gltf?mode=ar_only"));
                    sceneViewerIntent.setPackage("com.google.ar.core");
                    context.startActivity(sceneViewerIntent);
                }
            });

            // Description

            if (product.getDescription() == null) {
                mainLayoutViewHolder.textViewDescription.setVisibility(View.GONE);
            } else {
                mainLayoutViewHolder.textViewDescription.setText(product.getDescription());
            }

            // Rating Bar

            mainLayoutViewHolder.ratingBar.setRating(product.getRating());

            //------------------------------------------------------------------------------------------------------------

            //Product Compare
            Product finalProduct = product;
            mainLayoutViewHolder.buttonCompareProducts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View popupView = LayoutInflater.from(context).inflate(R.layout.layout_product_compare_popup, null);
                    PopupWindow popupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
                    popupWindow.setAnimationStyle(R.style.Animation_Design_BottomSheetDialog);
                    popupView.setBackground(new ColorDrawable(context.getResources().getColor(R.color.colorPrimary)));
                    popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

                    ImageView imageViewImageProductCompare = popupView.findViewById(R.id.imageView_image_product_compare);

                    Glide.with(context)
                            .load(storage.getReferenceFromUrl(finalProduct.getImage_links().get(0)))
                            .transition(withCrossFade())
                            .fitCenter()
                            .error(R.drawable.error_loading)
                            .fallback(R.drawable.error_loading)
                            .into(imageViewImageProductCompare);

                    TextView textViewNameProductCompare = popupView.findViewById(R.id.product_name_product_compare);

                    textViewNameProductCompare.setText(finalProduct.getName());

                    FloatingActionButton buttonSelectProduct = popupView.findViewById(R.id.floating_action_button_add_product_product_compare);

                    buttonSelectProduct.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle bundle = new Bundle();
                            bundle.putString("id", finalProduct.getId());
                            navController.navigate(R.id.action_to_navigation_select_product_product_compare, bundle);

                            popupWindow.dismiss();
                        }
                    });
                }
            });

            //Product Compare End
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
                    if (Integer.parseInt(mainLayoutViewHolder.textViewCartCount.getText().toString()) >= 1) {
                        CartItem cartItem = CartItem.builder()
                                .ProductID(id)
                                .productReference(documentReferenceProduct)
                                .itemCount(Integer.parseInt(mainLayoutViewHolder.textViewCartCount.getText().toString()))
                                .build();

                        collectionReferenceCart.document(id).set(cartItem)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        /*
                                        Analytics for add_to_cart event.
                                         */
                                        Bundle bundle=new Bundle();
                                        bundle.putString(FirebaseAnalytics.Param.CURRENCY,"LKR");
                                        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY,finalProduct1.getProductType().toString());
                                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID,finalProduct1.getId());
                                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME,finalProduct1.getName());
                                        bundle.putString(FirebaseAnalytics.Param.PRICE,String.valueOf(finalProduct1.getPrice()));

                                        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART,bundle);

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
                            /*
                            Analytics for remove_from_cart event.
                             */
                            Bundle bundle=new Bundle();
                            bundle.putString(FirebaseAnalytics.Param.CURRENCY,"LKR");
                            bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY,finalProduct1.getProductType().toString());
                            bundle.putString(FirebaseAnalytics.Param.ITEM_ID,finalProduct1.getId());
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME,finalProduct1.getName());
                            bundle.putString(FirebaseAnalytics.Param.PRICE,String.valueOf(finalProduct1.getPrice()));

                            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.REMOVE_FROM_CART,bundle);

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

            // Cart End

            //----------------------------------------------------------------------------------------------------------
            // Store

            db.collection("users").document(finalProduct.getStoreId())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d("Product Store", "DocumentSnapshot data: " + document.getData());

                                    User user=document.toObject(User.class);

                                    mainLayoutViewHolder.chipStore.setText(user.getName().toUpperCase());

                                    mainLayoutViewHolder.chipStore.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Bundle bundle = new Bundle();
                                            bundle.putString("StoreId", finalProduct.getStoreId());
                                            navController.navigate(R.id.action_to_navigation_store, bundle);
                                        }
                                    });

                                    mainLayoutViewHolder.linearLayoutStore.setVisibility(View.VISIBLE);

                                    db.collection("users").document(finalProduct.getStoreId())
                                            .collection("visits")
                                            .document(finalProduct.getId()).get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            Log.d("Visit Update", "DocumentSnapshot data: " + document.getData());

                                                            Map<String,Object> map=new HashMap<>();
                                                            map.put("visitCount",Integer.parseInt(document.get("visitCount").toString()) +1);
                                                            map.put("timeStamp", Timestamp.now());

                                                            db.collection("users").document(finalProduct.getStoreId())
                                                                    .collection("visits")
                                                                    .document(finalProduct.getId()).set(map);
                                                        } else {
                                                            Log.d("Visit Update", "No such document");

                                                            Map<String,Object> map=new HashMap<>();
                                                            map.put("visitCount",1 );
                                                            map.put("timeStamp", Timestamp.now());

                                                            db.collection("users").document(finalProduct.getStoreId())
                                                                    .collection("visits")
                                                                    .document(finalProduct.getId()).set(map);

                                                        }
                                                    } else {
                                                        Log.d("Visit Update", "get failed with ", task.getException());
                                                    }
                                                }
                                            });
                                } else {
                                    Log.d("Product Store", "No such document");
                                }
                            } else {
                                Log.d("Product Store", "get failed with ", task.getException());
                            }
                        }
                    });


            // Store End
            //----------------------------------------------------------------------------------------------------------
            // Product Features

            mainLayoutViewHolder.recyclerViewProductFeatures.setHasFixedSize(true);
            mainLayoutViewHolder.recyclerViewProductFeatures.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

            ProductFeaturesRecyclerViewAdapter productFeaturesRecyclerViewAdapter = new ProductFeaturesRecyclerViewAdapter(context, product.getSpecification());
            mainLayoutViewHolder.recyclerViewProductFeatures.setAdapter(productFeaturesRecyclerViewAdapter);

            // Product Features End
            //----------------------------------------------------------------------------------------------------------

        }else if (holder.getItemViewType()==BOUGHT_TOGETHER_LAYOUT){
            /*
            Bought together layout
             */

            BoughtTogetherLayoutViewHolder boughtTogetherLayoutViewHolder=(BoughtTogetherLayoutViewHolder)holder;

            Glide.with(context)
                    .load(storage.getReferenceFromUrl(product.getImage_links().get(0)))
                    .transition(withCrossFade())
                    .fitCenter()
                    .error(R.drawable.error_loading)
                    .fallback(R.drawable.error_loading)
                    .into(boughtTogetherLayoutViewHolder.imageView1);

            boughtTogetherLayoutViewHolder.textViewName1.setText(product.getName());
            boughtTogetherLayoutViewHolder.textViewPrice1.setText(String.valueOf(product.getPrice())+" LKR");

            Random random=new Random();

            List<Product> randomProductList=new ArrayList<>();

            for (Product p:products){
                if (p.getId()!=product.getId()){
                    randomProductList.add(p);
                }
            }
            Product productRandom=randomProductList.get(random.nextInt(randomProductList.size()));

            Glide.with(context)
                    .load(storage.getReferenceFromUrl(productRandom.getImage_links().get(0)))
                    .transition(withCrossFade())
                    .fitCenter()
                    .error(R.drawable.error_loading)
                    .fallback(R.drawable.error_loading)
                    .into(boughtTogetherLayoutViewHolder.imageView2);

            boughtTogetherLayoutViewHolder.textViewName2.setText(productRandom.getName());
            boughtTogetherLayoutViewHolder.textViewPrice2.setText(String.valueOf(productRandom.getPrice())+" LKR");

            boughtTogetherLayoutViewHolder.linearLayoutProduct2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle=new Bundle();
                    bundle.putString("id", productRandom.getId());
                    Navigation.findNavController(view).navigate(R.id.action_to_navigation_product,bundle);
                }
            });

        }else if (holder.getItemViewType()==RECOMMENDED_LAYOUT){
            /*
            Layout for recommended products.
             */

            RecommendedLayoutViewHolder recommendedLayoutViewHolder = (RecommendedLayoutViewHolder) holder;

            recommendedLayoutViewHolder.textView.setText("Recommended Products");

            Product finalProduct3 = product;
            documentReferenceCurrentUser
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d("Product Recommendation", "DocumentSnapshot data: " + document.getData());

                                    User user=document.toObject(User.class);

                                    if (user.getRecommendationList()!=null){
                                        List<String> recommendationList=user.getRecommendationList();

                                        List<Product> recommendedProductList=new ArrayList<>();

                                        for (String s:recommendationList){
                                            for (Product p:products){
                                                if (s.equals(p.getId())&&!s.equals(finalProduct3.getId())){
                                                    recommendedProductList.add(p);
                                                }
                                            }
                                        }

                                        HomeRowRecycleViewAdapter homeRowRecycleViewAdapter = new HomeRowRecycleViewAdapter(context, recommendedProductList);

                                        recommendedLayoutViewHolder.recyclerView.setHasFixedSize(true);
                                        recommendedLayoutViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                                        recommendedLayoutViewHolder.recyclerView.setAdapter(homeRowRecycleViewAdapter);

                                        recommendedLayoutViewHolder.button.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Bundle bundle=new Bundle();
                                                bundle.putString("header","Recommended Products");
                                                bundle.putSerializable("productList", (Serializable) products);
                                                navController.navigate(R.id.action_to_navigation_generic_product_ist,bundle);
                                            }
                                        });
                                    }else {
                                        recommendedLayoutViewHolder.itemView.setVisibility(View.GONE);
                                    }
                                } else {
                                    Log.d("Product Recommendation", "No such document");
                                }
                            } else {
                                Log.d("Product Recommendation", "get failed with ", task.getException());
                            }
                        }
                    });
        }else if(holder.getItemViewType()==SPONSORED_LAYOUT){
            /*
            Layout for Sponsored products.
             */

            SponsoredLayoutViewHolder sponsoredLayoutViewHolder = (SponsoredLayoutViewHolder) holder;

            sponsoredLayoutViewHolder.textView.setText("Sponsored Products");

            Collections.shuffle(products);

            HomeRowRecycleViewAdapter homeRowRecycleViewAdapter = new HomeRowRecycleViewAdapter(context, products);

            sponsoredLayoutViewHolder.recyclerView.setHasFixedSize(true);
            sponsoredLayoutViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            sponsoredLayoutViewHolder.recyclerView.setAdapter(homeRowRecycleViewAdapter);

            sponsoredLayoutViewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle=new Bundle();
                    bundle.putString("header","Sponsored Products");
                    bundle.putSerializable("productList", (Serializable) products);
                    navController.navigate(R.id.action_to_navigation_generic_product_ist,bundle);
                }
            });
        }else if (holder.getItemViewType()==QUESTION_LAYOUT){
            /*
            Layout for Questions.
             */

            QuestionLayoutViewHolder questionLayoutViewHolder=(QuestionLayoutViewHolder)holder;

            questionLayoutViewHolder.recyclerView.setHasFixedSize(true);
            questionLayoutViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

            collectionReferenceQuestions.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            List<Question> questionList=new ArrayList<>();

                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.e("Question", document.getId() + " => " + document.getData());

                                    Question question=document.toObject(Question.class);
                                    questionList.add(question);
                                }

                                QuestionRecyclerViewAdapter questionRecyclerViewAdapter=new QuestionRecyclerViewAdapter(context,questionList,id);
                                questionLayoutViewHolder.recyclerView.setAdapter(questionRecyclerViewAdapter);
                            } else {
                                Log.e("Question", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }else if (holder.getItemViewType()==RANDOM_ITEM_LAYOUT){
            /*
            Layout for Random products.
             */

            RandomLayoutViewHolder randomLayoutViewHolder = (RandomLayoutViewHolder) holder;

            randomLayoutViewHolder.textView.setText("Random Products");

            Collections.shuffle(products);

            HomeRowRecycleViewAdapter homeRowRecycleViewAdapter = new HomeRowRecycleViewAdapter(context, products);

            randomLayoutViewHolder.recyclerView.setHasFixedSize(true);
            randomLayoutViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            randomLayoutViewHolder.recyclerView.setAdapter(homeRowRecycleViewAdapter);

            randomLayoutViewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle=new Bundle();
                    bundle.putString("header","Sponsored Products");
                    bundle.putSerializable("productList", (Serializable) products);
                    navController.navigate(R.id.action_to_navigation_generic_product_ist,bundle);
                }
            });
        }else if (holder.getItemViewType()==RATING_LAYOUT){
            /*
            Layout for Ratings.
             */

            RatingLayoutViewHolder ratingLayoutViewHolder=(RatingLayoutViewHolder) holder;

            ratingLayoutViewHolder.recyclerView.setHasFixedSize(true);
            ratingLayoutViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

            collectionReferenceRatings.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            List<Rating> ratingList=new ArrayList<>();

                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.e("Rating", document.getId() + " => " + document.getData());

                                    Rating rating=document.toObject(Rating.class);
                                    ratingList.add(rating);
                                }

                                RatingRecyclerViewAdapter ratingRecyclerViewAdapter=new RatingRecyclerViewAdapter(context,ratingList,id);
                                ratingLayoutViewHolder.recyclerView.setAdapter(ratingRecyclerViewAdapter);
                            } else {
                                Log.e("Rating", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }else {
            /*
            Layout for sponsored products.
             */

            SponsoredLayoutViewHolder sponsoredLayoutViewHolder = (SponsoredLayoutViewHolder) holder;

            sponsoredLayoutViewHolder.textView.setText("Sponsored Products");

            Collections.shuffle(products);

            HomeRowRecycleViewAdapter homeRowRecycleViewAdapter = new HomeRowRecycleViewAdapter(context, products);

            sponsoredLayoutViewHolder.recyclerView.setHasFixedSize(true);
            sponsoredLayoutViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            sponsoredLayoutViewHolder.recyclerView.setAdapter(homeRowRecycleViewAdapter);

            sponsoredLayoutViewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle=new Bundle();
                    bundle.putString("header","Sponsored Products");
                    bundle.putSerializable("productList", (Serializable) products);
                    navController.navigate(R.id.action_to_navigation_generic_product_ist,bundle);
                }
            });
        }
    }

    /*
    Returns layout type based on position.
     */
    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return MAIN_LAYOUT;
        else if (position == 1)
            return BOUGHT_TOGETHER_LAYOUT;
        else if (position == 2)
            return RECOMMENDED_LAYOUT;
        else if (position == 3)
            return SPONSORED_LAYOUT;
        else if (position == 4)
            return QUESTION_LAYOUT;
        else if (position == 5)
            return RANDOM_ITEM_LAYOUT;
        else if (position == 6)
            return RATING_LAYOUT;
        else return SPONSORED_LAYOUT;
    }

    @Override
    public int getItemCount() {
        return 7;
    }

    /*
    Inner class for main layout.
     */
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
        RecyclerView recyclerViewProductFeatures;
        Button buttonCompareProducts;
        Chip chipStore;
        LinearLayout linearLayoutStore;
        TextView textViewPromotionBadge;
        TextView textViewPriceDiscount;

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
            recyclerViewProductFeatures = itemView.findViewById(R.id.product_features_recyclerView);
            buttonCompareProducts = itemView.findViewById(R.id.btn_compare_product);
            chipStore=itemView.findViewById(R.id.store_name_product);
            linearLayoutStore=itemView.findViewById(R.id.Linear_layout_available_stores);
            textViewPromotionBadge=itemView.findViewById(R.id.promotion_badge);
            textViewPriceDiscount=itemView.findViewById(R.id.textPriceDiscount);
        }
    }

    /*
    Inner class for bought together layout.
     */
    public class BoughtTogetherLayoutViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linearLayoutProduct1;
        ImageView imageView1;
        TextView textViewName1;
        TextView textViewPrice1;

        LinearLayout linearLayoutProduct2;
        ImageView imageView2;
        TextView textViewName2;
        TextView textViewPrice2;

        public BoughtTogetherLayoutViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayoutProduct1=itemView.findViewById(R.id.product1_bought_together);
            imageView1 = itemView.findViewById(R.id.imageView_image1_bought_together);
            textViewName1 = itemView.findViewById(R.id.product_name1_bought_together);
            textViewPrice1 = itemView.findViewById(R.id.product_price1_bought_together);

            linearLayoutProduct2=itemView.findViewById(R.id.product2_bought_together);
            imageView2 = itemView.findViewById(R.id.imageView_image2_bought_together);
            textViewName2 = itemView.findViewById(R.id.product_name2_bought_together);
            textViewPrice2 = itemView.findViewById(R.id.product_price2_bought_together);
        }
    }

    /*
    Inner class for recommended layout.
     */
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

    /*
    Inner class for sponsored layout.
     */
    public class SponsoredLayoutViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView textView;
        Button button;

        public SponsoredLayoutViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerView = itemView.findViewById(R.id.recyclerview_home_row);
            textView = itemView.findViewById(R.id.recyclerview_home_row_title);
            button = itemView.findViewById(R.id.btnMore);
        }
    }

    /*
    Inner class for question layout.
     */
    public class QuestionLayoutViewHolder extends RecyclerView.ViewHolder{

        RecyclerView recyclerView;

        public QuestionLayoutViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerView=itemView.findViewById(R.id.recyclerview_question);
        }
    }

    /*
    Inner class for random product layout.
     */
    public class RandomLayoutViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView textView;
        Button button;

        public RandomLayoutViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerView = itemView.findViewById(R.id.recyclerview_home_row);
            textView = itemView.findViewById(R.id.recyclerview_home_row_title);
            button = itemView.findViewById(R.id.btnMore);
        }
    }

    /*
    Inner class for ratings layout.
     */
    public class RatingLayoutViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;

        public RatingLayoutViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerView = itemView.findViewById(R.id.recyclerview_rating);
        }
    }
}
