package com.electro.electro_cart.ui.orderHistory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.electro.electro_cart.R;
import com.electro.electro_cart.ViewAdapters.OrderHistoryRecyclerViewAdapter;
import com.electro.electro_cart.models.OrderHistory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrderHistoryRecyclerViewAdapter orderHistoryRecyclerViewAdapter;

    ProgressBar progressBar;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final CollectionReference collectionReferenceProduct = db.collection("products");

    private final CollectionReference collectionReferenceOrderHistory = db.collection("users")
            .document(firebaseAuth.getCurrentUser().getUid())
            .collection("Order_History");

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_order_history, container, false);

        progressBar=root.findViewById(R.id.progressBar_order_history_fragment);

        recyclerView=root.findViewById(R.id.recyclerView_order_history_fragment);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        collectionReferenceOrderHistory.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<OrderHistory> orderHistoryList=new ArrayList<>();

                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                OrderHistory orderHistory=document.toObject(OrderHistory.class);
                                orderHistoryList.add(orderHistory);
                            }

                            orderHistoryRecyclerViewAdapter=new OrderHistoryRecyclerViewAdapter(getContext(),orderHistoryList);
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setAdapter(orderHistoryRecyclerViewAdapter);
                        }else {
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Failed to load products. Check your internet connection.", Toast.LENGTH_LONG);
                        }
                    }
                });

        return root;
    }
}