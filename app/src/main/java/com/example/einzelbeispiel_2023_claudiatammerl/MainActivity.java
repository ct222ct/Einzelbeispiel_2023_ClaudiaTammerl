package com.example.einzelbeispiel_2023_claudiatammerl;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    Button verbinden;
    Button berechnen_quersumme;
    EditText matrikelnummer;
    TextView ergebnis_berechnung;
    TextView serverAntwort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ergebnis_berechnung = (TextView) findViewById(R.id.ergebnis_textView3);
        matrikelnummer = (EditText)findViewById(R.id.editTextNumber);
        serverAntwort = (TextView) findViewById(R.id.serverAntwort);
        verbinden = (Button) findViewById(R.id.verbinden);
        berechnen_quersumme = (Button) findViewById(R.id.berechen);

        verbinden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new TCP_Client().execute();
            }
        });

        berechnen_quersumme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                int[] zahlenArray = new int[matrikelnummer.length()];

                for(int i=0;i<matrikelnummer.length();i++) {
                    zahlenArray[i] = Character.getNumericValue(matrikelnummer.getText().charAt(i));
                }
                ergebnis_berechnung.setText(getBerechnen_quersumme(zahlenArray));
            }
        });

    }

    public String getBerechnen_quersumme(int [] matrikelNr) {
        String gerade = "Die Quersumme ist gerade!";
        String ungerade = "Die Quersumme ist ungerade!";
        int summe = 0;
        for (int i = 0; i < matrikelNr.length; i++ ) {
            if (i % 2 == 0){
                summe = summe + matrikelNr[i];
            }else{
                summe = summe - matrikelNr[i];
            }
        }
        if(summe %2 == 0){
            return gerade;
        }else{
            return ungerade;
        }
    }


    class TCP_Client extends AsyncTask<Void, Void, Void> {

        String antwort;

        @Override
        protected Void doInBackground(Void... voids) {

            OutputStreamWriter outputStreamWriter = null;
            InputStreamReader inputStreamReader = null;
            BufferedReader bR = null;
            BufferedWriter bW = null;
            matrikelnummer = (EditText) findViewById(R.id.editTextNumber);

            Socket socket = null;

            try {
                socket = new Socket("se2-isys.aau.at",53212);

                inputStreamReader = new InputStreamReader(socket.getInputStream());
                outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

                bR = new BufferedReader(inputStreamReader);
                bW = new BufferedWriter(outputStreamWriter);

                bW.write(matrikelnummer.getText().toString());
                bW.newLine();
                bW.flush();

                antwort = bR.readLine();

                System.out.println("Server-Antwort: " + antwort);

            } catch (IOException e) {
                e.printStackTrace();
                antwort = "UnknownHostException: " + e.toString();

            } finally {
                try {
                    if(socket != null)
                        socket.close();

                    if(inputStreamReader != null)
                        inputStreamReader.close();

                    if(outputStreamWriter != null)
                        outputStreamWriter.close();

                    if(bR != null)
                        bR.close();

                    if(bW != null)
                        bW.close();

                } catch (IOException e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);

            serverAntwort.setText(antwort);
        }
    }
}

