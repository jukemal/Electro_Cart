package com.electro.electro_cart.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.electro.electro_cart.R;
import com.electro.electro_cart.ViewAdapters.CartRecyclerViewAdapter;
import com.electro.electro_cart.models.CartItem;
import com.electro.electro_cart.models.OrderHistory;
import com.electro.electro_cart.models.Product;
import com.electro.electro_cart.utils.EnumOrderTrackingStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartRecyclerViewAdapter cartRecyclerViewAdapter;

    private FirebaseAnalytics firebaseAnalytics;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final CollectionReference collectionReferenceProduct = db.collection("products");

    private final CollectionReference collectionReference = db.collection("users")
            .document(firebaseAuth.getCurrentUser().getUid())
            .collection("cart");

    private final CollectionReference collectionReferenceOrderTracking = db.collection("users")
            .document(firebaseAuth.getCurrentUser().getUid())
            .collection("Order_Tracking");

    private final CollectionReference collectionReferenceOrderHistory = db.collection("users")
            .document(firebaseAuth.getCurrentUser().getUid())
            .collection("Order_History");

    private double total = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cart, container, false);

        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        firebaseAnalytics.setUserId(firebaseAuth.getUid());

        Calendar calendar=Calendar.getInstance();

        Bundle bundle=new Bundle();
        bundle.putString("time",calendar.getTime().toString());

        firebaseAnalytics.logEvent("view_cart",bundle);

        ProgressBar progressBar = root.findViewById(R.id.progressBar_cart);

        ConstraintLayout constraintLayoutInner = root.findViewById(R.id.innerRecycleView_cart);

        ConstraintLayout constraintLayoutTotalCart = root.findViewById(R.id.constraintLayout_total_cart);

        TextView textViewSubTotal = root.findViewById(R.id.text_sub_total_cart);
        TextView textViewDeliveryFee = root.findViewById(R.id.text_delivery_fee_cart);
        TextView textViewTotal = root.findViewById(R.id.text_total_cart);
        Button buttonProceed = root.findViewById(R.id.btn_proceed_cart);

        recyclerView = root.findViewById(R.id.recyclerview_cart);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        collectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<CartItem> cartItemList = new ArrayList<>();

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot item : task.getResult()) {
                                CartItem c = item.toObject(CartItem.class);
                                cartItemList.add(c);
                            }

                            cartRecyclerViewAdapter = new CartRecyclerViewAdapter(getContext(), cartItemList);
                            recyclerView.setAdapter(cartRecyclerViewAdapter);

                            if (cartItemList.isEmpty()) {
                                progressBar.setVisibility(View.GONE);
                                constraintLayoutInner.setVisibility(View.VISIBLE);
                            } else {

                                total = 0;

                                for (CartItem c : cartItemList) {
                                    collectionReferenceProduct.document(c.getProductID())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot documentSnapshot = task.getResult();

                                                        if (documentSnapshot.exists()) {
                                                            Product product = documentSnapshot.toObject(Product.class);
                                                            Log.e("pro", product.toString());
                                                            total += product.getPrice() * c.getItemCount();
                                                            Log.e("total", String.valueOf(total));

                                                            textViewSubTotal.setText(String.valueOf(total) + " LKR");
                                                            textViewDeliveryFee.setText(String.valueOf(total * 8 / 100) + " LKR");
                                                            textViewTotal.setText(String.valueOf(total * 108 / 100) + " LKR");
                                                        }
                                                    }
                                                }
                                            });
                                }

                                constraintLayoutTotalCart.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);


                            }
                        } else {
                            Toast.makeText(getContext(), "Failed to load Cart.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        buttonProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonProceed.setEnabled(false);

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());

                builder.setMessage("Do you want to proceed with the purchase?")
                        .setTitle("Cart")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Bundle bundle=new Bundle();
                                bundle.putString(FirebaseAnalytics.Param.CURRENCY,"LKR");
                                bundle.putString(FirebaseAnalytics.Param.PRICE,String.valueOf(total));

                                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE,bundle);

                                Bundle bundlePoints=new Bundle();
                                bundlePoints.putString(FirebaseAnalytics.Param.VIRTUAL_CURRENCY_NAME,"Points");
                                bundlePoints.putString(FirebaseAnalytics.Param.VALUE,"50");

                                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.EARN_VIRTUAL_CURRENCY,bundlePoints);

                                Map<String, Object> data = new HashMap<>();
                                data.put("orderStatus", EnumOrderTrackingStatus.ORDER_ACCEPTED);

                                collectionReferenceOrderTracking.add(data)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                collectionReference.get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                List<CartItem> cartItemList = new ArrayList<>();

                                                                if (task.isSuccessful()) {
                                                                    for (QueryDocumentSnapshot item : task.getResult()) {
                                                                        CartItem c = item.toObject(CartItem.class);
                                                                        cartItemList.add(c);
                                                                    }

                                                                    OrderHistory orderHistory = OrderHistory.builder()
                                                                            .cartItemList(cartItemList).build();

                                                                    if (!cartItemList.isEmpty()) {
                                                                        collectionReferenceOrderHistory.add(orderHistory)
                                                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                                    @Override
                                                                                    public void onSuccess(DocumentReference documentReference) {
                                                                                        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                if (task.isSuccessful()) {
                                                                                                    for (QueryDocumentSnapshot item : task.getResult()) {
                                                                                                        CartItem c = item.toObject(CartItem.class);

                                                                                                        collectionReference.document(c.getProductID()).delete()
                                                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                    @Override
                                                                                                                    public void onSuccess(Void aVoid) {
                                                                                                                        Navigation.findNavController(container).navigate(R.id.navigation_tracking);
                                                                                                                    }
                                                                                                                })
                                                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                                                    @Override
                                                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                                                        Toast.makeText(getActivity(), "Failed to Proceed. Try again.", Toast.LENGTH_SHORT).show();
                                                                                                                        buttonProceed.setEnabled(true);
                                                                                                                    }
                                                                                                                });
                                                                                                    }
                                                                                                } else {
                                                                                                    Toast.makeText(getActivity(), "Failed to Proceed. Try again.", Toast.LENGTH_SHORT).show();
                                                                                                    buttonProceed.setEnabled(true);
                                                                                                }
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                })
                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        Toast.makeText(getActivity(), "Failed to Proceed. Try again.", Toast.LENGTH_SHORT).show();
                                                                                        buttonProceed.setEnabled(true);
                                                                                    }
                                                                                });
                                                                    }
                                                                } else {
                                                                    Toast.makeText(getActivity(), "Failed to Proceed. Try again.", Toast.LENGTH_SHORT).show();
                                                                    buttonProceed.setEnabled(true);
                                                                }
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getActivity(), "Failed to Proceed. Try again.", Toast.LENGTH_SHORT).show();
                                                buttonProceed.setEnabled(true);
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                buttonProceed.setEnabled(true);
                            }
                        });

                builder.show();
            }
        });

        return root;
    }
}
