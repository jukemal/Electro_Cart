package com.electro.electro_cart.ui.tracking;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.electro.electro_cart.R;
import com.electro.electro_cart.utils.EnumOrderTrackingStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class TrackingFragment extends Fragment {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final CollectionReference collectionReferenceOrderTracking = db.collection("users")
            .document(firebaseAuth.getCurrentUser().getUid())
            .collection("Order_Tracking");

    private final DocumentReference documentReferenceUser=db.collection("users")
            .document(firebaseAuth.getCurrentUser().getUid());

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tracking, container, false);

        ProgressBar progressBar=root.findViewById(R.id.progressBar);

        ConstraintLayout constraintLayoutMain=root.findViewById(R.id.constraintLayout_main_order_tracking);
        ConstraintLayout constraintLayoutInner=root.findViewById(R.id.constraintLayout_inner_order_tracking);

        FloatingActionButton floatingActionButton1 = root.findViewById(R.id.fab_1_tracking_fragment);
        TextView textView1 = root.findViewById(R.id.text_1_tracking_fragment);

        FloatingActionButton floatingActionButton2 = root.findViewById(R.id.fab_2_tracking_fragment);
        TextView textView2 = root.findViewById(R.id.text_2_tracking_fragment);

        FloatingActionButton floatingActionButton3 = root.findViewById(R.id.fab_3_tracking_fragment);
        TextView textView3 = root.findViewById(R.id.text_3_tracking_fragment);

        FloatingActionButton floatingActionButton4 = root.findViewById(R.id.fab_4_tracking_fragment);
        TextView textView4 = root.findViewById(R.id.text_4_tracking_fragment);

        floatingActionButton1.setEnabled(false);
        floatingActionButton2.setEnabled(false);
        floatingActionButton3.setEnabled(false);
        floatingActionButton4.setEnabled(false);

        final String[] orderTrackingId = {null};

        collectionReferenceOrderTracking.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                progressBar.setVisibility(View.GONE);
                                constraintLayoutInner.setVisibility(View.VISIBLE);
                            } else {
                                String orderStatus=null;

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    orderTrackingId[0] =document.getId();
                                    orderStatus =document.get("orderStatus").toString();
                                    break;
                                }

                                if (orderStatus.equals(EnumOrderTrackingStatus.ORDER_ACCEPTED.toString())){
                                    floatingActionButton1.setImageDrawable(root.getResources().getDrawable(R.drawable.ic_done_black_24dp));
                                    floatingActionButton2.setEnabled(true);
                                }else if (orderStatus.equals(EnumOrderTrackingStatus.ORDER_PACKAGED.toString())){
                                    floatingActionButton1.setImageDrawable(root.getResources().getDrawable(R.drawable.ic_done_black_24dp));
                                    floatingActionButton2.setImageDrawable(root.getResources().getDrawable(R.drawable.ic_done_black_24dp));
                                    floatingActionButton3.setEnabled(true);
                                    textView2.setEnabled(true);
                                }else if (orderStatus.equals(EnumOrderTrackingStatus.ON_THE_WAY.toString())){
                                    floatingActionButton1.setImageDrawable(root.getResources().getDrawable(R.drawable.ic_done_black_24dp));
                                    floatingActionButton2.setImageDrawable(root.getResources().getDrawable(R.drawable.ic_done_black_24dp));
                                    floatingActionButton3.setImageDrawable(root.getResources().getDrawable(R.drawable.ic_done_black_24dp));
                                    floatingActionButton4.setEnabled(true);
                                    textView2.setEnabled(true);
                                    textView3.setEnabled(true);
                                }else {
                                    floatingActionButton1.setEnabled(true);
                                }

                                progressBar.setVisibility(View.GONE);
                                constraintLayoutMain.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Log.e("Order Tracking", "Error getting documents: ", task.getException());
                            progressBar.setVisibility(View.GONE);
                            constraintLayoutInner.setVisibility(View.VISIBLE);
                        }
                    }
                });

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingActionButton1.setImageDrawable(root.getResources().getDrawable(R.drawable.ic_done_black_24dp));
                floatingActionButton1.setEnabled(false);
                floatingActionButton2.setEnabled(true);
            }
        });

        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collectionReferenceOrderTracking.document(orderTrackingId[0])
                        .update("orderStatus",EnumOrderTrackingStatus.ORDER_PACKAGED)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Order Tracking", "DocumentSnapshot successfully updated!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Order Tracking", "Error updating document", e);
                            }
                        });

                floatingActionButton2.setImageDrawable(root.getResources().getDrawable(R.drawable.ic_done_black_24dp));
                textView2.setEnabled(true);
                floatingActionButton1.setEnabled(false);
                floatingActionButton2.setEnabled(false);
                floatingActionButton3.setEnabled(true);
            }
        });

        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collectionReferenceOrderTracking.document(orderTrackingId[0])
                        .update("orderStatus",EnumOrderTrackingStatus.ON_THE_WAY)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Order Tracking", "DocumentSnapshot successfully updated!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Order Tracking", "Error updating document", e);
                            }
                        });

                floatingActionButton3.setImageDrawable(root.getResources().getDrawable(R.drawable.ic_done_black_24dp));
                textView3.setEnabled(true);
                floatingActionButton1.setEnabled(false);
                floatingActionButton2.setEnabled(false);
                floatingActionButton3.setEnabled(false);
                floatingActionButton4.setEnabled(true);
            }
        });

        floatingActionButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collectionReferenceOrderTracking.document(orderTrackingId[0])
                        .update("orderStatus",EnumOrderTrackingStatus.ARRIVED)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Order Tracking", "DocumentSnapshot successfully updated!");

                                collectionReferenceOrderTracking.document(orderTrackingId[0]).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("Order Tracking", "DocumentSnapshot successfully deleted!");

                                                documentReferenceUser.get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    DocumentSnapshot document = task.getResult();
                                                                    if (document.exists()) {
                                                                        Log.d("Order Tracking", "DocumentSnapshot data: " + document.getData());

                                                                        documentReferenceUser.update("points",String.valueOf(Integer.parseInt(document.get("points").toString())+50))
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Log.d("Order Tracking", "Successfully Updated.");
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Log.d("Order Tracking", "get failed with ", e);
                                                                            }
                                                                        });
                                                                    } else {
                                                                        Log.d("Order Tracking", "No such document");
                                                                    }
                                                                } else {
                                                                    Log.d("Order Tracking", "get failed with ", task.getException());
                                                                }
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("Order Tracking", "Error deleting document", e);
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Order Tracking", "Error updating document", e);
                            }
                        });

                floatingActionButton4.setImageDrawable(root.getResources().getDrawable(R.drawable.ic_done_black_24dp));
                floatingActionButton1.setEnabled(false);
                floatingActionButton2.setEnabled(false);
                floatingActionButton3.setEnabled(false);
                floatingActionButton4.setEnabled(false);
                textView4.setEnabled(true);

                View popupView = LayoutInflater.from(getContext()).inflate(R.layout.layout_order_tracking_popup, null);
                PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                popupWindow.setAnimationStyle(R.style.Animation_Design_BottomSheetDialog);
                popupView.setBackground(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

                Button buttonPopupOk=popupView.findViewById(R.id.btn_ok_order_tracking_popup);

                buttonPopupOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                    }
                });


            }
        });

        return root;
    }
}