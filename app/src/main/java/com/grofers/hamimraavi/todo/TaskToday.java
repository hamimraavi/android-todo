package com.grofers.hamimraavi.todo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TaskToday extends AppCompatActivity {
    private List<Task> taskList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TaskAdapter mAdapter;
    private Button addNewTask;
    private String token;
    private List<JSONObject> hash = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            token = extras.getString("token");
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        addNewTask = (Button) findViewById(R.id.bAddNewTask);

        mAdapter = new TaskAdapter(taskList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        addNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TaskToday.this, AddTaskActivity.class);
                intent.putExtra("token", token);
                startActivity(intent);
            }
        });

        final GestureDetector mGestureDetector = new GestureDetector(TaskToday.this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());


                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {

                    int pos = recyclerView.getChildLayoutPosition(child);
                    Intent intent = new Intent(TaskToday.this, UpdateDeleteActivity.class);
                    intent.putExtra("token", token);
                    intent.putExtra("id", hash.get(pos).toString());
                    Log.d("content", hash.get(pos).toString());
                    startActivity(intent);
                    //Toast.makeText(TaskToday.this, ""+hash.get(pos), Toast.LENGTH_SHORT).show();
                    return true;

                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        new MyAsyncTask().execute();
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
                Boolean status = jsonStatus.getBoolean("success");

                if (status == false) {
                    String message = jsonStatus.getString("message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                } else {
                    JSONObject jsonData = myJson.getJSONObject("data");
                    JSONArray tasks = jsonData.getJSONArray("tasks");
                    Task newTask;
                    taskList.clear();
                    hash.clear();
                    for (int i = 0; i < tasks.length(); i++) {
                        JSONObject task = tasks.getJSONObject(i);
                        String id_str = task.getString("id");
                        String description = task.getString("description");
                        String time = task.getString("scheduled_time");
                        Boolean pending = task.getBoolean("pending");
                        int id = Integer.parseInt(id_str);
                        Log.d("pending today", "" + pending);
                        newTask = new Task(id, description, time, pending);
                        taskList.add(newTask);
                        hash.add(task);
                    }

                    mAdapter.notifyDataSetChanged();
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
            HttpGet httpget = new HttpGet("http://10.0.2.2:8000/api/v1/tasks/?days=0");

            JSONObject myJson = null;

            try {
                // Execute HTTP Post Request
                httpget.setHeader("Authorization", "Token " + token);
                HttpResponse response = httpclient.execute(httpget);

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
    }  // Async Task

    private class MyAsyncTaskForLogout extends AsyncTask<String, Integer, JSONObject> {

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
                Boolean status = jsonStatus.getBoolean("success");
                String message = jsonStatus.getString("message");
                if (status == false) {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    finish();
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
            HttpPost httppost = new HttpPost("http://10.0.2.2:8000/api/v1/users/session/logout/");

            JSONObject myJson = null;

            try {
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
    }  // Async Task



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            new MyAsyncTaskForLogout().execute();
            return true;
        }

        else if(id == R.id.action_today){
            return true;
        }

        else if(id == R.id.action_week){
            Intent i = new Intent(TaskToday.this, TaskWeek.class);
            i.putExtra("token", token);
            startActivity(i);
            finish();
            return true;
        }

        else if(id == R.id.action_all){
            Intent i = new Intent(TaskToday.this, TaskActivity.class);
            i.putExtra("token", token);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
