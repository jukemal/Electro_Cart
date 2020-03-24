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
import androidx.navigation.Navigation;

import com.electro.electro_cart.R;
import com.electro.electro_cart.models.Product;
import com.electro.electro_cart.models.Promotion;
import com.electro.electro_cart.models.Specification;
import com.electro.electro_cart.utils.EnumProductType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.Map;

import lombok.SneakyThrows;

public class AddProductStoreFragment extends Fragment {

    @SneakyThrows
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_store_add_product,container,false);

        LinearLayout linearLayout = root.findViewById(R.id.linear_layout_add_product_store);

        Button buttonUpdate=root.findViewById(R.id.btn_add_product_store_add_product);

        Specification specification = Specification.builder().cpu("")
                .gpu("")
                .display("")
                .memory("")
                .ram("")
                .os("")
                .battery("")
                .material("")
                .dimensions("")
                .weight("")
                .port("")
                .port("")
                .port("")
                .port("")
                .port("")
                .port("")
                .port("")
                .feature("")
                .feature("")
                .feature("")
                .feature("")
                .feature("").build();

       final Product product = Product.builder().name("")
                .price(0)
                .productType(EnumProductType.LAPTOP)
                .description(null)
                .specification(specification)
                .image_link("")
                .image_link("")
                .ar_link("")
                .rating(0).build();

        ObjectMapper objectMapper = new ObjectMapper();

        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(product);

        Map<String, Object> flattenedJsonMap = JsonFlattener.flattenAsMap(json);

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

        return root;
    }
}
