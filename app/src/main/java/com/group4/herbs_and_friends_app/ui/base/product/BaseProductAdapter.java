package com.group4.herbs_and_friends_app.ui.base.product;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group4.herbs_and_friends_app.data.model.Product;

import java.util.List;

public abstract class BaseProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected final Context context;
    protected final ProductActionListener listener;
    protected List<Product> productList;

    public BaseProductAdapter(Context context,
                              ProductActionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    /**
     * Supply or update the product list to display.
     */
    public void setProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                      int viewType) {
        return buildViewHolder(parent, viewType);
    }

    /**
     * Inflate and return the correct ViewHolder for the given viewType.
     */
    public abstract RecyclerView.ViewHolder buildViewHolder(ViewGroup parent,
                                                            int viewType);

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,
                                 int position) {
        Product product = productList.get(position);
        bindViewHolder(holder, product);
    }

    /**
     * Bind data into the provided ViewHolder.
     */
    public abstract void bindViewHolder(RecyclerView.ViewHolder holder,
                                        Product product);

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    /**
     * Listener interface for all product actions (detail, edit, delete).
     */
    public interface ProductActionListener {
        void onProductDetailCLick(String productId);

        void onProductEditClick(String productId);

        void onProductDeleteClick(String productId, String productName);
    }
}
