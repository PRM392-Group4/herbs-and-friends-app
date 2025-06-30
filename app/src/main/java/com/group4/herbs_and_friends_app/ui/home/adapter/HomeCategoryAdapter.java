package com.group4.herbs_and_friends_app.ui.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.data.model.Category;
import com.group4.herbs_and_friends_app.databinding.ItemHomeCategoryBinding;
import com.group4.herbs_and_friends_app.utils.AppCts;

import java.util.ArrayList;
import java.util.List;

/**
 *  Adapter for handling recycler view of category section in home fragment
 *  Displays 5 parent categories and "All category" option
 *  Not to be confused with full category list for filtering in product list fragment
 */
public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.HomeCategoryViewHolder> {
    private List<Category> categoryList = new ArrayList<>();
    private Context context;
    private HomeCategoryClickListener listener;

    public HomeCategoryAdapter(Context context, HomeCategoryClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setCategoryList(List<Category> categoryList) {
        Category allCategory = new Category();
        allCategory.setId(context.getString(R.string.all_cate_id));
        allCategory.setName(context.getString(R.string.all_cate_name));
        categoryList.add(0, allCategory);
        this.categoryList = categoryList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HomeCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeCategoryViewHolder(ItemHomeCategoryBinding.inflate(LayoutInflater.from(context),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HomeCategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryName.setText(category.getName());

        int[] resIds = getResIds(position);
        holder.categoryIcon.setImageResource(resIds[0]);
        holder.categoryIcon.setColorFilter(ContextCompat.getColor(context, resIds[1]));
        holder.categoryIcon.setBackgroundColor(ContextCompat.getColor(context, resIds[2]));

        holder.itemView.setOnClickListener(v -> listener.onHomeCategoryClick(category));
    }

    @Override
    public int getItemCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    public class HomeCategoryViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView categoryIcon;
        TextView categoryName;
        public HomeCategoryViewHolder(@NonNull ItemHomeCategoryBinding binding) {
            super(binding.getRoot());
            categoryIcon = binding.categoryIc;
            categoryName = binding.categoryName;
        }
    }

    public interface HomeCategoryClickListener { void onHomeCategoryClick(Category category); }

    private int[] getResIds(int position) {
        int[] resIds = new int[3];

        switch (position) {
            case 0:
                resIds[0] = R.drawable.ic_category_all;
                resIds[1] = R.color.h_on_primary;
                resIds[2] = R.color.h_primary;
                break;
            case 1:
                resIds[0] = R.drawable.ic_category_outdoor_plant;
                resIds[1] = R.color.h_info;
                resIds[2] = R.color.h_skyblue_light;
                break;
            case 2:
                resIds[0] = R.drawable.ic_category_indoor_plant;
                resIds[1] = R.color.h_secondary;
                resIds[2] = R.color.h_yellow_light;
                break;
            case 3:
                resIds[0] = R.drawable.ic_category_edible;
                resIds[1] = R.color.h_error;
                resIds[2] = R.color.h_red_light;
                break;
            case 4:
                resIds[0] = R.drawable.ic_category_seed;
                resIds[1] = R.color.h_purple;
                resIds[2] = R.color.h_purple_light;
                break;
            case 5:
                resIds[0] = R.drawable.ic_category_tool;
                resIds[1] = R.color.h_highlight;
                resIds[2] = R.color.h_blue_light;
                break;
        }

        return resIds;
    }
}
