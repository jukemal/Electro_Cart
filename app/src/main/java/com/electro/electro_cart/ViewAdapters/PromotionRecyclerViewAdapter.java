package com.electro.electro_cart.ViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.electro.electro_cart.R;

public class PromotionRecyclerViewAdapter extends RecyclerView.Adapter<PromotionRecyclerViewAdapter.PromotionRecyclerViewHolder> {

    Context context;

    int[] sliderImages = {R.drawable.image_slide_1, R.drawable.image_slide_2, R.drawable.image_slide_3, R.drawable.image_slide_4, R.drawable.image_slide_5};

    public PromotionRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public PromotionRecyclerViewAdapter.PromotionRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_promotion_list, parent, false);

        return new PromotionRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromotionRecyclerViewAdapter.PromotionRecyclerViewHolder holder, int position) {
        holder.imageView.setImageResource(sliderImages[position]);
    }

    @Override
    public int getItemCount() {
        return sliderImages.length;
    }

    public class PromotionRecyclerViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        public PromotionRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.imgThumb_item_promotion);
        }
    }
}
