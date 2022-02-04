package com.avdprog.exchangerates;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private ListView listValute;
    private ArrayAdapter<String> adapter;
    private String[] valutes;
    private List<ValuteData> res;
    private Spinner spinner;
    private TextView result;
    private String[] data;
    private EditText editTextNumberDecimal;
    private int currentSpinnerElement = 0;
    private Timer updateTimer;

    private long updateTime = 5; //задает период обновления списка валют в минутах
    private String jsonValute = "";
    private String filename = "jsonfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        spinner = (Spinner) findViewById(R.id.spinner);
        listValute = (ListView) findViewById(R.id.listValute);

        // читаем сохраненный json из файла
        String jsonFromFile = openFile(filename);

        if (jsonFromFile.equals("")) {
            //если файла нет - то получаем строку json с сайта
            jsonValute = getJsonString();
            updateListViewData(jsonValute);
        } else {
            // файл есть - используем его данные
            updateListViewData(jsonFromFile);
        }


        result = findViewById(R.id.result);

        // слушатель выбора элемента спинера
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                currentSpinnerElement = position;
                result.setText(calculate());

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });


        editTextNumberDecimal = findViewById(R.id.editTextNumberDecimal);
        editTextNumberDecimal.setText("1");


        // слушатель нажатия кнопки Done на клавиатуре
        editTextNumberDecimal.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);

                    //   result.setText(editTextNumberDecimal.getText());
                    result.setText(calculate());

                    return true;
                }
                return false;

            }
        });


        updateTimer = new Timer();

        // задача для периодического выполнения
        updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        jsonValute = getJsonString();
                        updateListViewData(jsonValute);
                    }
                });
            }
        }, 0, updateTime * 60 * 1000);


    }


    // чтение файла
    public String openFile(String fileName) {
        try {
            InputStream inputStream = openFileInput(fileName);

            if (inputStream != null) {
                InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(isr);
                String line;
                StringBuilder builder = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    builder.append(line + "\n");
                }

                inputStream.close();

                return builder.toString();

            }
        } catch (Throwable t) {
            t.printStackTrace();

        }
        return "";
    }

    // запись файла
    public void saveFile(String fileName, String jsonString) {
        try {
            OutputStream outputStream = openFileOutput(fileName, 0);
            OutputStreamWriter osw = new OutputStreamWriter(outputStream);
            osw.write(jsonString);
            osw.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    //обработка клика по кнопке Обновить
    public void clickUpdate(View view) {
        jsonValute = getJsonString();
        updateListViewData(jsonValute);
    }

    // получение строки с json
    public String getJsonString() {

        // получаем строку с json
        String jsonString = "";
        try {
            jsonString = URLReader.readUrl("https://www.cbr-xml-daily.ru/daily_json.js");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonString;


    }

    // установка данных для списка
    public void updateListViewData(String jsonString) {

        if (!jsonString.equals("")) {
            //парсим полученную строку json в коллекцию типа ValuteData
            res = WorkJson.parse(jsonString);

            // создаем массив строк с данными курсов валют и заполняем его
            valutes = new String[res.size()];

            for (int i = 0; i < res.size(); i++) {
                valutes[i] = res.get(i).getNominal() + " " + res.get(i).getName() + " = " + res.get(i).getValue() + " Руб.";
            }


            // создаем адаптер
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, valutes);

            // присваиваем адаптер списку
            listValute.setAdapter(adapter);


            setSpinnerData();

        } else {

            Toast toast = Toast.makeText(getApplicationContext(),
                    "Нет сети! Когда сеть появится нажмите кнопку Обновить", Toast.LENGTH_LONG);
            toast.show();


        }


    }

    // установка данных для элементов спинера
    public void setSpinnerData() {

        if (!(res == null)) {
            data = new String[res.size()];

            for (int i = 0; i < res.size(); i++) {
                data[i] = res.get(i).getCharCode();
            }

            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


            spinner.setAdapter(adapterSpinner);

        }
    }

    //конвертирование рублей по курсу
    public String calculate() {
        if (!(res == null)) {
            int index = 0;

            for (int i = 0; i < res.size(); i++) {
                if (data[currentSpinnerElement].equals(res.get(i).getCharCode())) {
                    index = i;
                    break;
                }
            }

            double result = Double.parseDouble(String.valueOf(editTextNumberDecimal.getText())) * Double.parseDouble(res.get(index).getNominal()) / Double.parseDouble(res.get(index).getValue());
            return String.format("%.4f", result);
        } else return "";
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (!jsonValute.equals("")) {
            //сохраняем файл с данными при закрытии
            saveFile(filename, jsonValute);
        }
    }


}

