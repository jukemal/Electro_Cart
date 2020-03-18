package com.electro.electro_cart.ViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.electro.electro_cart.R;
import com.electro.electro_cart.models.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.storage.FirebaseStorage;

import java.util.LinkedHashMap;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ProductCompareViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Product> productList;

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    LinkedHashMap<String,Object> product1SpecificationMap;
    LinkedHashMap<String,Object> product2SpecificationMap;

    private static final int HEADER = 0;
    private static final int TABLE_HEADER = 1;
    private static final int CONTENT = 2;

    public ProductCompareViewAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;

        ObjectMapper objectMapper=new ObjectMapper();

        product1SpecificationMap=objectMapper.convertValue(productList.get(0).getSpecification(),LinkedHashMap.class);
        product2SpecificationMap=objectMapper.convertValue(productList.get(1).getSpecification(),LinkedHashMap.class);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;

        if (viewType==HEADER){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_compare_product_header, parent, false);

            viewHolder = new Header(view);
        }else if (viewType==TABLE_HEADER){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_compare_product_table_header, parent, false);

            viewHolder = new TableHeader(view);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_compare_product_content, parent, false);

            viewHolder = new ProductContent(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType()==HEADER){

            final Header header=(Header)holder;

            Product product1=productList.get(0);

            Glide.with(context)
                    .load(storage.getReferenceFromUrl(product1.getImage_links().get(0)))
                    .transition(withCrossFade())
                    .fitCenter()
                    .error(R.drawable.error_loading)
                    .fallback(R.drawable.error_loading)
                    .into(header.imageViewProduct1);

            header.textViewProduct1Name.setText(product1.getName());
            header.textViewProduct1Price.setText(String.valueOf(product1.getPrice())+" LKR");
            header.ratingBarProduct1.setRating(product1.getRating());

            Product product2=productList.get(1);

            Glide.with(context)
                    .load(storage.getReferenceFromUrl(product2.getImage_links().get(0)))
                    .transition(withCrossFade())
                    .fitCenter()
                    .error(R.drawable.error_loading)
                    .fallback(R.drawable.error_loading)
                    .into(header.imageViewProduct2);

            header.textViewProduct2Name.setText(product2.getName());
            header.textViewProduct2Price.setText(String.valueOf(product2.getPrice())+" LKR");
            header.ratingBarProduct2.setRating(product2.getRating());

        }else if (holder.getItemViewType()==TABLE_HEADER){
            final TableHeader tableHeader=(TableHeader)holder;

            tableHeader.textViewTableHeader1.setText(productList.get(0).getName());
            tableHeader.textViewTableHeader2.setText(productList.get(1).getName());

        }else {
/*holder.textViewID.setText(specificationMap.keySet().toArray()[position].toString().toUpperCase());
        holder.textViewName.setText(specificationMap.values().toArray()[position].toString());*/

            final ProductContent productContent=(ProductContent)holder;

            productContent.textViewContentId.setText(product1SpecificationMap.keySet().toArray()[position-2].toString().toUpperCase());
            productContent.textViewContentProduct1.setText(product1SpecificationMap.values().toArray()[position-2].toString());
            productContent.textViewContentProduct2.setText(product2SpecificationMap.values().toArray()[position-2].toString());
        }
    }

    @Override
    public int getItemCount() {
        return 14;
    }

    @Override
    public int getItemViewType(int position) {
        if (position==0)
            return HEADER;
        else if (position==1)
            return TABLE_HEADER;
        else return CONTENT;
    }

    public class Header extends RecyclerView.ViewHolder{

        ImageView imageViewProduct1;
        TextView textViewProduct1Name;
        TextView textViewProduct1Price;
        RatingBar ratingBarProduct1;

        ImageView imageViewProduct2;
        TextView textViewProduct2Name;
        TextView textViewProduct2Price;
        RatingBar ratingBarProduct2;

        public Header(@NonNull View itemView) {
            super(itemView);

            imageViewProduct1=itemView.findViewById(R.id.imageView_image1_product_compare);
            textViewProduct1Name=itemView.findViewById(R.id.product_name1_product_compare);
            textViewProduct1Price=itemView.findViewById(R.id.product_price1_product_compare);
            ratingBarProduct1=itemView.findViewById(R.id.product_rating1_product_compare);

            imageViewProduct2=itemView.findViewById(R.id.imageView_image2_product_compare);
            textViewProduct2Name=itemView.findViewById(R.id.product_name2_product_compare);
            textViewProduct2Price=itemView.findViewById(R.id.product_price2_product_compare);
            ratingBarProduct2=itemView.findViewById(R.id.product_rating2_product_compare);
        }
    }

    public class TableHeader extends RecyclerView.ViewHolder{

        TextView textViewTableHeader1;
        TextView textViewTableHeader2;

        public TableHeader(@NonNull View itemView) {
            super(itemView);

            textViewTableHeader1=itemView.findViewById(R.id.name_table_header_product1_compare);
            textViewTableHeader2=itemView.findViewById(R.id.name_table_header_product2_compare);
        }
    }

    public class ProductContent extends RecyclerView.ViewHolder{

        TextView textViewContentId;
        TextView textViewContentProduct1;
        TextView textViewContentProduct2;

        public ProductContent(@NonNull View itemView) {
            super(itemView);

            textViewContentId=itemView.findViewById(R.id.id_content_product_compare);
            textViewContentProduct1=itemView.findViewById(R.id.name_content_product1_compare);
            textViewContentProduct2=itemView.findViewById(R.id.name_content_product2_compare);
        }
    }
}
