package com.electro.electro_cart.ui.promotions;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PromotionsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PromotionsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is promotions fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}