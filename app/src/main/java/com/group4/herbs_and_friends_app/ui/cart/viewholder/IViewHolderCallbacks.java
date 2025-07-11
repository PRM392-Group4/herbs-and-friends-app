package com.group4.herbs_and_friends_app.ui.cart.viewholder;

public interface IViewHolderCallbacks {
    public void onItemClicked(int position);

    public void onItemModifyQuantity(int position, int quantity);

    public void onItemRemove(int position);
}
