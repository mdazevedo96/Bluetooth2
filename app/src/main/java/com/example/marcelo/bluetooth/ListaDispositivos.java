package com.example.marcelo.bluetooth;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class ListaDispositivos extends ListActivity {

    BluetoothAdapter mBluetoothAdapter2 = null;
    static String ENDERECO_MAC = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     ArrayAdapter<String> ArrayBluetooth = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

     mBluetoothAdapter2 = BluetoothAdapter.getDefaultAdapter();

     //dispositivos pareados
        Set<BluetoothDevice> dispositivosPareados = mBluetoothAdapter2.getBondedDevices();

        if(dispositivosPareados.size() > 0) {
            for (BluetoothDevice dispositivo : dispositivosPareados) {
                String nomeBT = dispositivo.getName();
                String macBT = dispositivo.getAddress();

                ArrayBluetooth.add(nomeBT + "\n" + macBT);
            }
        }
        setListAdapter(ArrayBluetooth);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String infoGeral = ((TextView)v).getText().toString();

        //Toast.makeText(getApplicationContext(),infoGeral,Toast.LENGTH_LONG).show();

        String endMAC = infoGeral.substring(infoGeral.length()-17);
        //Toast.makeText(getApplicationContext(),endMAC,Toast.LENGTH_LONG).show();

        Intent retornaMac = new Intent();
        retornaMac.putExtra(ENDERECO_MAC,endMAC);

        setResult(RESULT_OK,retornaMac);
        finish();
    }
}
