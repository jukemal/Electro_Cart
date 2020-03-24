package com.electro.electro_cart.ViewAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.electro.electro_cart.R;
import com.electro.electro_cart.models.Product;
import com.electro.electro_cart.models.Rating;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class RatingStoreRecyclerViewAdapter extends RecyclerView.Adapter<RatingStoreRecyclerViewAdapter.RatingStoreRecyclerViewHolder> {

    private Context context;
    private List<Product> productList;

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final CollectionReference collectionReferenceProduct = db.collection("products");

    public RatingStoreRecyclerViewAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }


    @NonNull
    @Override
    public RatingStoreRecyclerViewAdapter.RatingStoreRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_store_question,parent,false);

        return new RatingStoreRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingStoreRecyclerViewAdapter.RatingStoreRecyclerViewHolder holder, int position) {
        final Product product=productList.get(position);

        Glide.with(context)
                .load(storage.getReferenceFromUrl(product.getImage_links().get(0)))
                .transition(withCrossFade())
                .fitCenter()
                .error(R.drawable.error_loading)
                .fallback(R.drawable.error_loading)
                .into(holder.imageView);

        holder.textViewName.setText(product.getName());

        holder.textViewPrice.setText(String.valueOf(product.getPrice())+" LKR");

        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        collectionReferenceProduct.document(product.getId()).collection("ratings")
                .get()
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

                            RatingRecyclerViewAdapter ratingRecyclerViewAdapter=new RatingRecyclerViewAdapter(context,ratingList,product.getId());
                            holder.recyclerView.setAdapter(ratingRecyclerViewAdapter);
                        } else {
                            Log.e("Rating", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class RatingStoreRecyclerViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textViewName;
        TextView textViewPrice;
        RecyclerView recyclerView;

        public RatingStoreRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imgThumb_txt_name_question_store);
            textViewName = itemView.findViewById(R.id.txt_name_question_store);
            textViewPrice = itemView.findViewById(R.id.txt_price_question_store);
            recyclerView = itemView.findViewById(R.id.recyclerView_question_store);
        }
    }
}
