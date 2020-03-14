package com.electro.electro_cart.ui.home;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.electro.electro_cart.models.Product;
import com.electro.electro_cart.models.Question;
import com.electro.electro_cart.models.Rating;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeViewModel extends ViewModel {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final CollectionReference collectionReference = db.collection("products");

    private MutableLiveData<List<Product>> products;
    private MutableLiveData<Map<String, List<Rating>>> ratings;
    private MutableLiveData<Map<String, List<Question>>> questions;

    public LiveData<List<Product>> getProducts(final Context context) {
        if (products == null) {
            products = new MutableLiveData<>();
            final List<Product> productList = new ArrayList<>();

            collectionReference.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot product : task.getResult()) {
                                    Product p = product.toObject(Product.class);
                                    productList.add(p);
                                }
                            }

                            products.postValue(productList);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Failed to load products. Check your internet connection.", Toast.LENGTH_LONG);
                        }
                    });
        }
        return products;
    }

    public LiveData<Map<String, List<Rating>>> getRatings(final Context context, final String id) {
        if (ratings == null || !ratings.getValue().containsKey(id)) {
            ratings = new MutableLiveData<>();
            final List<Rating> ratingList = new ArrayList<>();
            final Map<String, List<Rating>> ratingMap = new HashMap<>();

            collectionReference.document(id)
                    .collection("ratings")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot rating : task.getResult()) {
                                    Rating r = rating.toObject(Rating.class);
                                    ratingList.add(r);
                                }
                                ratingMap.put(id, ratingList);
                                ratings.postValue(ratingMap);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Failed to load ratings. Check your internet connection.", Toast.LENGTH_LONG);
                        }
                    });
        }
        return ratings;
    }

    public LiveData<Map<String, List<Question>>> getQuestions(final Context context, final String id) {
        if (questions == null || !questions.getValue().containsKey(id)) {
            questions = new MutableLiveData<>();
            final List<Question> questionList = new ArrayList<>();
            final Map<String, List<Question>> questionMap = new HashMap<>();

            collectionReference.document(id)
                    .collection("questions")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot question : task.getResult()) {
                                    Question q = question.toObject(Question.class);
                                    questionList.add(q);
                                }
                                questionMap.put(id, questionList);
                                questions.postValue(questionMap);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Failed to load questions. Check your internet connection.", Toast.LENGTH_LONG);
                        }
                    });
        }
        return questions;
    }

    public void setFavourite(String id,boolean isFavourite){
        collectionReference.document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Product product=documentSnapshot.toObject(Product.class);

            }
        });
    }
}