package com.electro.electro_cart.ui.store;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.electro.electro_cart.R;
import com.electro.electro_cart.ViewAdapters.OrderHistoryRecyclerViewAdapter;
import com.electro.electro_cart.ViewAdapters.TransactionHistoryStoreRecyclerViewAdapter;
import com.electro.electro_cart.models.User;
import com.electro.electro_cart.utils.EnumUserType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/*
 * Class for transaction history.
 */
public class TransactionHistoryStoreFragment extends Fragment {

    private RecyclerView recyclerView;
    private TransactionHistoryStoreRecyclerViewAdapter transactionHistoryStoreRecyclerViewAdapter;

    ProgressBar progressBar;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final CollectionReference collectionReferenceProduct = db.collection("products");

    private final CollectionReference collectionReferenceUser = db.collection("users");

    private final CollectionReference collectionReferenceOrderHistory = db.collection("users")
            .document(firebaseAuth.getCurrentUser().getUid())
            .collection("Order_History");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_store_transaction_history,container,false);

        progressBar=root.findViewById(R.id.progressBar_fragment_history_store);

        recyclerView=root.findViewById(R.id.recyclerView_fragment_history_store);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        /*
         * All the purchases made by users that are related to currently logged in store is fetched and displayed in a list.
         */
        collectionReferenceUser
                .whereEqualTo("userType", EnumUserType.USER)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<User> userList=new ArrayList<>();

                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user=document.toObject(User.class);
                                userList.add(user);
                            }

                            transactionHistoryStoreRecyclerViewAdapter=new TransactionHistoryStoreRecyclerViewAdapter(getContext(),userList);
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setAdapter(transactionHistoryStoreRecyclerViewAdapter);

                            Log.e("History",userList.toString());
                        }else {
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Failed to load products. Check your internet connection.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        return root;
    }
}
