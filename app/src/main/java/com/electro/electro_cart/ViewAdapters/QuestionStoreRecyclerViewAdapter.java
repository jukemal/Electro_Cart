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
import com.electro.electro_cart.models.Question;
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

/*
Recyclerview for Store Questions interface.

Displays questions for each products in a list.
 */
public class QuestionStoreRecyclerViewAdapter extends RecyclerView.Adapter<QuestionStoreRecyclerViewAdapter.QuestionStoreRecyclerViewHolder> {

    private Context context;
    private List<Product> productList;

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final CollectionReference collectionReferenceProduct = db.collection("products");

    public QuestionStoreRecyclerViewAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public QuestionStoreRecyclerViewAdapter.QuestionStoreRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_store_question,parent,false);

        return new QuestionStoreRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionStoreRecyclerViewAdapter.QuestionStoreRecyclerViewHolder holder, int position) {
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

        /*
        Questions for given product is fetched and displayed in a list.
         */
        collectionReferenceProduct.document(product.getId()).collection("questions")
                .get()
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

                            QuestionRecyclerViewAdapter questionRecyclerViewAdapter=new QuestionRecyclerViewAdapter(context,questionList,product.getId());
                            holder.recyclerView.setAdapter(questionRecyclerViewAdapter);
                        } else {
                            Log.e("Question", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    /*
    Innerclass for the layout.
     */
    public class QuestionStoreRecyclerViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textViewName;
        TextView textViewPrice;
        RecyclerView recyclerView;

        public QuestionStoreRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imgThumb_txt_name_question_store);
            textViewName = itemView.findViewById(R.id.txt_name_question_store);
            textViewPrice = itemView.findViewById(R.id.txt_price_question_store);
            recyclerView = itemView.findViewById(R.id.recyclerView_question_store);
        }
    }
}
