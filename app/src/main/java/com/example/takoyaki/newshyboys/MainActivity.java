package com.example.takoyaki.newshyboys;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;


import android.os.HandlerThread;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private String html = "";
    private HandlerThread mHandler;
    private String TAG = "socketDebug";

    private Socket socket;
    private String name;
    private BufferedReader networkReader;
    private BufferedWriter networkWriter;
    private String ip = "168.188.128.130";
    private int port = 5000;

    public EditText question;
    public ImageButton send;
    private MyTimerTask timerTask;
    private EditText code_edit;


    @Override
    protected void onStop() {
        super.onStop();
//        try {
//            socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("");
        mHandler = new HandlerThread("handler");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        question = (EditText) findViewById(R.id.question_Text);
        send = (ImageButton) findViewById(R.id.send_Button);

        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.anitest);
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String inputText = question.getText().toString();
                //Toast.makeText(MainActivity.this, inputText,Toast.LENGTH_SHORT).show();

                question.startAnimation(animation);
                Timer timer = new Timer();
                timerTask = new MyTimerTask();
                timer.schedule(timerTask, 500);
            }
        });
    }
        private class MyTimerTask extends TimerTask {
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    question.setText("");
                }
            });

        }
    };
        @Override
        public void onBackPressed() {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            // Handle navigation view item clicks here.
            int id = item.getItemId();


            if (id == R.id.Professor) {
                Context mContext = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

                View layout = inflater.inflate(R.layout.roomcode_popup,(ViewGroup) findViewById(R.id.h_room_popup));
                final android.app.AlertDialog.Builder aDialog = new android.app.AlertDialog.Builder(MainActivity.this);

                aDialog.setTitle("Room code"); //타이틀바 제목
                aDialog.setView(layout); //inti.xml 파일을 뷰로 셋팅
                aDialog.setCancelable(true);
                //그냥 닫기버튼을 위한 부분
                code_edit = (EditText) layout.findViewById(R.id.code_edittext);
                aDialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                aDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String code = code_edit.getText().toString();

                        Intent intent = new Intent(MainActivity.this, ProfessorActivity.class);
                        intent.putExtra("code", code);
                        startActivity(intent);
                    }
                });
                //팝업창 생성
                android.app.AlertDialog ad = aDialog.create();
                ad.show();//보여줌!

                // Handle the camera action
            } else if (id == R.id.Student) {

            }
//          else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        }
//        else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        public void setSocket(String ip, int port) throws IOException {
            try {
                socket = new Socket(ip, port);
                networkWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                networkReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}
