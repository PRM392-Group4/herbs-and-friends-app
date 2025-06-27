package com.group4.herbs_and_friends_app.ui.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.databinding.ItemImageCarouselBinding;

import java.util.ArrayList;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.ImageViewHolder> {
    private ArrayList<String> imageUrlList;
    private Context context;

    public CarouselAdapter(Context context, ArrayList<String> imageUrlList) {
        this.context = context;
        this.imageUrlList = imageUrlList;
    }

    @NonNull
    @Override
    public CarouselAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageViewHolder(ItemImageCarouselBinding.inflate(LayoutInflater.from(context),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselAdapter.ImageViewHolder holder, int position) {
        Glide.with(context).load(imageUrlList.get(position))
                .error(R.drawable.ic_launcher_foreground)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return imageUrlList != null ? imageUrlList.size() : 0;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        public ImageViewHolder(@NonNull ItemImageCarouselBinding binding) {
            super(binding.getRoot());
            image = binding.itemImage;
        }
    }
}
