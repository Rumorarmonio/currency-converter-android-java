package com.example.currencyconverterjava;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.currencyconverterjava.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ArrayList<Currency> currencyList;
    private final Handler mainHandler = new Handler();
    private ProgressDialog progressDialog;
    private Currency currencyToConvert;
    private Currency destinationCurrency;
    private AlertDialog dialog;
    private boolean isRubles = false;
    private boolean toUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        убирает status bar и action bar
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        чтение данных из интернета
        new FetchData().start();

        binding.checkBox.setOnClickListener(v -> {
            isRubles = !isRubles;
            binding.firstSpinnerContainer.setVisibility(isRubles ? View.GONE : View.VISIBLE);
        });

//        инициализация листа
        currencyList = new ArrayList<>();

//        при нажатии на кнопку происходит создание объекта и запуск потока на фоне
        binding.fetchDataButton.setOnClickListener(view -> {
//            поток можно запустить двумя способами: при помощи Thread и AsyncTask
//            new MyAsyncTask().execute();
            toUpdate = true;
            new FetchData().start();
        });
    }

    private void init() {
//        создание экземпляра адаптера для списка
        SpinnerAdapter myCustomAdapter = new SpinnerAdapter(this, R.layout.custom_spinnner_adapter, currencyList);
//        применение адаптера к spinner в разметке
        binding.currencyToConvert.setAdapter(myCustomAdapter);
//        создание обработчика выбора элемента для спиннера
        binding.currencyToConvert.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currencyToConvert = (Currency) parent.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

//        создание обработчика нажатия на кнопку информации о валюте и вывод информационного окна на экран
        binding.firstImageButton.setOnClickListener(view -> createNewInfoDialog(currencyToConvert));

//        всё то же самое для валюты, в которую происходит перевод
        binding.destinationCurrency.setAdapter(myCustomAdapter);
        binding.destinationCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                destinationCurrency = (Currency) parent.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.secondImageButton.setOnClickListener(view -> createNewInfoDialog(destinationCurrency));

        binding.convertButton.setOnClickListener(view -> {
            String str = String.valueOf(binding.textInput.getText());
            binding.resultField.setText(str.isEmpty() ? "¯\\_(ツ)_/¯" : isRubles ?
                    String.valueOf(Double.parseDouble(str) / (destinationCurrency.getValue()))
                    : String.valueOf(Double.parseDouble(str) / (destinationCurrency.getValue() / currencyToConvert.getValue())));
        });
    }

    //    функция записи данных в файл
    private void writeToFile(String data, File file) throws IOException {
        try (FileOutputStream stream = new FileOutputStream(file)) {
            stream.write(data.getBytes());
        }
    }

    //    функция чтения из файла
    private String readFromFile(File name) throws IOException {
        String data;
        FileInputStream inputStream = new FileInputStream(name);

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String receiveString;
        StringBuilder stringBuilder = new StringBuilder();

        while ((receiveString = bufferedReader.readLine()) != null)
            stringBuilder.append("\n").append(receiveString);

        inputStream.close();
        data = stringBuilder.toString();

        return data;
    }

    //    функция для вывода информационного окна на экран
    private void createNewInfoDialog(Currency currency) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View currencyPopupView = getLayoutInflater().inflate(R.layout.popup_info_window, null);
        TextView textView = currencyPopupView.findViewById(R.id.text_field);
        textView.setText(currency.toString());
        Button button = currencyPopupView.findViewById(R.id.understandable_button);
        dialogBuilder.setView(currencyPopupView);
        dialog = dialogBuilder.create();
        dialog.show();
        button.setOnClickListener(view -> dialog.dismiss());
    }

    private abstract class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                readJSON();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class FetchData extends Thread {
        @Override
        public void run() {
            try {
                readJSON();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void readJSON() throws IOException, JSONException {
//        вывод окна с загрузкой
        mainHandler.post(() -> {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Загрузка данных");
            progressDialog.setCancelable(false);
            progressDialog.show();
        });

        currencyList.clear();
        //создаём объект типа URL, в который помещается ссылка, откуда нужно прочитать JSON
        URL url = new URL("https://www.cbr-xml-daily.ru/daily_json.js");
        //открытие http-соединения
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //получаем данные при помощи inputStream
        InputStream stream = connection.getInputStream();
        //передаём InputStream в BufferedReader
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        StringBuilder data = new StringBuilder();
        //считывание всех строк и запись их в переменную data
        while ((line = reader.readLine()) != null)
            data.append(line);
        String message = "Обновите данные!";
//        обработка переменной data, создание JSON'a
        if (data.length() > 0) {
            File file = new File(this.getFilesDir(), "data.srl");
            if (file.isFile() && !toUpdate) {
                fillList(readFromFile(file));
                message = "Данные прочитаны из файла!";
            } else if (toUpdate) {
                writeToFile(String.valueOf(data), file);
                fillList(data.toString());
                message = "Данные обновлены!";
            }
        }

        String finalMsg = message;
        mainHandler.post(() -> {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            Toast.makeText(this, finalMsg, Toast.LENGTH_SHORT).show();
//            основная инициализация происходит только после чтения JSON'a
            init();
        });
    }

    //    функция для заполнения листа с валютами
    private void fillList(String data) throws JSONException {
        JSONObject jsonObject = new JSONObject(data).getJSONObject("Valute");
//        итератор для обхода ключей
        Iterator<String> keys = jsonObject.keys();
//        у каждого JSON-объекта есть ключ, происходит обход ключей и создание объектов типа Currency
        while (keys.hasNext()) {
            String key = keys.next();
            if (jsonObject.get(key) instanceof JSONObject) {
                JSONObject jsonObj = (JSONObject) jsonObject.get(key);
//                создание объекта Currency и помещение его в коллекцию
                currencyList.add(new Currency(
                        (String) jsonObj.get("ID"),
                        (String) jsonObj.get("NumCode"),
                        (String) jsonObj.get("CharCode"),
                        (int) jsonObj.get("Nominal"),
                        (String) jsonObj.get("Name"),
                        (double) jsonObj.get("Value"),
                        (double) jsonObj.get("Previous"))
                );
            }
        }
    }
}