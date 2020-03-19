package com.electro.electro_cart.ViewAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.electro.electro_cart.R;
import com.electro.electro_cart.models.CartItem;
import com.electro.electro_cart.models.OrderHistory;
import com.electro.electro_cart.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryRecyclerViewAdapter extends RecyclerView.Adapter<OrderHistoryRecyclerViewAdapter.OrderHistoryRecyclerViewHolder> {

    Context context;
    List<OrderHistory> orderHistoryList;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final CollectionReference collectionReferenceProduct = db.collection("products");

    public OrderHistoryRecyclerViewAdapter(Context context, List<OrderHistory> orderHistoryList) {
        this.context = context;
        this.orderHistoryList = orderHistoryList;
    }

    @NonNull
    @Override
    public OrderHistoryRecyclerViewAdapter.OrderHistoryRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order_history,parent,false);

        return new OrderHistoryRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHistoryRecyclerViewAdapter.OrderHistoryRecyclerViewHolder holder, int position) {
        final OrderHistory orderHistory=orderHistoryList.get(position);

        holder.textViewDate.setText(orderHistory.getTimestamp().toString());

        for (CartItem c:orderHistory.getCartItemList()){
            collectionReferenceProduct.document(c.getProductID()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();

                                if (document.exists()){
                                    Product product=document.toObject(Product.class);
                                    holder.textViewTotal.setText(String.valueOf(Double.parseDouble(holder.textViewTotal.getText().toString())+((double)(product.getPrice()*c.getItemCount()))));
                                }else {
                                    Log.d("Order History", "No such document");
                                }
                            }else {
                                Log.d("Order History", "get failed with ", task.getException());
                            }
                        }
                    });
        }

        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        OrderHistoryRecyclerViewAdapterInner orderHistoryRecyclerViewAdapterInner=new OrderHistoryRecyclerViewAdapterInner(context,orderHistory.getCartItemList());
        holder.recyclerView.setAdapter(orderHistoryRecyclerViewAdapterInner);
    }

    @Override
    public int getItemCount() {
        return orderHistoryList.size();
    }

    public class OrderHistoryRecyclerViewHolder extends RecyclerView.ViewHolder{

        TextView textViewTotal;
        TextView textViewDate;
        RecyclerView recyclerView;

        public OrderHistoryRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTotal=itemView.findViewById(R.id.txt_total_order_history);
            textViewDate=itemView.findViewById(R.id.txt_date_order_history);
            recyclerView=itemView.findViewById(R.id.recyclerview_order_history);
        }
    }
}

class OrderHistoryRecyclerViewAdapterInner extends RecyclerView.Adapter<OrderHistoryRecyclerViewAdapterInner.OrderHistoryRecyclerViewHolderInner>{


    FirebaseStorage storage = FirebaseStorage.getInstance();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final CollectionReference collectionReferenceProduct = db.collection("products");


    Context context;
    List<CartItem> cartItemList;

    public OrderHistoryRecyclerViewAdapterInner(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public OrderHistoryRecyclerViewAdapterInner.OrderHistoryRecyclerViewHolderInner onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order_history_nested, parent, false);

        return new OrderHistoryRecyclerViewHolderInner(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHistoryRecyclerViewAdapterInner.OrderHistoryRecyclerViewHolderInner holder, int position) {
        final CartItem cartItem=cartItemList.get(position);

        collectionReferenceProduct.document(cartItem.getProductID())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Product product =documentSnapshot.toObject(Product.class);

                        Glide.with(context)
                                .load(storage.getReferenceFromUrl(product.getImage_links().get(0)))
                                .transition(withCrossFade())
                                .fitCenter()
                                .error(R.drawable.error_loading)
                                .fallback(R.drawable.error_loading)
                                .into(holder.imageView);

                        holder.textViewName.setText(product.getName());

                        holder.textViewPrice.setText(String.valueOf(product.getPrice()));

                        holder.textViewCount.setText("X "+String.valueOf(cartItem.getItemCount()));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"ID not Found",Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    class OrderHistoryRecyclerViewHolderInner extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textViewName;
        TextView textViewPrice;
        TextView textViewCount;

         OrderHistoryRecyclerViewHolderInner(@NonNull View itemView) {
            super(itemView);

             imageView=itemView.findViewById(R.id.imgThumb_item_order_history_nested);
             textViewName=itemView.findViewById(R.id.txt_name_item_order_history_nested);
             textViewPrice=itemView.findViewById(R.id.txt_price_item_order_history_nested);
             textViewCount=itemView.findViewById(R.id.txt_count_item_order_history_nested);
        }
    }
}
