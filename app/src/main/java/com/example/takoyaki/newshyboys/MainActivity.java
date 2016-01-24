package com.example.takoyaki.newshyboys;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;


import android.os.HandlerThread;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private SocketClient client;
    //private String ip = "168.188.129.152";
    private String ip = "168.188.128.130";
    private int port = 5000;
    String roomcode=null;
    public EditText question;
    public ImageButton send;
    private MyTimerTask timerTask;
    private EditText code_edit;
    Handler hMain=null;
    private TextView main_textView;


    @Override
    protected void onStop() {
        super.onStop();
        //client.exitServer();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        main_textView = (TextView) findViewById(R.id.lvMessageList);
        question = (EditText) findViewById(R.id.question_Text);
        send = (ImageButton) findViewById(R.id.send_Button);


        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.anitest);
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String inputText = question.getText().toString();
                //Toast.makeText(MainActivity.this, inputText,Toast.LENGTH_SHORT).show();

                if (roomcode == null) {
                    Context mContext = getApplicationContext();
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

                    View layout = inflater.inflate(R.layout.roomcode_popup, (ViewGroup) findViewById(R.id.h_room_popup));
                    final android.app.AlertDialog.Builder aDialog = new android.app.AlertDialog.Builder(MainActivity.this);

                    aDialog.setView(layout); //inti.xml 파일을 뷰로 셋팅
                    aDialog.setCancelable(true);

                    code_edit = (EditText) layout.findViewById(R.id.code_edittext);
                    aDialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    aDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String code = code_edit.getText().toString();
                            roomcode = code;
                        }
                    });
                    android.app.AlertDialog ad = aDialog.create();
                    ad.show();
                } else {
                    question.startAnimation(animation);
                    Timer timer = new Timer();
                    timerTask = new MyTimerTask();
                    timer.schedule(timerTask, 500);
                    //connect socket and send message to server
                    client = new SocketClient(ip, port, roomcode, inputText, 0,hMain);
                    client.start();
//                sendthread = new SendThread(client.socket,inputText,0);
//                sendthread.start();
                }
            }
        });
        question.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                main_textView.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(question.getText().toString().equals("")){
                    main_textView.setText("질문을 입력하세요");
                }
            }
        });
//        question.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus == true) {
//                    myScrollView.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            myScrollView.smoothScrollBy(0, 800);
//                        }
//                    }, 100);
//                }
//            }
//        });
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
            } else if (id == R.id.code_change) { //룸코드 바꿀 시

                Context mContext = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

                View layout = inflater.inflate(R.layout.roomcode_popup,(ViewGroup) findViewById(R.id.h_room_popup));
                final android.app.AlertDialog.Builder aDialog = new android.app.AlertDialog.Builder(MainActivity.this);

                aDialog.setView(layout); //inti.xml 파일을 뷰로 셋팅
                aDialog.setCancelable(true);

                code_edit = (EditText) layout.findViewById(R.id.code_edittext);
                aDialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                aDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String code = code_edit.getText().toString();
                        roomcode = code;
                    }
                });
                android.app.AlertDialog ad = aDialog.create();
                ad.show();
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
}
