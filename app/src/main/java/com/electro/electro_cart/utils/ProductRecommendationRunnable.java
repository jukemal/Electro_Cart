package com.electro.electro_cart.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import com.electro.electro_cart.models.Product;
import com.electro.electro_cart.models.Rating;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ProductRecommendationRunnable implements Runnable {

    private static final String TAG = "Product Recommendation";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final CollectionReference collectionReferenceProduct = db.collection("products");

    private final CollectionReference collectionReferenceUser = db.collection("users");

    private final String currentUserId=firebaseAuth.getCurrentUser().getUid();

    @Override
    public void run() {
        Map<String,Map<String,Integer>> map=new HashMap<>();
        collectionReferenceProduct
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Product product=document.toObject(Product.class);

                                collectionReferenceProduct
                                        .document(product.getId())
                                        .collection("ratings")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {

                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Log.d(TAG, document.getId() + " => " + document.getData());

                                                        Rating rating=document.toObject(Rating.class);

                                                        if (map.get(rating.getOwnerId())==null){
                                                            Map<String,Integer> map1=new HashMap<>();
                                                            map1.put(product.getId(),rating.getScore());

                                                            map.put(rating.getOwnerId(),map1);
                                                        }else {
                                                            Map<String,Integer> map1=map.get(rating.getOwnerId());
                                                            map1.put(product.getId(),rating.getScore());

                                                            map.put(rating.getOwnerId(),map1);
                                                        }
                                                        if (map.size()>=2){
                                                            collectionReferenceUser
                                                                    .document(currentUserId)
                                                                    .update("recommendationList",recommendationEngine(map,currentUserId))
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Log.w(TAG, "Error updating document", e);
                                                                        }
                                                                    });
                                                        }
                                                    }

                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private double productCorrelation(Map<String,Map<String,Integer>> map, String user1Id, String user2Id) {
        Map<String,Integer> user1_ratingList = map.get(user1Id);

        Map<String,Integer> user2_ratingList = map.get(user2Id);

        List<ExistsInBoth> existsInBothList=new ArrayList<>();

        for (Map.Entry<String,Integer> r1:user1_ratingList.entrySet()){
            for (Map.Entry<String,Integer> r2:user2_ratingList.entrySet()){
                if (r1.getKey().equals(r2.getKey())){
                    Item item1=new Item(r1.getKey(),r1.getValue());
                    Item item2=new Item(r2.getKey(),r2.getValue());
                    existsInBothList.add(new ExistsInBoth(item1,item2));
                }
            }
        }

        if (existsInBothList.isEmpty())
            return 0;

        double user1_sum = 0;
        double user2_sum = 0;
        double user1_sq_sum = 0;
        double user2_sq_sum = 0;
        double prod_user1_user_2 = 0;

        for (ExistsInBoth e : existsInBothList) {
            user1_sum += e.getItem1().getRating();
            user2_sum += e.getItem2().getRating();
            user1_sq_sum += Math.pow(e.getItem1().getRating(), 2);
            user2_sq_sum += Math.pow(e.getItem2().getRating(), 2);
            prod_user1_user_2 += e.getItem1().getRating() * e.getItem2().getRating();
        }

        double numerator = prod_user1_user_2 - ((user1_sum * user2_sum) / existsInBothList.size());
        double st1 = user1_sq_sum - (Math.pow(user1_sum, 2) / existsInBothList.size());
        double st2 = user2_sq_sum - (Math.pow(user2_sum, 2) / existsInBothList.size());
        double denominator = Math.sqrt(st1 * st2);

        if (Double.isNaN(numerator/denominator)) {
            return 0;
        } else {
            return numerator / denominator;
        }
    }

    private List<String> recommendationEngine(Map<String,Map<String,Integer>> map,String user){

        List<String> recommendedProductList=new LinkedList<>();

        Map<String,RecommendedItem> temp=new HashMap<>();
        Map<String,Double> recommendedItemMap=new HashMap<>();

        for (Map.Entry<String,Map<String,Integer>> m:map.entrySet()){
            if (m.getKey().equals(user)) continue;

            double similer=productCorrelation(map,user,m.getKey());

            if (similer<=0) continue;

            for (Map.Entry<String,Integer> n:m.getValue().entrySet()){
                temp.put(n.getKey(),new RecommendedItem(n.getValue()*similer,similer));
            }
        }

        for (Map.Entry<String,RecommendedItem> m:temp.entrySet()){
            double val=m.getValue().getTotals()/m.getValue().getSimsum();
            recommendedItemMap.put(m.getKey(),val);
        }

        Map<String,Double> recommendedItemMapSorted=sortByValue(recommendedItemMap);

        for (Map.Entry<String,Double> m:recommendedItemMapSorted.entrySet()){
            recommendedProductList.add(m.getKey());
        }

        return recommendedProductList;
    }

    static private class Item{
        private String productId;
        private int rating;

        Item(String productId, int rating) {
            this.productId = productId;
            this.rating = rating;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "productId='" + productId + '\'' +
                    ", rating=" + rating +
                    '}';
        }
    }

    static private class ExistsInBoth {
        private Item item1;
        private Item item2;

        public ExistsInBoth(Item item1, Item item2) {
            this.item1 = item1;
            this.item2 = item2;
        }

        public Item getItem1() {
            return item1;
        }

        public void setItem1(Item item1) {
            this.item1 = item1;
        }

        public Item getItem2() {
            return item2;
        }

        public void setItem2(Item item2) {
            this.item2 = item2;
        }

        @Override
        public String toString() {
            return "ExistsInBoth{" +
                    "item1=" + item1 +
                    ", item2=" + item2 +
                    '}';
        }
    }

    private  static class RecommendedItem{
        private double totals;
        private double simsum;

        public RecommendedItem(double totals, double simsum) {
            this.totals = totals;
            this.simsum = simsum;
        }

        public double getTotals() {
            return totals;
        }

        public void setTotals(double totals) {
            this.totals = totals;
        }

        public double getSimsum() {
            return simsum;
        }

        public void setSimsum(double simsum) {
            this.simsum = simsum;
        }

        @Override
        public String toString() {
            return "RecommendedItem{" +
                    "totals=" + totals +
                    ", simsum=" + simsum +
                    '}';
        }
    }

    private static Map<String, Double> sortByValue(Map<String, Double> unsortMap) {

        List<Map.Entry<String, Double>> list = new LinkedList<>(unsortMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> a, Map.Entry<String, Double> b) {
                if (b.getValue()<a.getValue()) {
                    return -1;
                }else if (b.getValue()>a.getValue()) {
                    return 1;
                }else {
                return 0;
                }
            }
        });

        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        for (Map.Entry<String, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

         return sortedMap;
    }
}
