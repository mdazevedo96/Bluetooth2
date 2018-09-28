package com.example.marcelo.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    boolean conexao = false;
   private static final int requestBt = 1;
   private static final int discover = 2;
   private static final int requestConection = 3;
   private static String MAC = null;
   UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //CONEXAO DIRETA
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDevice = null;
    private BluetoothSocket mSocket = null;
    ConnectedThread connectedThread;

    Button btnConexao;
    Button btnEnviar;
    //Button btnSair;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //botoes
        btnConexao = (Button)findViewById(R.id.btnConectar);
        btnEnviar = (Button)findViewById(R.id.btnEnviar);
       // btnSair = (Button)findViewById(R.id.btnSair);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "O dispositivo não possui bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        }
        else if (!mBluetoothAdapter.isEnabled()) {
             Intent AtivarBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
             startActivityForResult(AtivarBT, requestBt);

        }

        // ao clicar no btnConexao
        btnConexao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conexao) {

                    //desconectar
                    //pode acontecer erros
                    try {
                        mSocket.close();
                        conexao = false;
                        btnConexao.setText("Conectar");

                        Toast.makeText(getApplicationContext(),"Conexao desconectada",Toast.LENGTH_LONG).show();

                    } catch (IOException erro) {


                    }
                }
                else {
                    //conectar
                    //abrir lista
                    Intent abreLista = new Intent (MainActivity.this,ListaDispositivos.class);
                    startActivityForResult(abreLista, requestConection);

                  //DEVERIA FUNCIONAR AQUI
                   // connectedThread.write("S");
                }

            }
        });

        //Enviar mensagem
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (conexao == true) {

                   //Criar hash MD5
                 /*  MessageDigest algoritmo = null;

                   try {
                       algoritmo = MessageDigest.getInstance("MD5");
                   } catch (NoSuchAlgorithmException e) {
                       e.printStackTrace();
                   }
                   try {
                       byte messageDigest [] = algoritmo.digest("senha".getBytes("UTF-8"));

                       } catch (UnsupportedEncodingException e) {
                       e.printStackTrace();

                       StringBuilder hexString = new StringBuilder();
                       for (byte b: messageDigest) {
                           hexString.append(String.format("%02X",0xFF & b));
                   }
                   String senha = hexString.toString();
                   }

*/


                   //LIGA a sinaleira
                   connectedThread.write("S");
               }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode) {
            //ativar bluetooth
            case requestBt:
                if(resultCode == Activity.RESULT_OK) {
                    Toast.makeText(getApplicationContext(),"Bluetooth ativado",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"A aplicação será encerrada",Toast.LENGTH_LONG).show();
                    finish();
                }
                break;

            case requestConection:
                if(resultCode == RESULT_OK) {
                    MAC = data.getExtras().getString(ListaDispositivos.ENDERECO_MAC);

                    Toast.makeText(getApplicationContext(),"MAC:" + MAC,Toast.LENGTH_LONG).show();

                    mDevice = mBluetoothAdapter.getRemoteDevice(MAC);

                    try {
                        mSocket = mDevice.createRfcommSocketToServiceRecord(mUUID);

                        mSocket.connect();
                        conexao = true;

                        //instancia connectThread
                        connectedThread = new ConnectedThread(mSocket);
                        connectedThread.start();

                        //FUNCIONA !!!
                       // connectedThread.write("S");

                        btnConexao.setText("Desconectar");

                        Toast.makeText(getApplicationContext(),"Conectado com:" + MAC,Toast.LENGTH_LONG).show();

                    } catch (IOException erro) {
                        Toast.makeText(getApplicationContext(),"Erro:" + erro,Toast.LENGTH_LONG).show();
                        conexao = false;
                        btnConexao.setText("Conectar");
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(),"Falha ao obter o MAC",Toast.LENGTH_LONG).show();
                }

        }
    }




    /// ENVIAR DADOS ///

    private class ConnectedThread extends Thread {

        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        // RECEBE DADOS DO ARDUINO

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                   // mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                     //       .sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String dadosEnviar ) {

            //transforma dados em bytes
            byte[] msgBuffer = dadosEnviar.getBytes();
            try {
               //dados a serem enviados
                mmOutStream.write(msgBuffer);
            } catch (IOException e) { }
        }


    }

}
