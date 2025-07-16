package com.group4.herbs_and_friends_app.ui.customer_side.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group4.herbs_and_friends_app.data.model.Category;
import com.group4.herbs_and_friends_app.databinding.ItemFilterBinding;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> categoryList;
    private Context context;
    private CategoryCheckListener listener;

    public CategoryAdapter(Context context, CategoryCheckListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
        notifyDataSetChanged();
    }

    public void setSelectedCategories(List<String> selectedCategoryIds) {
        if (categoryList != null && selectedCategoryIds != null) {
            for (Category category : categoryList) {
                if (selectedCategoryIds.contains(category.getId())) {
                    category.setChecked(true);

                    if (category.getChildCategories() != null) {
                        for (Category child : category.getChildCategories()) {
                            if (selectedCategoryIds.contains(child.getId())) {
                                child.setChecked(true);
                            }
                        }
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    public void clearSelectedCategories() {
        for (Category category : categoryList) {
            category.setChecked(false);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(ItemFilterBinding.inflate(LayoutInflater.from(context),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);

        holder.checkBox.setText(category.getName());

        // Set checkbox state without triggering listener
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(category.isChecked());

        if(category.isChildCategory()) {
            holder.indent.setVisibility(View.VISIBLE);
        } else holder.indent.setVisibility(View.GONE);

        holder.checkBox.setOnCheckedChangeListener((v, isChecked) -> {
            category.setChecked(isChecked);

            if(isChecked) {
                if (category.getChildCategories() != null) {
                    for (Category child : category.getChildCategories()) {
                        child.setChecked(true);
                    }
                    notifyDataSetChanged();
                }
            } else {
                if(category.isChildCategory()) {
                    Category parent = categoryList.stream()
                            .filter(c -> c.getId().equals(category.getCategoryParentId()))
                            .findFirst()
                            .get();
                    parent.setChecked(false);
                    notifyDataSetChanged();
                } else if(category.getChildCategories() != null) {
                    for (Category child : category.getChildCategories()) {
                        child.setChecked(false);
                    }
                    notifyDataSetChanged();
                }
            }

            listener.onCategoryChecked(category);
        });
    }

    @Override
    public int getItemCount() {
        return categoryList != null? categoryList.size() : 0;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        View indent;
        public CategoryViewHolder(@NonNull ItemFilterBinding binding) {
            super(binding.getRoot());
            checkBox = binding.filterCheckbox;
            indent = binding.indent;
        }
    }

    public interface CategoryCheckListener {
        void onCategoryChecked(Category category);
    }
}
