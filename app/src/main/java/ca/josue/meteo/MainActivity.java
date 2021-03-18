package ca.josue.meteo;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import ca.josue.meteo.interfaces.Weather;
import ca.josue.meteo.model.Main;
import ca.josue.meteo.model.Temperature;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private final String NAME_SERVER = "https://api.openweathermap.org/data/2.5/";
    private final String myKEY = "4e2908b291e3062a8bd8774aac219ccb";
    private final String url = "api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}";

    private EditText editText;
    private TextView textViewTemperatureExt;
    private TextView textViewTemperatureRessentie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // relier les champs
        editText = findViewById(R.id.name_city);
        Button btnValider = findViewById(R.id.btn_valider);
        textViewTemperatureExt = findViewById(R.id.showTempExt);
        textViewTemperatureRessentie = findViewById(R.id.showTempRessenti);

        // lorsqu'on clique sur le button valider
        btnValider.setOnClickListener(v -> {

            // lancer retrofit
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(NAME_SERVER)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // Vérifier si le champs sont vides
            if(TextUtils.isEmpty(editText.getText().toString())){
                Snackbar.make(v, "Please enter a city", 5000)
                        .setAction("I understand", d -> {
                        }).setActionTextColor(Color.parseColor("#FFB0D9B9")).show();

                editText.setError("City required");
                return;
            }

            // création l'instance de l'interface par injection
            Weather myAPI = retrofit.create(Weather.class);
            Call<Temperature> temperatureCall = myAPI.getTemperature(editText.getText().toString().trim(), myKEY);
            temperatureCall.enqueue(new Callback<Temperature>() {
                @Override
                public void onResponse(Call<Temperature> call, Response<Temperature> response) {

                    // Si la Ville n'est pas bonne
                    if (response.code() == 404) {
                        Snackbar.make(v, "Entrer une ville Valide", 5000)
                                .setAction("Okay", d -> {
                                }).setActionTextColor(Color.parseColor("#FFB0D9B9")).show();
                        return;
                    } else if (!response.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Code : " + response.code(), Toast.LENGTH_LONG).show();
                    }

                    // Récupérer la Reponse de la requête
                    Temperature temperature = response.body();
                    Main main = temperature.getMain();
                    Double tempAir = main.getTemp();
                    Double tempRessenti = main.getFeelsLike();

                    // Soustraire pour obtenir la température exacte
                    Integer temperatureAir = (int) (tempAir - 273.15);
                    Integer temperatureressenti = (int) (tempRessenti - 273.15);

                    // Setter les températures
                    textViewTemperatureExt.setText(String.valueOf(temperatureAir + " °C\n"));
                    textViewTemperatureRessentie.setText(String.valueOf(temperatureressenti + " °C\n"));
                }

                @Override
                public void onFailure(Call<Temperature> call, Throwable t) {
                    // En cas d'echec
                    Toast.makeText(MainActivity.this, "Erreur : " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

    }
}