package com.example.marcelo.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    boolean conexao = false;
   private static final int requestBt = 1;
   private static final int discover = 2;
   private static final int requestConection = 3;
   private static String MAC = null;
    private BluetoothAdapter mBluetoothAdapter;

    Button btnConexao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //botoes
        btnConexao = (Button)findViewById(R.id.btnConectar);


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
                }
                else {
                    //conectar
                    //abrir lista
                    Intent abreLista = new Intent (MainActivity.this,ListaDispositivos.class);
                    startActivityForResult(abreLista, requestConection);
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

                }
                else {
                    Toast.makeText(getApplicationContext(),"Falha ao obter o MAC",Toast.LENGTH_LONG).show();
                }

        }
    }

}
