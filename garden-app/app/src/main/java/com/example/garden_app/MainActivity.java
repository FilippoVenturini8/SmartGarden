package com.example.garden_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.garden_app.utils.C;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.UUID;

import btlib.BluetoothChannel;
import btlib.BluetoothUtils;
import btlib.CommChannel;
import btlib.ConnectToBluetoothServerTask;
import btlib.ConnectionTask;
import btlib.RealBluetoothChannel;
import btlib.exceptions.BluetoothDeviceNotFound;

public class MainActivity extends AppCompatActivity {
    private BluetoothChannel btChannel;

    TextView txtLed3;
    TextView txtLed4;
    TextView txtIrrigationSpeed;
    Button connectBtn;

    private String led1 = "0";
    private String led2 = "0";
    private String led3 = "0";
    private String led4 = "0";
    private String openIrrigation = "0";
    private String irrigationSpeed = "0";
    private String modality;

    private boolean openedConnection = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        txtLed3 = findViewById(R.id.led3_value);
        txtLed4 = findViewById(R.id.led4_value);
        txtIrrigationSpeed = findViewById(R.id.irrigation_speed);
        connectBtn = findViewById(R.id.connectBtn);

        if(btAdapter != null && !btAdapter.isEnabled()) {
            startActivityForResult(
                    new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                    C.bluetooth.ENABLE_BT_REQUEST
            );
        }

