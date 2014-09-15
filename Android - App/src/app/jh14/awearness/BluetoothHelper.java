package app.jh14.awearness;

import android.bluetooth.BluetoothDevice;

import java.util.UUID;

public class BluetoothHelper {
    public static String MY_UUID = "0000%04X-0000-1000-8000-00805F9B34FB";

    public static UUID sixteenBitUuid(long shortUuid) {
        assert shortUuid >= 0 && shortUuid <= 0xFFFF;
        return UUID.fromString(String.format(MY_UUID, shortUuid & 0xFFFF));
    }

    public static String infoAboutDevice(BluetoothDevice device, int rssi) {
        return new StringBuilder()
                .append("Name: ").append(device.getName())
                .append("\nMAC-Adresse: ").append(device.getAddress())
                .append("\nRSSI: ").append(rssi)
                .toString();
    }
}
