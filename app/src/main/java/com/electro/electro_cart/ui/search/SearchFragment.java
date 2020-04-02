package com.electro.electro_cart.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
    private SearchView searchView;

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
                        } else {
                            Toast.makeText(getContext(), "Failed to load products. Check your internet connection.", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                        }
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

                                        searchRecyclerViewAdapter = null;
                                        searchRecyclerViewAdapter = new SearchRecyclerViewAdapter(getContext(), productList);

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

                                        searchRecyclerViewAdapter = null;
                                        searchRecyclerViewAdapter = new SearchRecyclerViewAdapter(getContext(), productList);

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

                                        searchRecyclerViewAdapter = null;
                                        searchRecyclerViewAdapter = new SearchRecyclerViewAdapter(getContext(), productList);

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

                                        searchRecyclerViewAdapter = null;
                                        searchRecyclerViewAdapter = new SearchRecyclerViewAdapter(getContext(), productList);

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

        //--------------------------------------------------------------------------------------------------------
        //Filter

        ImageButton imageButtonFilter=root.findViewById(R.id.filter_imgBtn);

        imageButtonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogView= inflater.inflate(R.layout.layout_search_filter_popup, null);

                MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(getContext());
                builder.setView(dialogView);
                final AlertDialog alertDialog=builder.show();

                Button buttonFilter=dialogView.findViewById(R.id.btnFilter);

                buttonFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                ImageButton imageButtonClose=dialogView.findViewById(R.id.close);

                imageButtonClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                {
                    AppCompatSpinner appCompatSpinner = dialogView.findViewById(R.id.spinner_type);

                    List<String> list = new ArrayList<>();
                    list.add("Laptop");
                    list.add("Phone");

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, list);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    appCompatSpinner.setAdapter(arrayAdapter);
                }

                {
                    AppCompatSpinner appCompatSpinner = dialogView.findViewById(R.id.spinner_processor);

                    List<String> list = new ArrayList<>();
                    list.add("Intel Core i7");
                    list.add("Intel Core i5");
                    list.add("Intel Core i3");

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, list);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    appCompatSpinner.setAdapter(arrayAdapter);
                }

                {
                    AppCompatSpinner appCompatSpinner = dialogView.findViewById(R.id.spinner_display);

                    List<String> list = new ArrayList<>();
                    list.add("15\" HD");
                    list.add("15\" Full HD");
                    list.add("17\" HD");
                    list.add("17\" Full HD");

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, list);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    appCompatSpinner.setAdapter(arrayAdapter);
                }

                {
                    AppCompatSpinner appCompatSpinner = dialogView.findViewById(R.id.spinner_memory);

                    List<String> list = new ArrayList<>();
                    list.add("4GB");
                    list.add("8GB");
                    list.add("16GB");
                    list.add("32GB");
                    list.add("64GB");

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, list);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    appCompatSpinner.setAdapter(arrayAdapter);
                }

                {
                    AppCompatSpinner appCompatSpinner = dialogView.findViewById(R.id.spinner_storage);

                    List<String> list = new ArrayList<>();
                    list.add("128GB");
                    list.add("256GB");
                    list.add("512GB");
                    list.add("1TB");
                    list.add("2TB");
                    list.add("4TB");

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, list);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    appCompatSpinner.setAdapter(arrayAdapter);
                }

                {
                    AppCompatSpinner appCompatSpinner = dialogView.findViewById(R.id.spinner_graphics);

                    List<String> list = new ArrayList<>();
                    list.add("Nvidia");
                    list.add("AMD");
                    list.add("Qualcomm");

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, list);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    appCompatSpinner.setAdapter(arrayAdapter);
                }

                {
                    AppCompatSpinner appCompatSpinner = dialogView.findViewById(R.id.spinner_operating_system);

                    List<String> list = new ArrayList<>();
                    list.add("Windows 10");
                    list.add("Windows 8");
                    list.add("Windows 7");
                    list.add("MacOS");
                    list.add("Linux");
                    list.add("Android");

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, list);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    appCompatSpinner.setAdapter(arrayAdapter);
                }
            }
        });

        //Filter End
        //--------------------------------------------------------------------------------------------------------

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        searchView.setQuery("", false);
        searchView.clearFocus();
    }
}