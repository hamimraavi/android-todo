package com.grofers.hamimraavi.todo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
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
import org.apache.http.client.methods.HttpPut;
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

public class UpdateDeleteActivity extends AppCompatActivity {

    //private Calendar calendar;
    private EditText etDescription;
    private TextView dateView, timeView;
    private Switch switchDate, switchTime, switchPending;
    private Button bSetDate, bSetTime, bUpdateTask, bDeleteTask;
    private int year, month, day, hour, min, id;
    private String token, task_str, description, scheduled_time, pending_str;
    private Boolean pending, is_deleted=false;
    private Boolean FLAG_TIME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delete);

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            token = extras.getString("token");
            task_str = extras.getString("id");
        }

        try {
            JSONObject task = new JSONObject(task_str);
            id = task.getInt("id");
            description = task.getString("description");
            scheduled_time = task.getString("scheduled_time");
            pending_str = task.getString("pending");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        etDescription = (EditText) findViewById(R.id.etuDescription);
        etDescription.setText(description);

        dateView = (TextView) findViewById(R.id.tvuDate);
        timeView = (TextView) findViewById(R.id.tvuTime);
        dateView.setText(scheduled_time.substring(0,10));
        timeView.setText(scheduled_time.substring(11,16));

        switchDate = (Switch) findViewById(R.id.switchuDate);
        switchTime = (Switch) findViewById(R.id.switchuTime);
        switchPending = (Switch) findViewById(R.id.switchuPending);
        switchDate.setChecked(false);
        switchTime.setChecked(false);
        switchTime.setOnCheckedChangeListener(null);

        bUpdateTask = (Button) findViewById(R.id.buUpdateTask);
        bDeleteTask = (Button) findViewById(R.id.buDeleteTask);
        bSetDate = (Button) findViewById(R.id.buSetDate);
        bSetTime = (Button) findViewById(R.id.buSetTime);
        bSetDate.setClickable(false);
        bSetTime.setClickable(false);

        FLAG_TIME = false;

        if(pending_str.equals("false")) {
            pending = false;
            switchPending.setChecked(false);
        }
        else {
            pending = true;
            switchPending.setChecked(true);
        }

        Log.d("pending", ""+pending);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT")); //offset US to GMT
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        //showDate(year, month+1, day);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
        //showTime(hour, min);
        Log.d("year", ""+year);
        Log.d("month", ""+month);
        Log.d("day", ""+day);
        Log.d("hour", ""+hour);
        Log.d("minute", ""+min);

        switchPending.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                    pending = true;
                else
                    pending = false;
            }
        });

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

        bUpdateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String description = etDescription.getText().toString();
                if(description.equals(""))
                    Toast.makeText(getApplicationContext(), "Description field cannot be empty", Toast.LENGTH_SHORT).show();
                else{
                    new MyAsyncTaskForUpdate().execute();
                }
            }
        });

        bDeleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                is_deleted = true;
                new MyAsyncTaskForUpdate().execute();

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
        showTime(hour, min);
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

    private class MyAsyncTaskForUpdate extends AsyncTask<String, Integer, JSONObject> {

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
                    if(is_deleted){
                        Toast.makeText(getApplicationContext(), "Task deleted successfully", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                    onPause();
                    //Intent intent = new Intent(UpdateDeleteActivity.this, TaskActivity.class);
                    //intent.putExtra("token", token);
                    //startActivity(intent);
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
            HttpPut httpput = new HttpPut("http://10.0.2.2:8000/api/v1/tasks/"+id+"/");

            JSONObject myJson = null;

            try {
                // Add your data
                String description = etDescription.getText().toString();
                StringBuilder sb = new StringBuilder();
                sb.append(dateView.getText().toString()).append('T');
                sb.append(timeView.getText().toString()).append(":00.000000Z");
                String scheduled_time = sb.toString();
                Log.d("desc", description);
                Log.d("time", scheduled_time);
                JSONObject json = new JSONObject();
                json.put("description", description);
                json.put("scheduled_time", scheduled_time);
                json.put("pending", pending);
                json.put("is_deleted", is_deleted);
                StringEntity se = new StringEntity(json.toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httpput.setEntity(se);

                // Execute HTTP Post Request
                httpput.setHeader("Authorization", "Token " + token);
                HttpResponse response = httpclient.execute(httpput);

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
