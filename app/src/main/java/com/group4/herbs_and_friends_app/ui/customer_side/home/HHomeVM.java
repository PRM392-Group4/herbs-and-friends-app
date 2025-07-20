package com.group4.herbs_and_friends_app.ui.customer_side.home;

import com.group4.herbs_and_friends_app.data.repository.CategoryRepository;
import com.group4.herbs_and_friends_app.data.repository.ProductRepository;
import com.group4.herbs_and_friends_app.ui.base.product.BaseProductViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HHomeVM extends BaseProductViewModel {
    @Inject
    public HHomeVM(CategoryRepository cateRepo,
                   ProductRepository productRepo) {
        super(cateRepo, productRepo);
    }
}