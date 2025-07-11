package com.group4.herbs_and_friends_app.ui.cart.viewholder;

public interface IViewHolderListeners {
    public void onItemClicked(String cartId);

    public void onItemModifyQuantity(String cartId, int quantity);
}
