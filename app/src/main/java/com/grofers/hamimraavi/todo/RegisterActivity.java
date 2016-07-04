package com.grofers.hamimraavi.todo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    EditText etEmail, etPassword, etFirstName, etLastName;
    Button bRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = (EditText) findViewById(R.id.etrEmail);
        etPassword = (EditText) findViewById(R.id.etrPassword);
        etFirstName = (EditText) findViewById(R.id.etrFirstName);
        etLastName = (EditText) findViewById(R.id.etrLastName);

        bRegister = (Button) findViewById(R.id.brRegister);

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MyAsyncTask().execute();
            }
        });
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            // TODO Auto-generated method stub
            JSONObject myJson = postData();

            return myJson;
        }

        protected void onPostExecute(JSONObject myJson){
            JSONObject jsonStatus = null;
            try {
                jsonStatus = myJson.getJSONObject("status");
                Boolean status =jsonStatus.getBoolean("success");
                String message = jsonStatus.getString("message");
                if(status == false){
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        protected void onProgressUpdate(Integer... progress){

        }

        public JSONObject postData() {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://10.0.2.2:8000/api/v1/users/");

            JSONObject myJson = null;

            try {
                // Add your data
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String first_name = etFirstName.getText().toString();
                String last_name = etLastName.getText().toString();

                JSONObject json = new JSONObject();
                json.put("username", email);
                json.put("password", password);
                json.put("first_name", first_name);
                json.put("last_name", last_name);
                StringEntity se = new StringEntity( json.toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httppost.setEntity(se);

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                String temp = EntityUtils.toString(response.getEntity());
                myJson = new JSONObject(temp);


            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
            catch(Exception e) {
                e.printStackTrace();
                //createDialog("Error", "Cannot Estabilish Connection");
            }
            return myJson;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
