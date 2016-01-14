package com.example.takoyaki.newshyboys;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ProfessorActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String roomcode;
    TextView room;
    SocketClient client;
    private String ip = "168.188.129.152";
//    private String ip = "168.188.128.130";
    private int port = 5000;
    private EditText code_edit;
    static ArrayAdapter<String> arrayAdapter;
    static ArrayList<String> arrayList = new ArrayList<String>();
    ListView listView;

    public Handler hMain = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    UI_change(); //MainActivity의 함수이다.
                    break;
                case 1:
                    duplication();
                    break;
                default:
                    break;
            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor);
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

        room = (TextView)findViewById(R.id.room_Text);
        Intent intent = getIntent();
        roomcode = intent.getExtras().getString("code");//roomcode 정보를 저장
        room.setText(roomcode);//룸코드를 textview로 출력

        listView = (ListView) findViewById( R.id.question_List);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter) ;
        Log.d("debug", "확인");
        //서버와 socket통신을 하기 위한 사전작업
        client = new SocketClient(ip,port, roomcode, "Professor",1,hMain);
        Log.d("debug", "확인2");
        client.start();
        Log.d("debug", "확인3");

        Log.d("debug", "확인4");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            arrayList.clear();
            arrayAdapter.notifyDataSetChanged();
            client.exitServer();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.professor, menu);
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

        if (id == R.id.code_change) {
            Context mContext = getApplicationContext();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.roomcode_popup,(ViewGroup) findViewById(R.id.h_room_popup));
            final android.app.AlertDialog.Builder aDialog = new android.app.AlertDialog.Builder(ProfessorActivity.this);


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
                    room.setText(code);
                    roomcode = code;
                    arrayList.clear();
                    arrayAdapter.notifyDataSetChanged();
                    client.exitServer();
                    client = new SocketClient(ip,port,roomcode,"Professor",1,hMain);
                    client.start();
                }
            });
            android.app.AlertDialog ad = aDialog.create();
            ad.show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void UI_change(){
        arrayAdapter.notifyDataSetChanged();
    }
    public void duplication(){
        Context mContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.duplication_popup,(ViewGroup) findViewById(R.id.duplicate_popup_view));
        final android.app.AlertDialog.Builder aDialog = new android.app.AlertDialog.Builder(ProfessorActivity.this);


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
                room.setText(code);
                arrayList.clear();
                arrayAdapter.notifyDataSetChanged();
                client = new SocketClient(ip,port,roomcode,"Professor",1,hMain);
                client.start();
            }
        });
        //팝업창 생성
        android.app.AlertDialog ad = aDialog.create();
        ad.show();//보여줌!
    }

}
