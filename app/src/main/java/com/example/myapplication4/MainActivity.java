package com.example.myapplication4;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.*;

import android.os.Bundle;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private Button button2;
    private TextInputEditText GPT_ke_fieldy;
    private String GPT_key;
    private boolean Key_not_entered = true;
    private TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("TAG", "App starting");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        button2 = findViewById(R.id.button2);
        GPT_ke_fieldy = findViewById(R.id.GPT_key_field);
        textView2 = findViewById(R.id.textView2);

        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                GPT_key = GPT_ke_fieldy.getText().toString();

                JSONObject postData = new JSONObject();
                try {
                    postData.put("model", "gpt-3.5-turbo");

                    JSONObject temp1 = new JSONObject();
                    temp1.put("role", "system");
                    temp1.put("content", "You are a helpful assistant.");

                    JSONObject temp2 = new JSONObject();
                    temp2.put("role", "user");
                    temp2.put("content", "Hello!");

                    JSONArray arr = new JSONArray();
                    arr.put(temp1);
                    arr.put(temp2);

                    postData.put("messages", arr);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //sendPost();
                new SendDeviceDetails().execute("https://api.openai.com/v1/chat/completions", postData.toString());

                Log.e("TAG", "App start ended");

            }});
    }

    private class SendDeviceDetails extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            String data = "";

            HttpURLConnection httpURLConnection = null;
            try {

                URL url = new URL(params[0]);

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.addRequestProperty("Content-Type", "application/json");
                httpURLConnection.addRequestProperty("Authorization", "Bearer " + GPT_key);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                DataOutputStream os = new DataOutputStream(httpURLConnection.getOutputStream());
                os.writeBytes(params[1]);

                httpURLConnection.connect();

                Log.e("TAG", httpURLConnection.getResponseMessage());
                Log.e("TAG", "" + httpURLConnection.getResponseCode());
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data += current;
                }
            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }



            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("TAG", result);


            if(result.isEmpty() == false && Key_not_entered)
            {
                button2.removeCallbacks(null);
                //GPT_ke_fieldy.setHint("Enter a message");
                button2.setText("Send");

                button2.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){

                        textView2.setText(textView2.getText().toString() + "You: " + GPT_ke_fieldy.getText().toString() + "\n");

                        JSONObject postData = new JSONObject();
                        try {
                            postData.put("model", "gpt-3.5-turbo");

                            JSONObject temp1 = new JSONObject();
                            temp1.put("role", "system");
                            temp1.put("content", "You are a helpful assistant.");

                            JSONObject temp2 = new JSONObject();
                            temp2.put("role", "user");
                            temp2.put("content", GPT_ke_fieldy.getText().toString());

                            JSONArray arr = new JSONArray();
                            arr.put(temp1);
                            arr.put(temp2);

                            postData.put("messages", arr);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //sendPost();
                        new SendDeviceDetails().execute("https://api.openai.com/v1/chat/completions", postData.toString());

                        Log.e("TAG", "App start ended");

                    }


                });
                JSONObject obj = null;
                try {
                    obj = new JSONObject(result);
                    JSONArray temp22 = (JSONArray) obj.getJSONArray("choices");
                    JSONObject temp44 =  temp22.getJSONObject(0).getJSONObject("message");
                    String response = temp44.getString("content");


                    textView2.setText(textView2.getText().toString() + "GPT: " + response + "\n");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                Key_not_entered = false;
                GPT_ke_fieldy.setHint("Enter a message");
            }
            else if(result.isEmpty() == false && (Key_not_entered == false))
            {
                JSONObject obj = null;
                try {
                    obj = new JSONObject(result);
                    JSONArray temp22 = (JSONArray) obj.getJSONArray("choices");
                    JSONObject temp44 =  temp22.getJSONObject(0).getJSONObject("message");
                    String response = temp44.getString("content");

                    textView2.setText(textView2.getText().toString() + "GPT: " + response + "\n");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
            GPT_ke_fieldy.setText("");
        }
    }


}
