package com.example.mariohernandez.supergeolocator;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Socket socket;

    private DatagramSocket socket2;

    private  int serverPort = 12345;

    private  String serverIP = "157.253.218.220";

    private Button btn = null;

    private String protocolo = "TCP";

    private InetAddress serverAddr;

    private Handler handler = new Handler();

    private  MyRun run = new MyRun();

    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;

    private String idd;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buildGoogleApiClient();

       final LocationService locc = new LocationService(getApplicationContext());

        mLastLocation = locc.darUbicacion();

        RadioGroup group = (RadioGroup) findViewById(R.id.radioGroup);

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioTCP) {
                    protocolo = "TCP";
                } else if (checkedId == R.id.radioUDP) {
                    protocolo = "UDP";
                }
            }
        });

        btn = (Button) findViewById(R.id.btnIniciar);



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mLastLocation = locc.darUbicacion();

                try {

                    if (btn.getText().toString().equals("Iniciar")) {

                        handler.postDelayed(run, 10);

                    } else {
                        handler.removeCallbacks(run, null);
                        btn.setText("Iniciar");
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), "Hubo un problema con la ubicación",Toast.LENGTH_SHORT).show();
                }



              /*  try {
                    Toast.makeText(getBaseContext(), "" + mLastLocation.getAltitude() + ":"+
                            mLastLocation.getSpeed(), Toast.LENGTH_LONG).show();
                }catch(Exception e){
                    Toast.makeText(getBaseContext(), "Localización no disponible", Toast.LENGTH_LONG).show();
                }
                */
            }
        });
    }

    @Override
    public void onConnected(Bundle connectionHint) {
       /* mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {

            Toast.makeText(getBaseContext(),String.valueOf(mLastLocation.getLatitude()),Toast.LENGTH_LONG).show();
             //       mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        }
        */
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    class ClientThread implements Runnable {

        @Override
        public void run() {

            try {

                serverAddr = InetAddress.getByName(serverIP);

                if(protocolo.equals("TCP")) {
                    socket = new Socket(serverAddr, serverPort);
                }else{
                    socket2 = new DatagramSocket();
                    String b = idd+","+mLastLocation.getLatitude()+","+mLastLocation.getLongitude()+","+
                            "2625.0"+","+mLastLocation.getSpeed();
                    byte[] buffer = b.getBytes();
                    DatagramPacket packet = new DatagramPacket(
                            buffer, buffer.length, serverAddr, serverPort);
                    socket2.send(packet);
                }

            } catch (UnknownHostException e1) {

                e1.printStackTrace();
            } catch (IOException e1) {

                e1.printStackTrace();

            }

        }

    }


    public class MyRun implements Runnable{

        @Override
        public void run() {

            serverIP = ((EditText) findViewById(R.id.ip)).getText().toString();
            String[] str = ((EditText) findViewById(R.id.port)).getText().toString().split(":");
            serverPort = Integer.parseInt(str[0]);
            idd = str[1];

            Thread thread = new Thread(new ClientThread());
            thread.start();

            btn.setText("Detener");
            while (thread.isAlive()) {

            }

            try {

                if (protocolo.equals("TCP")) {
                    PrintWriter out = new PrintWriter(new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream())),
                            true);
                    out.println(idd+","+mLastLocation.getLatitude()+","+mLastLocation.getLongitude()+","+
                            "2625.0"+","+mLastLocation.getSpeed());
                } else {

                }


            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();

            }

            handler.postDelayed(run, 1000);

        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

}
