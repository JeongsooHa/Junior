package com.example.takoyaki.newshyboys;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ProfessorActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String roomcode;
    TextView room;
    SocketClient client;
 //   private String ip = "168.188.129.152";
    private String ip = "168.188.128.130";
    private int port = 5000;
    private EditText code_edit;
    static ArrayAdapter<String> arrayAdapter;
    static ArrayList<String> arrayList = new ArrayList<String>();
    ListView listView;
    String state;
    File path;    //저장 데이터가 존재하는 디렉토리경로
    File file;     //파일명까지 포함한 경로

    public Handler hMain = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    UI_change(); //MainActivity의 함수이다.
                    break;
                case 1:
                    duplication();

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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        state = Environment.getExternalStorageState();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long now = System.currentTimeMillis();

                Date date = new Date(now);
                SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String strCurDate = CurDateFormat.format(date);
                String filename = strCurDate+="-"+roomcode;
                if (!state.equals(Environment.MEDIA_MOUNTED)) {
                    Toast.makeText(ProfessorActivity.this, "SDcard Not Mounted", Toast.LENGTH_SHORT).show();
                    return;
                }
                path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                file = new File(path,filename); //파일명까지 포함함 경로의 File 객체 생성

                try {
                    //데이터 추가가 가능한 파일 작성자(FileWriter 객체생성)
                    FileWriter wr = new FileWriter(file, true); //두번째 파라미터 true: 기존파일에 추가할지 여부를 나타냅니다.
                    PrintWriter writer = new PrintWriter(wr);
                    String content="";

                    for(int i = 0 ; i < arrayList.size();i++){
                        content += arrayList.get(i).toString();
                        Log.d("debughmm",content);
                    }


                    writer.println(content);
                    writer.close();


                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                // File객체로부터 Uri값 생성
                final Uri fileUri = Uri.fromFile(file);

                Intent it = new Intent(Intent.ACTION_SEND);
                it.setType("plain/text");

                // 수신인 주소 - tos배열의 값을 늘릴 경우 다수의 수신자에게 발송됨
                String[] tos = {""};
                it.putExtra(Intent.EXTRA_EMAIL, tos);
                it.putExtra(Intent.EXTRA_SUBJECT,strCurDate+" 질문");
                it.putExtra(Intent.EXTRA_TEXT, "룸코드 : "+roomcode);

                // 파일첨부
                it.putExtra(Intent.EXTRA_STREAM,fileUri);

                startActivity(it);
            }
        });

        room = (TextView)findViewById(R.id.room_Text);
        Intent intent = getIntent();
        roomcode = intent.getExtras().getString("code");//roomcode 정보를 저장
        room.setText(roomcode);//룸코드를 textview로 출력


        arrayAdapter = new ArrayAdapter<String>(this, R.layout.professor_listview_row, arrayList);
        listView = (ListView) findViewById( R.id.question_List);
        listView.setAdapter(arrayAdapter);
        Log.d("debug", "확인");
        //서버와 socket통신을 하기 위한 사전작업
        client = new SocketClient(ip,port, roomcode, "Professor",1,hMain);
        Log.d("debug", "확인2");
        client.start();

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
        Log.d("debug", "리시브 스레드6");
        arrayAdapter.notifyDataSetChanged();
        Log.d("debug", "리시브 스레드7");
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
