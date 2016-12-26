package com.rol.usa.raydiusdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.Iterator;

public class BeaconSearcher extends Activity implements BeaconConsumer {

    private final String TAG = "BeaconSearcher";
    private BeaconManager beaconManager;
    private TextView beaconInfo;
    private TextView beaconAreaInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_searcher);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconInfo = (TextView) findViewById(R.id.beaconInfo);
        beaconAreaInfo = (TextView) findViewById(R.id.beaconAreaInfo);

        beaconManager.bind(this);
    }

    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    protected void onPause() {
        super.onPause();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(true);
    }

    protected void onResume() {
        super.onResume();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {

            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                int beaconId = -1;
                if (beacons.size() > 0) {
                    //EditText editText = (EditText)RangingActivity.this.findViewById(R.id.rangingText);
                    String beaconInfoText = ""; int currentIndex = 1;
                    Iterator<Beacon> beaconIterator = beacons.iterator();
                    double minDistance = Double.MAX_VALUE;
                    while (beaconIterator.hasNext()) {
                        Beacon beacon = beaconIterator.next();
                        logToDisplay("The first beacon " + beacon.toString() + " is about " + beacon.getDistance() + " meters away.");
                        beaconInfoText += "Beacon #"+ currentIndex + "\n\t" + beacon.toString() + "\n\tDistance: " + beacon.getDistance() + " meters \n\n";
                        currentIndex++;
                        if (beacon.getDistance() < minDistance) {
                            minDistance = beacon.getDistance();
                            beaconId = beacon.getId2().toInt();
                        }
                    }
                    if (minDistance > 6.0) {
                        beaconId = -1;
                    }
                    updateBeaconInfo(beaconInfoText, beaconId);
                } else {
                    updateBeaconInfo("No Beacons found.", beaconId);
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
        } catch (RemoteException e) {    }
    }

    private void logToDisplay(final String line) {
        Log.i("Found beacon", line);
    }
    private void updateBeaconInfo(final String content, final int beaconId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                beaconInfo.setText(content);
                String areaInfo = "-";
                switch(beaconId) {
                    case 47158:
                        areaInfo = "Dome";
                        break;
                    case 47390:
                        areaInfo = "Reaffirming Identity";
                        break;
                    case 43955:
                        areaInfo = "Fostering Harmony";
                        break;
                    case 47208:
                        areaInfo = "Inspiring Hope";
                        break;
                    case 47345:
                        areaInfo = "Engaging in Dialogue";
                        break;
                    default:
                        break;
                }
                beaconAreaInfo.setText(areaInfo);
            }
        });
    }
}
