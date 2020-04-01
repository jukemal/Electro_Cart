package com.electro.electro_cart.ViewAdapters;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.electro.electro_cart.R;
import com.electro.electro_cart.models.Product;
import com.electro.electro_cart.models.Promotion;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import lombok.SneakyThrows;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class PromotionStoreRecyclerViewAdapter extends RecyclerView.Adapter<PromotionStoreRecyclerViewAdapter.PromotionStoreRecyclerViewHolder> {

    private static final String TAG = "Promotion";
    private Context context;
    private List<Product> productList;
    PromotionStoreRecyclerViewAdapterClickInterface promotionStoreRecyclerViewAdapterClickInterface;

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final CollectionReference collectionReferenceProduct = db.collection("products");

    public PromotionStoreRecyclerViewAdapter(Context context, List<Product> productList, PromotionStoreRecyclerViewAdapterClickInterface promotionStoreRecyclerViewAdapterClickInterface) {
        this.context = context;
        this.productList = productList;
        this.promotionStoreRecyclerViewAdapterClickInterface = promotionStoreRecyclerViewAdapterClickInterface;
    }

    @NonNull
    @Override
    public PromotionStoreRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_store_promotion,parent,false);

       return new PromotionStoreRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromotionStoreRecyclerViewHolder holder, int position) {
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

        final Promotion promotion=product.getPromotion();

        if (promotion==null){
            holder.buttonAdd.setVisibility(View.VISIBLE);
        }else {
            holder.textViewDescription.setText(promotion.getDescription());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MMM-dd", Locale.ENGLISH);
            holder.textViewEndDate.setText(simpleDateFormat.format(promotion.getEndDate()));

            holder.textViewDiscount.setText(String.valueOf(promotion.getDiscountPercentage())+"%");

            holder.linearLayout.setVisibility(View.VISIBLE);
        }

        holder.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogView= LayoutInflater.from(context).inflate(R.layout.layout_store_promotion_popup,null);

                MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(context);
                builder.setView(dialogView);
                final AlertDialog alertDialog=builder.show();

                TextInputLayout textInputLayoutDescription=dialogView.findViewById(R.id.txtDescriptionLayout);
                TextInputEditText textInputEditTextDescription=dialogView.findViewById(R.id.txtDescription);

                TextInputLayout textInputLayoutEndDate=dialogView.findViewById(R.id.txtEndDateLayout);
                TextInputEditText textInputEditTextEndDate=dialogView.findViewById(R.id.txtEndDate);

                final Calendar calendar=Calendar.getInstance();

                DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        calendar.set(Calendar.YEAR,i);
                        calendar.set(Calendar.MONTH,i1);
                        calendar.set(Calendar.DAY_OF_MONTH,i2);

                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MMM-dd",Locale.ENGLISH);

                        textInputEditTextEndDate.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };

                textInputEditTextEndDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new DatePickerDialog(context,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });

                NumberPicker numberPicker=dialogView.findViewById(R.id.number_picker);

                numberPicker.setMinValue(1);
                numberPicker.setMaxValue(99);

                Button buttonAddPromotion=dialogView.findViewById(R.id.btnAdd);

                buttonAddPromotion.setOnClickListener(new View.OnClickListener() {
                    @SneakyThrows
                    @Override
                    public void onClick(View view) {
                        String description=textInputEditTextDescription.getText().toString();
                        String endDate=textInputEditTextEndDate.getText().toString();

                        if (description.isEmpty()||endDate.isEmpty()){
                            if (description.isEmpty()){
                                textInputLayoutDescription.setError("Enter Description");
                            }else {
                                textInputLayoutDescription.setError(null);
                            }
                            if (endDate.isEmpty()){
                                textInputLayoutEndDate.setError("Enter Date");
                            }else {
                                textInputLayoutEndDate.setError(null);
                            }
                        }else {
                            textInputLayoutDescription.setError(null);
                            textInputLayoutEndDate.setError(null);

                            Promotion promotion1=Promotion.builder()
                                    .description(description)
                                    .endDate(new SimpleDateFormat("yyyy-MMM-dd",Locale.ENGLISH).parse(endDate))
                                    .discountPercentage(numberPicker.getValue())
                                    .build();

                            collectionReferenceProduct
                                    .document(product.getId())
                                    .update("promotion",promotion1)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                                            Toast.makeText(context,"Promotion Successfully Added.",Toast.LENGTH_SHORT).show();
                                            alertDialog.dismiss();
                                            promotionStoreRecyclerViewAdapterClickInterface.UpdatePromotionList(position);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error updating document", e);
                                            Toast.makeText(context,"Error Promotion Adding.",Toast.LENGTH_SHORT).show();
                                            alertDialog.dismiss();
                                        }
                                    });
                        }
                    }
                });
            }
        });

        holder.buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(context);

                builder.setMessage("Confirm Delete Promotion.")
                        .setTitle("Promotion")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                collectionReferenceProduct
                                        .document(product.getId())
                                        .update("promotion",null)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                holder.linearLayout.setVisibility(View.GONE);
                                                holder.buttonAdd.setVisibility(View.VISIBLE);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error updating document", e);
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                builder.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }


    public class PromotionStoreRecyclerViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textViewName;
        TextView textViewPrice;

        TextView textViewDescription;
        TextView textViewEndDate;
        TextView textViewDiscount;

        Button buttonAdd;
        Button buttonRemove;

        LinearLayout linearLayout;

        public PromotionStoreRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imgThumb_txt_name_promotion_store);
            textViewName = itemView.findViewById(R.id.txt_name_promotion_store);
            textViewPrice = itemView.findViewById(R.id.txt_price_promotion_store);

            textViewDescription= itemView.findViewById(R.id.txt_description);
            textViewEndDate= itemView.findViewById(R.id.txt_endDate);
            textViewDiscount= itemView.findViewById(R.id.txt_discount);

            buttonAdd= itemView.findViewById(R.id.btn_add_promotion_store);
            buttonRemove= itemView.findViewById(R.id.btn_remove_promotion_store);

            linearLayout= itemView.findViewById(R.id.linear_layout);
        }
    }
}
