package ca.josue.meteo.model;

import com.google.gson.annotations.SerializedName;

public class Temperature {
    @SerializedName("main")
    Main main;

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }
}
