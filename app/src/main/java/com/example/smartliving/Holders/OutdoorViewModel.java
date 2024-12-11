package com.example.smartliving.Holders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OutdoorViewModel extends ViewModel {
    private MutableLiveData<Boolean> doorState = new MutableLiveData<>();
    private MutableLiveData<Boolean> gardenLightsState = new MutableLiveData<>();

    public LiveData<Boolean> getDoorState() {
        return doorState;
    }

    public void setDoorState(boolean state) {
        doorState.setValue(state);
    }

    public LiveData<Boolean> getGardenLightsState() {
        return gardenLightsState;
    }

    public void setGardenLightsState(boolean state) {
        gardenLightsState.setValue(state);
    }
}
