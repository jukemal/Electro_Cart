package com.electro.electro_cart.ViewAdapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.electro.electro_cart.R;
import com.electro.electro_cart.models.CartItem;
import com.electro.electro_cart.models.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class CartRecyclerViewAdapter extends RecyclerView.Adapter<CartRecyclerViewAdapter.CartRecyclerViewHolder> {

    Context context;
    List<CartItem> cartItemList;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final CollectionReference collectionReferenceProduct = db.collection("products");

    public CartRecyclerViewAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public CartRecyclerViewAdapter.CartRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cart_list, parent, false);

        return new CartRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartRecyclerViewAdapter.CartRecyclerViewHolder holder, int position) {
        final CartItem cartItem=cartItemList.get(position);

        collectionReferenceProduct.document(cartItem.getProductID())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Product product =documentSnapshot.toObject(Product.class);

                        Glide.with(context)
                                .load("https://fdn2.gsmarena.com/vv/bigpic/samsung-galaxy-s20-ultra-.jpg")
                                .transition(withCrossFade())
                                .fitCenter()
                                .placeholder(R.drawable.loading)
                                .error(R.drawable.error_loading)
                                .fallback(R.drawable.error_loading)
                                .into(holder.imageView);

                        holder.textViewName.setText(product.getName());

                        holder.textViewPrice.setText(String.valueOf(product.getPrice()));

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bundle bundle=new Bundle();
                                bundle.putString("id",product.getId());
                                Navigation.findNavController(view).navigate(R.id.action_to_navigation_product, bundle);

                            }
                        });
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

    public class CartRecyclerViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textViewName;
        TextView textViewPrice;

        public CartRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.imgThumb_item_cart);
            textViewName=itemView.findViewById(R.id.txt_name_item_cart);
            textViewPrice=itemView.findViewById(R.id.txt_price_item_cart);
        }
    }
}
