package com.electro.electro_cart.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import androidx.appcompat.widget.SearchView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.electro.electro_cart.R;
import com.electro.electro_cart.ViewAdapters.SearchRecyclerViewAdapter;
import com.electro.electro_cart.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private SearchRecyclerViewAdapter searchRecyclerViewAdapter;
    SearchView searchView;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final CollectionReference collectionReference = db.collection("products");


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_search, container, false);

        ProgressBar progressBar = root.findViewById(R.id.progressBar_search);

        recyclerView = root.findViewById(R.id.recyclerview_search);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        collectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Product> productList = new ArrayList<>();


                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot product : task.getResult()) {
                                Product p = product.toObject(Product.class);
                                productList.add(p);
                            }

                            searchRecyclerViewAdapter = new SearchRecyclerViewAdapter(getActivity(), productList);
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setAdapter(searchRecyclerViewAdapter);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed to load products. Check your internet connection.", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        searchView = root.findViewById(R.id.searchView_search);

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setQuery("", false);
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchRecyclerViewAdapter.getFilter().filter(s);
                return false;
            }
        });

        ImageButton imageButton = root.findViewById(R.id.sort_imgBtn);

        PopupMenu popupMenu = new PopupMenu(getActivity(), imageButton);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_search, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                searchView.setQuery("", false);
                searchView.clearFocus();

                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);

                if (menuItem.getItemId() == R.id.name_a_z_search_menu) {
                    collectionReference
                            .orderBy("name")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    List<Product> productList = new ArrayList<>();

                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot product : task.getResult()) {
                                            Product p = product.toObject(Product.class);
                                            productList.add(p);
                                        }

                                        searchRecyclerViewAdapter=null;
                                        searchRecyclerViewAdapter=new SearchRecyclerViewAdapter(getContext(),productList);

                                        recyclerView.setAdapter(null);
                                        recyclerView.setLayoutManager(null);
                                        recyclerView.setAdapter(searchRecyclerViewAdapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

                                        progressBar.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Failed to load products. Check your internet connection.", Toast.LENGTH_LONG).show();
                        }
                    });

                    return true;
                } else if (menuItem.getItemId() == R.id.name_z_a_search_menu) {
                    collectionReference
                            .orderBy("name", Query.Direction.DESCENDING)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    List<Product> productList = new ArrayList<>();

                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot product : task.getResult()) {
                                            Product p = product.toObject(Product.class);
                                            productList.add(p);
                                        }

                                        searchRecyclerViewAdapter=null;
                                        searchRecyclerViewAdapter=new SearchRecyclerViewAdapter(getContext(),productList);

                                        recyclerView.setAdapter(null);
                                        recyclerView.setLayoutManager(null);
                                        recyclerView.setAdapter(searchRecyclerViewAdapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

                                        progressBar.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Failed to load products. Check your internet connection.", Toast.LENGTH_LONG).show();
                        }
                    });

                    return true;
                } else if (menuItem.getItemId() == R.id.price_low_high_search_menu) {
                    collectionReference
                            .orderBy("price")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    List<Product> productList = new ArrayList<>();

                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot product : task.getResult()) {
                                            Product p = product.toObject(Product.class);
                                            productList.add(p);
                                        }

                                        searchRecyclerViewAdapter=null;
                                        searchRecyclerViewAdapter=new SearchRecyclerViewAdapter(getContext(),productList);

                                        recyclerView.setAdapter(null);
                                        recyclerView.setLayoutManager(null);
                                        recyclerView.setAdapter(searchRecyclerViewAdapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

                                        progressBar.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Failed to load products. Check your internet connection.", Toast.LENGTH_LONG).show();
                        }
                    });

                    return true;
                } else if (menuItem.getItemId() == R.id.price_high_low_search_menu) {
                    collectionReference
                            .orderBy("price", Query.Direction.DESCENDING)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    List<Product> productList = new ArrayList<>();

                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot product : task.getResult()) {
                                            Product p = product.toObject(Product.class);
                                            productList.add(p);
                                        }

                                        searchRecyclerViewAdapter=null;
                                        searchRecyclerViewAdapter=new SearchRecyclerViewAdapter(getContext(),productList);

                                        recyclerView.setAdapter(null);
                                        recyclerView.setLayoutManager(null);
                                        recyclerView.setAdapter(searchRecyclerViewAdapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

                                        progressBar.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Failed to load products. Check your internet connection.", Toast.LENGTH_LONG).show();
                        }
                    });

                    return true;
                }
                return false;
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        searchView.setQuery("", false);
        searchView.clearFocus();
    }
}