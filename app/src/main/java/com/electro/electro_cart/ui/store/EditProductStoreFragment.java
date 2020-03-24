package com.electro.electro_cart.ui.store;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.electro.electro_cart.R;
import com.electro.electro_cart.models.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;

public class EditProductStoreFragment extends Fragment {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final CollectionReference collectionReference = db.collection("products");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_store_edit_product, container, false);

        final String id = getArguments().getString("id");
        Log.e("ID", id);

        LinearLayout linearLayout = root.findViewById(R.id.linear_layout_edit_product_store);

        Button buttonUpdate=root.findViewById(R.id.btn_update_product_store_edit_product);

        collectionReference.document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    private static final String TAG = "Edit Product";

                    @SneakyThrows
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.e(TAG, "DocumentSnapshot data: " + document.getData());

                                Product product = document.toObject(Product.class);

                                ObjectMapper objectMapper = new ObjectMapper();

                                String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(product);

                                String flattenedJson = JsonFlattener.flatten(json);

                                Map<String, Object> flattenedJsonMap = JsonFlattener.flattenAsMap(json);

                                List<TextInputLayout> textInputLayoutList = new ArrayList<>();

                                for (Map.Entry<String, Object> p : flattenedJsonMap.entrySet()) {
                                    String key = p.getKey();
                                    Object object = p.getValue();

                                    Log.e("Key", key);

                                    if (!key.equals("id") && !key.equals("timestamp")&& !key.equals("storeId")&& !key.contains("promotion")) {
                                        key=key.substring(0, 1).toUpperCase() + key.substring(1);
                                        key=key.replace("_"," ");
                                        key=key.replace("."," : ");

                                        String value = null;

                                        if (object instanceof Integer) {
                                            Log.e("value", String.valueOf((int) object));
                                            value = String.valueOf((int) object);
                                        } else {
                                            Log.e("value", String.valueOf(object));
                                            value = String.valueOf(object);
                                        }

                                        View viewTextInputLayout = inflater.inflate(R.layout.layout_text_box, null);

                                        TextInputLayout textInputLayout = viewTextInputLayout.findViewById(R.id.textInputLayout_text_box);
                                        textInputLayout.setHint(key);

                                        TextInputEditText editText = viewTextInputLayout.findViewById(R.id.textInputEditText_text_box);
                                        editText.setText(value);

                                        linearLayout.addView(viewTextInputLayout);
                                    }

                                }

                                buttonUpdate.setVisibility(View.VISIBLE);

                                buttonUpdate.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());

                                        builder.setTitle("Update Product")
                                                .setMessage("Do you want to update this Product?")
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        Navigation.findNavController(root).navigateUp();
                                                    }
                                                })
                                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                       dialogInterface.cancel();
                                                    }
                                                });

                                        builder.show();
                                    }
                                });


                            } else {
                                Log.e(TAG, "No such document");
                            }
                        } else {
                            Log.e(TAG, "get failed with ", task.getException());
                        }
                    }
                });

        return root;
    }


}
