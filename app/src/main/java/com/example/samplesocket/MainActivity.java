package com.example.samplesocket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class MainActivity extends AppCompatActivity {

    EditText editText;

    TextView textView;
    TextView textView2;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String data = editText.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        send(data);
                    }
                }).start();
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startServer();
                    }
                }).start();
            }
        });
    }

    public void printClientLog(final String data){
        Log.d("MainActivity", data);

        handler.post(new Runnable() {
            @Override
            public void run() {
                textView.append(data + "\n");
            }
        });
    }

    public void printServerLog(final String data){
        Log.d("MainActivity", data);

        handler.post(new Runnable() {
            @Override
            public void run() {
                textView2.append(data + "\n");
            }
        });
    }

    public void send(String data){
        try{
            int portNumber = 5001;
            Socket sock = new Socket("localhost", portNumber);
            //Socket객체 생성(클라이언트)

            printClientLog("소켓 연결함");

            ObjectOutputStream outputStream = new ObjectOutputStream(sock.getOutputStream());
            //문자열 객체를 그대로 사용하기 위해 + 아웃풋 스트림 객체 획득
            outputStream.writeObject(data);
            //쓰기 작업 및 전달
            outputStream.flush();
            //비움
            printClientLog("데이터 전송함");

            ObjectInputStream inputStream = new ObjectInputStream(sock.getInputStream());
            //문자열 객체를 그대로 얻기 위해
            printClientLog("서버로부터 받음 : " + inputStream.readObject());

            sock.close(); //소켓을 닫음
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startServer(){
        try{
           int portNumber = 5001;

            ServerSocket server = new ServerSocket(portNumber);
            //소켓 서버 객체 생성(서버)
            printServerLog("서버 시작함 : " + portNumber);

            while(true){
                Socket sock = server.accept();
                //소켓 객체를 반환받아 클라이언트 소켓의 연결 정보 확인 가능
                InetAddress clientHost = sock.getLocalAddress();
                //클라이언트 소켓의 IP주소 획득(InetAddress타입으로)
                int clientPort = sock.getPort();
                //클라이언트 소켓의 포트 획득
                printServerLog("클라이언트 연결됨 : " + clientHost + " : " + clientPort);

                ObjectInputStream inputStream = new ObjectInputStream(sock.getInputStream());
                Object obj = inputStream.readObject();
                //(데이터)객체를 얻음
                printServerLog("데이터 받음 : " + obj);

                ObjectOutputStream outputStream = new ObjectOutputStream(sock.getOutputStream());
                outputStream.writeObject(obj + "from Server");
                outputStream.flush();
                printServerLog("데이터 보냄");

                sock.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}