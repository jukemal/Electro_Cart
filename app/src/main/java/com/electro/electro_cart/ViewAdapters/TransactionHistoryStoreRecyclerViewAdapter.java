package com.electro.electro_cart.ViewAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.electro.electro_cart.R;
import com.electro.electro_cart.models.OrderHistory;
import com.electro.electro_cart.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/*
Recyclerview for Transaction History Interface.
 */
public class TransactionHistoryStoreRecyclerViewAdapter extends RecyclerView.Adapter<TransactionHistoryStoreRecyclerViewAdapter.TransactionHistoryStoreRecyclerViewHolder> {

    private Context context;
    private List<User> userList;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public TransactionHistoryStoreRecyclerViewAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }


    @NonNull
    @Override
    public TransactionHistoryStoreRecyclerViewAdapter.TransactionHistoryStoreRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_store_transaction_history, parent, false);

        return new TransactionHistoryStoreRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionHistoryStoreRecyclerViewAdapter.TransactionHistoryStoreRecyclerViewHolder holder, int position) {
        User user = userList.get(position);

        holder.textView.setText(user.getName());

        final CollectionReference collectionReferenceOrderHistory = db.collection("users")
                .document(user.getId())
                .collection("Order_History");

        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));

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

                            OrderHistoryRecyclerViewAdapter orderHistoryRecyclerViewAdapter=new OrderHistoryRecyclerViewAdapter(context,orderHistoryList);
                            holder.recyclerView.setAdapter(orderHistoryRecyclerViewAdapter);

                            Log.e("History",orderHistoryList.toString());
                        }else {
                            holder.recyclerView.setVisibility(View.GONE);
                            Toast.makeText(context, "Failed to load products. Check your internet connection.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class TransactionHistoryStoreRecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        RecyclerView recyclerView;

        public TransactionHistoryStoreRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.txt_user_name_history_store);
            recyclerView = itemView.findViewById(R.id.recyclerView_history_store);
        }
    }
}
