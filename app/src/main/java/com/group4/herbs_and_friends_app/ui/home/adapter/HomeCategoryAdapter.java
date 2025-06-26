package com.group4.herbs_and_friends_app.ui.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.group4.herbs_and_friends_app.R;
import com.group4.herbs_and_friends_app.data.model.Category;
import com.group4.herbs_and_friends_app.utils.AppCts;

import java.util.ArrayList;
import java.util.List;

/**
 *  Adapter for handling recycler view of category section in home fragment
 *  Displays 5 parent categories and "All category" option
 *  Not to be confused with full category list for filtering in product list fragment
 */
public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.HomeCategoryViewHolder> {
    private List<Category> categoryList;
    private Context context;
    private HomeCategoryClickListener listener;

    public HomeCategoryAdapter(Context context, HomeCategoryClickListener listener) {
        this.context = context;
        this.listener = listener;

        initCategoryList();
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList.addAll(1, categoryList);
        notifyItemRangeChanged(1, categoryList.size());
    }

    @NonNull
    @Override
    public HomeCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View categoryView = inflater.inflate(R.layout.home_category_item_layout, parent, false);
        return new HomeCategoryViewHolder(categoryView);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeCategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryName.setText(category.getName());

        int[] resIds = getResIds(position);
        holder.categoryIcon.setImageResource(resIds[0]);
        holder.categoryIcon.setColorFilter(ContextCompat.getColor(context, resIds[1]));
        holder.categoryIcon.setBackgroundColor(ContextCompat.getColor(context, resIds[2]));

        holder.itemView.setOnClickListener(v -> listener.onHomeCategoryClick(category.getId()));
    }

    @Override
    public int getItemCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    public class HomeCategoryViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView categoryIcon;
        TextView categoryName;
        public HomeCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryIcon = itemView.findViewById(R.id.category_ic);
            categoryName = itemView.findViewById(R.id.category_name);
        }
    }

    public interface HomeCategoryClickListener { void onHomeCategoryClick(String categoryId); }

    private void initCategoryList() {
        categoryList = new ArrayList<>();
        Category allCategory = new Category();
        allCategory.setId(AppCts.ALL_CATEGORY_ID);
        allCategory.setName(AppCts.ALL_CATEGORY_NAME);
        categoryList.add(allCategory);
    }

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