        initUI();
    }

    private void initUI() {
        findViewById(R.id.connectBtn).setOnClickListener(l -> {
            l.setEnabled(false);
            try {
                connectToBTServer();
            } catch (BluetoothDeviceNotFound bluetoothDeviceNotFound) {
                Toast.makeText(this, "Bluetooth device not found !", Toast.LENGTH_LONG)
                        .show();
                bluetoothDeviceNotFound.printStackTrace();
            } finally {
                l.setEnabled(true);
            }
        });

        findViewById(R.id.btn_led1).setOnClickListener(l -> {
            String toAppend;
            if(led1.equals("1")){
                toAppend = "0";
            }else{
                toAppend = "1";
            }
            String message = toAppend+"|-1|-1|-1|-1|-1|-1";
            btChannel.sendMessage(message);
        });

        findViewById(R.id.btn_led2).setOnClickListener(l -> {
            String toAppend;
            if(led2.equals("1")){
                toAppend = "0";
            }else{
                toAppend = "1";
            }
            String message = "-1|"+toAppend+"|-1|-1|-1|-1|-1";
            btChannel.sendMessage(message);
        });

        findViewById(R.id.btn_led3Plus).setOnClickListener(l -> {
            String toAppend = "-1";
            if(Integer.parseInt(led3) < 5){
                toAppend = String.valueOf(Integer.parseInt(led3)+1);
            }
            String message = "-1|-1|"+toAppend+"|-1|-1|-1|-1";
            btChannel.sendMessage(message);
        });

        findViewById(R.id.btn_led3Minus).setOnClickListener(l -> {
            String toAppend = "-1";
            if(Integer.parseInt(led3) > 0){
                toAppend = String.valueOf(Integer.parseInt(led3)-1);
            }
            String message = "-1|-1|"+toAppend+"|-1|-1|-1|-1";
            btChannel.sendMessage(message);
        });

        findViewById(R.id.btn_led4Plus).setOnClickListener(l -> {
            String toAppend = "-1";
            if(Integer.parseInt(led4) < 5){
                toAppend = String.valueOf(Integer.parseInt(led4)+1);
            }
            String message = "-1|-1|-1|"+toAppend+"|-1|-1|-1";
            btChannel.sendMessage(message);
        });

        findViewById(R.id.btn_led4Minus).setOnClickListener(l -> {
            String toAppend = "-1";
            if(Integer.parseInt(led4) > 0){
                toAppend = String.valueOf(Integer.parseInt(led4)-1);
            }
            String message = "-1|-1|-1|"+toAppend+"|-1|-1|-1";
            btChannel.sendMessage(message);
        });

        findViewById(R.id.irrigation_btn).setOnClickListener(l -> {
            String toAppend;
            String speedToAppend;
            if(irrigationSpeed.equals("0")){
                speedToAppend = "50";
            }else{
                speedToAppend = irrigationSpeed;
            }
            if(openIrrigation.equals("1")){
                toAppend = "0";
            }else{
                toAppend = "1";
            }
            String message = "-1|-1|-1|-1|"+toAppend+"|"+speedToAppend+"|-1";
            //String message = ("PROVA");
            btChannel.sendMessage(message);
        });

        findViewById(R.id.btn_irrigationPlus).setOnClickListener(l -> {
            String speedToAppend = "-1";
            if(Integer.parseInt(irrigationSpeed) >= 20){
                speedToAppend = String.valueOf(Integer.parseInt(irrigationSpeed)-10);
            }
            String message = "-1|-1|-1|-1|-1|"+speedToAppend+"|-1";
            btChannel.sendMessage(message);
        });

        findViewById(R.id.btn_irrigationMinus).setOnClickListener(l -> {
            String speedToAppend = "-1";
            if(Integer.parseInt(irrigationSpeed) <= 40){
                speedToAppend = String.valueOf(Integer.parseInt(irrigationSpeed)+10);
            }
            String message = "-1|-1|-1|-1|-1|"+speedToAppend+"|-1";
            btChannel.sendMessage(message);
        });

        findViewById(R.id.img_notifications).setOnClickListener(l -> {
            if(btChannel != null){
                String message = "-1|-1|-1|-1|-1|-1|MAN";
                btChannel.sendMessage(message);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        btChannel.close();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
                                    @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == C.bluetooth.ENABLE_BT_REQUEST && resultCode == RESULT_OK) {
            Log.d(C.APP_LOG_TAG, "Bluetooth enabled!");
        }

        if (requestCode == C.bluetooth.ENABLE_BT_REQUEST && resultCode == RESULT_CANCELED) {
            Log.d(C.APP_LOG_TAG, "Bluetooth not enabled!");
        }
    }

    private void connectToBTServer() throws BluetoothDeviceNotFound {
        if(!openedConnection){
            final BluetoothDevice serverDevice = BluetoothUtils
                    .getPairedDeviceByName(C.bluetooth.BT_DEVICE_ACTING_AS_SERVER_NAME);
            // !!! Choose the right UUID value
            final UUID uuid = BluetoothUtils.getEmbeddedDeviceDefaultUuid();
//        final UUID uuid = BluetoothUtils.generateUuidFromString(C.bluetooth.BT_SERVER_UUID);

            new ConnectToBluetoothServerTask(serverDevice, uuid, new ConnectionTask.EventListener() {
                @Override
                public void onConnectionActive(final BluetoothChannel channel) {
                    ((TextView) findViewById(R.id.statusLabel)).setText(String.format(
                            "Status : connected to server on device %s",
                            serverDevice.getName()
                    ));

                    btChannel = channel;
                    btChannel.registerListener(new RealBluetoothChannel.Listener() {
                        @Override
                        public void onMessageReceived(String receivedMessage) {
                            TextView chatLabel = (TextView) findViewById(R.id.chatLabel);
                            chatLabel.setText(String.format(
                                    "> [RECEIVED from %s] %s\n",
                                    btChannel.getRemoteDeviceName(),
                                    receivedMessage
                            )+ chatLabel.getText());
                            String[] splitted = receivedMessage.split("\\|");
                            Log.d("ZIO", Arrays.toString(splitted));
                            led1 = splitted[0];
                            led2 = splitted[1];
                            led3 = splitted[2];
                            led4 = splitted[3];
                            openIrrigation = splitted[4];
                            irrigationSpeed = splitted[5];
                            modality = splitted[6].replaceAll("\\r","");

                            updateUI();
                        }

                        @Override
                        public void onMessageSent(String sentMessage) {
                            ((TextView) findViewById(R.id.chatLabel)).append(String.format(
                                    "> [SENT to %s] %s\n",
                                    btChannel.getRemoteDeviceName(),
                                    sentMessage
                            ));

                            Log.d("SEND",sentMessage);
                        }
                    });

                    findViewById(R.id.btn_irrigationMinus).setEnabled(true);
                    findViewById(R.id.btn_irrigationPlus).setEnabled(true);
                    findViewById(R.id.irrigation_btn).setEnabled(true);
                    findViewById(R.id.btn_led3Minus).setEnabled(true);
                    findViewById(R.id.btn_led3Plus).setEnabled(true);
                    findViewById(R.id.btn_led4Minus).setEnabled(true);
                    findViewById(R.id.btn_led4Plus).setEnabled(true);
                    findViewById(R.id.btn_led2).setEnabled(true);
                    findViewById(R.id.btn_led1).setEnabled(true);

                    findViewById(R.id.img_notifications).setClickable(true);
                    findViewById(R.id.img_notifications).setFocusable(true);

                    connectBtn.setText("Return in auto mode");

                    String message = "-1|-1|-1|-1|-1|-1|MAN";
                    btChannel.sendMessage(message);
                }

                @Override
                public void onConnectionCanceled() {
                    ((TextView) findViewById(R.id.statusLabel)).setText(String.format(
                            "Status : unable to connect, device %s not found!",
                            C.bluetooth.BT_DEVICE_ACTING_AS_SERVER_NAME
                    ));
                }
            }).execute();
            openedConnection = true;
        }else{
            String message = "-1|-1|-1|-1|-1|-1|AUT";
            btChannel.sendMessage(message);

            btChannel.close();

            openedConnection = false;

            findViewById(R.id.btn_irrigationMinus).setEnabled(false);
            findViewById(R.id.btn_irrigationPlus).setEnabled(false);
            findViewById(R.id.irrigation_btn).setEnabled(false);
            findViewById(R.id.btn_led3Minus).setEnabled(false);
            findViewById(R.id.btn_led3Plus).setEnabled(false);
            findViewById(R.id.btn_led4Minus).setEnabled(false);
            findViewById(R.id.btn_led4Plus).setEnabled(false);
            findViewById(R.id.btn_led2).setEnabled(false);
            findViewById(R.id.btn_led1).setEnabled(false);

            findViewById(R.id.img_notifications).setClickable(false);
            findViewById(R.id.img_notifications).setFocusable(false);

            connectBtn.setText("Require Manual Control");
        }

    }

    private void updateUI(){
        txtLed3.setText(led3);
        txtLed4.setText(led4);

        if(modality.equals("ARM")){
            findViewById(R.id.img_notifications).setBackgroundResource(R.drawable.ic_baseline_notifications_24_error);
        }else{
            findViewById(R.id.img_notifications).setBackgroundResource(R.drawable.ic_baseline_notifications_24);
        }

        if(irrigationSpeed.equals("50")){
            txtIrrigationSpeed.setText("1");
        }else if(irrigationSpeed.equals("40")){
            txtIrrigationSpeed.setText("2");
        }else if(irrigationSpeed.equals("30")){
            txtIrrigationSpeed.setText("3");
        }else if(irrigationSpeed.equals("20")){
            txtIrrigationSpeed.setText("4");
        }
    }
}