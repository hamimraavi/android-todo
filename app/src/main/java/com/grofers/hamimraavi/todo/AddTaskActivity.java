package com.grofers.hamimraavi.todo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.util.Calendar;
import java.util.TimeZone;

public class AddTaskActivity extends AppCompatActivity {

    //private Calendar calendar;
    private EditText etDescription;
    private TextView dateView, timeView;
    private Switch switchDate, switchTime;
    private Button bSetDate, bSetTime, bAddTask;
    private int year, month, day, hour, min;
    private String token;
    private Boolean FLAG_TIME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            token = extras.getString("token");
        }

        etDescription = (EditText) findViewById(R.id.etDescription);

        dateView = (TextView) findViewById(R.id.tvDate);
        timeView = (TextView) findViewById(R.id.tvTime);

        switchDate = (Switch) findViewById(R.id.switchDate);
        switchTime = (Switch) findViewById(R.id.switchTime);
        switchDate.setChecked(false);
        switchTime.setChecked(false);
        switchTime.setOnCheckedChangeListener(null);

        bAddTask = (Button) findViewById(R.id.bAddTask);
        bSetDate = (Button) findViewById(R.id.bSetDate);
        bSetTime = (Button) findViewById(R.id.bSetTime);
        bSetDate.setClickable(false);
        bSetTime.setClickable(false);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT")); //offset US to GMT
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
        showTime(hour, min);
        Log.d("year", ""+year);
        Log.d("month", ""+month);
        Log.d("day", ""+day);
        Log.d("hour", ""+hour);
        Log.d("minute", ""+min);

        FLAG_TIME = false;

        switchDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    bSetDate.setClickable(true);
                    FLAG_TIME = true;
                }
                else {
                    bSetDate.setClickable(false);
                    bSetTime.setClickable(false);
                    FLAG_TIME = false;
                }
            }
        });

        switchTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked && FLAG_TIME) {
                    bSetTime.setClickable(true);
                }
                else {
                    bSetTime.setClickable(false);
                }
            }
        });

        bAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String description = etDescription.getText().toString();
                if(description.equals(""))
                    Toast.makeText(getApplicationContext(), "Description field cannot be empty", Toast.LENGTH_SHORT).show();
                else{
                    new MyAsyncTask().execute();
                }
            }
        });
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }

        if (id == 222) {
            return new TimePickerDialog(this, myTimeListener, hour, min, true);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2+1, arg3);
        }
    };

    private void showDate(int myyear, int mymonth, int myday) {

        StringBuilder sb = new StringBuilder();
        sb.append(myyear).append('-');
        if(mymonth<10)
            sb.append("0");
        sb.append(mymonth).append('-');
        if(myday<10)
            sb.append("0");
        sb.append(myday);
        String date = sb.toString();
        //dateView.setText(new StringBuilder().append(year).append("-")
          //      .append(month).append("-").append(day));
        if(myday<day || mymonth<month || myyear<year)
            Toast.makeText(getApplicationContext(), "Please choose a valid date", Toast.LENGTH_SHORT);
        else
            dateView.setText(date);

    }

    @SuppressWarnings("deprecation")
    public void setTime(View view) {
        showDialog(222);

        //int hour = timePicker1.getCurrentHour();
        //int min = timePicker1.getCurrentMinute();
        //showTime(hour, min);
    }

    private TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker arg0, int arg1, int arg2) {
            // TODO Auto-generated method stub
            // arg1 = hour
            // arg2 = minutes
            showTime(arg1, arg2);
        }
    };

    public void showTime(int myhour, int mymin) {
        StringBuilder sb = new StringBuilder();

        if(myhour<10)
            sb.append("0");
        sb.append(myhour).append(':');
        if(mymin<10)
            sb.append("0");
        sb.append(mymin);
        String time = sb.toString();
        //String time = new StringBuilder().append(hour).append(':')
          //      .append(min).toString();

        timeView.setText(time);
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            // TODO Auto-generated method stub
            JSONObject myJson = postData();
            return myJson;
        }

        protected void onPostExecute(JSONObject myJson) {
            JSONObject jsonStatus = null;

            try {
                jsonStatus = myJson.getJSONObject("status");
                Boolean status =jsonStatus.getBoolean("success");
                String message = jsonStatus.getString("message");
                if(status == false){
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(AddTaskActivity.this, TaskActivity.class);
//                    intent.putExtra("token", token);
//                    startActivity(intent);
                    onPause();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        protected void onProgressUpdate(Integer... progress) {

        }

        public JSONObject postData() {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://10.0.2.2:8000/api/v1/tasks/");

            JSONObject myJson = null;

            try {
                // Add your data
                String description = etDescription.getText().toString();
                StringBuilder sb = new StringBuilder();
                sb.append(dateView.getText().toString()).append('T');
                sb.append(timeView.getText().toString()).append(":00.000000Z");
                String scheduled_time = sb.toString();
                JSONObject json = new JSONObject();
                json.put("description", description);
                json.put("scheduled_time", scheduled_time);
                StringEntity se = new StringEntity( json.toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httppost.setEntity(se);

                // Execute HTTP Post Request
                httppost.setHeader("Authorization", "Token " + token);
                HttpResponse response = httpclient.execute(httppost);

                String temp = EntityUtils.toString(response.getEntity());
                Log.i("tag", temp);
                myJson = new JSONObject(temp);


            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            } catch (Exception e) {
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
