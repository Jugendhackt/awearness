package app.jh14.awearness;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class MainActivity extends Activity implements BluetoothAdapter.LeScanCallback {
    // Zustände der App
    final private static int STATE_BLUETOOTH_OFF = 1;
    final private static int STATE_DISCONNECTED = 2;
    final private static int STATE_CONNECTING = 3;
    final private static int STATE_CONNECTED = 4;

    private int state;

    private boolean scanStarted;
    private boolean scanning;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;

    private RFduinoService rfduinoService;
    
    LocationListener locationListener;

    private Button enableBluetoothButton;
    private TextView scanStatusText;
    private Button scanButton;
    private TextView deviceInfoText;
    private TextView connectionStatusText;
    private Button connectButton;
    private Button sendZeroButton;
    private Button sendValueButton;
    private Button buttonStartService;
    private Button buttonManualGPS;
    private Button buttonTestMessage;
    
    Thread ledThread;

    private final BroadcastReceiver bluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
            if (state == BluetoothAdapter.STATE_ON) {
                upgradeState(STATE_DISCONNECTED);
            } else if (state == BluetoothAdapter.STATE_OFF) {
                downgradeState(STATE_BLUETOOTH_OFF);
            }
        }
    };

    private final BroadcastReceiver scanModeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            scanning = (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_NONE);
            scanStarted &= scanning;
            updateUi();
        }
    };

    private final ServiceConnection rfduinoServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            rfduinoService = ((RFduinoService.LocalBinder) service).getService();
            if (rfduinoService.initialize()) {
                if (rfduinoService.connect(bluetoothDevice.getAddress())) {
                    upgradeState(STATE_CONNECTING);
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            rfduinoService = null;
            downgradeState(STATE_DISCONNECTED);
        }
    };

    private final BroadcastReceiver rfduinoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (RFduinoService.ACTION_CONNECTED.equals(action)) {
                upgradeState(STATE_CONNECTED);
            } else if (RFduinoService.ACTION_DISCONNECTED.equals(action)) {
                downgradeState(STATE_DISCONNECTED);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        
        
        enableBluetoothButton = (Button) findViewById(R.id.enableBluetooth);
        enableBluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableBluetoothButton.setEnabled(false);
                enableBluetoothButton.setText(
                        bluetoothAdapter.enable() ? "Aktiviere Bluetooth ..." : "Aktivierung fehlgeschlagen!");
            }
        });

        scanStatusText = (TextView) findViewById(R.id.scanStatus);

        scanButton = (Button) findViewById(R.id.scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanStarted = true;
                bluetoothAdapter.startLeScan(
                        new UUID[]{ RFduinoService.UUID_SERVICE },
                        MainActivity.this);
            }
        });

        deviceInfoText = (TextView) findViewById(R.id.deviceInfo);

        connectionStatusText = (TextView) findViewById(R.id.connectionStatus);

        connectButton = (Button) findViewById(R.id.connect);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                connectionStatusText.setText("Verbinden ...");
                Intent rfduinoIntent = new Intent(MainActivity.this, RFduinoService.class);
                bindService(rfduinoIntent, rfduinoServiceConnection, BIND_AUTO_CREATE);
            }
        });

        sendZeroButton = (Button) findViewById(R.id.sendZero);

        sendValueButton = (Button) findViewById(R.id.sendValue);
        
        buttonStartService = (Button) findViewById(R.id.buttonStartService);
        buttonStartService.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				/*BluetoothInformations.rfDuinoService = rfduinoService;
				
				if(getApplicationContext().getSystemService("app.jh14.awearness") != null) {
					Toast.makeText(getApplicationContext(), "EXISTIERT", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(), "EXISTIERT NICHT", Toast.LENGTH_LONG).show();
				}
				
				Intent serviceIntent = new Intent(getApplicationContext(), AwearnessService.class);
				getApplicationContext().startService(serviceIntent);*/
				
				Toast.makeText(getApplicationContext(), "Diese Funktion ist noch nicht komplett bugfrei! Daher wurde Sie deaktiviert!", Toast.LENGTH_LONG).show();
			}
		});
        
        buttonManualGPS = (Button) findViewById(R.id.buttonManualGPS);
        buttonManualGPS.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(!checkGPS()) {
					Toast.makeText(getApplicationContext(), "GPS muss für diese Funktion aktiviert sein.", Toast.LENGTH_LONG).show();
					return;
				}
				
				LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				
				Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				
				try {
					updateCoordinates(location.getLatitude(), location.getLongitude());
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "Es konnten keine Koordinaten bestimmt werden. Versuchen Sie es später erneut!", Toast.LENGTH_LONG).show();
				}
			}
		});
        
        buttonTestMessage = (Button) findViewById(R.id.buttonSendTestMessage);
        buttonTestMessage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {				
				if(ledThread == null) {
					ledThread = new Thread(new LEDThread(rfduinoService));
					ledThread.start();
					
					Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					vibrator.vibrate(500);
				} else if(ledThread.isAlive()) {
					ledThread.interrupt();
					ledThread.stop();
					
					rfduinoService.send(HexAsciiHelper.hexToBytes("00"));
				} else if(ledThread.isAlive()){
					ledThread = new Thread(new LEDThread(rfduinoService));
					ledThread.start();
					
					Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				}
			}
		});
    }

    @Override
    protected void onStart() {
        super.onStart();

        registerReceiver(scanModeReceiver, new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));
        registerReceiver(bluetoothStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        registerReceiver(rfduinoReceiver, RFduinoService.getIntentFilter());

        updateState(bluetoothAdapter.isEnabled() ? STATE_DISCONNECTED : STATE_BLUETOOTH_OFF);
    }

    @Override
    protected void onStop() {
        super.onStop();

        bluetoothAdapter.stopLeScan(this);

        unregisterReceiver(scanModeReceiver);
        unregisterReceiver(bluetoothStateReceiver);
        unregisterReceiver(rfduinoReceiver);
    }

    private void upgradeState(int newState) {
        if (newState > state) {
            updateState(newState);
        }
    }

    private void downgradeState(int newState) {
        if (newState < state) {
            updateState(newState);
        }
    }

    private void updateState(int newState) {
        state = newState;
        updateUi();
    }

    private void updateUi() {
        boolean on = state > STATE_BLUETOOTH_OFF;
        enableBluetoothButton.setEnabled(!on);
        enableBluetoothButton.setText(on ? "Bluetooth aktiviert" : "Aktiviere Bluetooth");
        scanButton.setEnabled(on);

        if (scanStarted && scanning) {
            scanStatusText.setText("Suchen ...");
            scanButton.setText("Suche gestoppt!");
            scanButton.setEnabled(true);
        } else if (scanStarted) {
            scanStatusText.setText("Suche gestartet ...");
            scanButton.setEnabled(false);
        } else {
            scanStatusText.setText("");
            scanButton.setText("Suche");
            scanButton.setEnabled(true);
        }

        boolean connected = false;
        String connectionText = "Getrennt";
        if (state == STATE_CONNECTING) {
            connectionText = "Verbinden ...";
        } else if (state == STATE_CONNECTED) {
            connected = true;
            connectionText = "Verbunden";
        }
        connectionStatusText.setText(connectionText);
        connectButton.setEnabled(bluetoothDevice != null && state == STATE_DISCONNECTED);

        sendZeroButton.setEnabled(connected);
        sendValueButton.setEnabled(connected);
        buttonStartService.setEnabled(connected);
        buttonTestMessage.setEnabled(connected);
    }

    @Override
    public void onLeScan(BluetoothDevice device, final int rssi, final byte[] scanRecord) {
        bluetoothAdapter.stopLeScan(this);
        bluetoothDevice = device;

        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deviceInfoText.setText(
                        BluetoothHelper.infoAboutDevice(bluetoothDevice, rssi));
                updateUi();
            }
        });
    }
    
    public boolean checkGPS() {
    	LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		return isGPSEnabled;
    }
    
    public void updateCoordinates(double latitude, double longitude) {
    	TextView textViewLatitude = (TextView) findViewById(R.id.textViewCurrentLatitude);
		textViewLatitude.setText("Breitengrad: " + latitude);
		
		TextView textViewLongitude = (TextView) findViewById(R.id.textViewCurrentLongitude);
		textViewLongitude.setText("Längengrad: " + longitude);
    }

}

