package com.example.hackathon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class MyActivity extends Activity implements GoogleMap.OnMarkerClickListener{
    /**
     * Called when the activity is first created.
     */
    GoogleMap googleMap;
    int i = 1;
    Marker me;

    Event hackathon = new Event();
    Event backToSchool = new Event();
    Event event = new Event();

    ArrayList<Event> events = new ArrayList<Event>();

    public Marker MarkerSetUp(LatLng markpos){
        return googleMap.addMarker(new MarkerOptions().
                position(markpos));
    }

    public void initializeMarkers(){
        TestAdapter mDbHelper = new TestAdapter(MyActivity.this);
        mDbHelper.createDatabase();
        mDbHelper.open();

        Cursor testdata = mDbHelper.getTestData();

        for (int i = 0; i < events.size(); i++){
            float Lat = LoadFromDb.GetColumnValue(testdata, "eventLat");
            float Lng = LoadFromDb.GetColumnValue(testdata, "eventLng");

            events.get(i).eventMarker = MarkerSetUp(new LatLng(Lat, Lng));
            if (testdata != null){
                testdata.moveToNext();
            }
            if (testdata.isAfterLast()){
                testdata = null;
            }

        }
    }
    public void initializeEvents(){
        events.add(hackathon);
        events.add(backToSchool);
        events.add(event);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initializeEvents();
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().
                    findFragmentById(R.id.map)).getMap();
        }
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMyLocationEnabled(true);
        if (googleMap != null){
            me = googleMap.addMarker(new MarkerOptions().position(new LatLng(1,1)).title("It's Me!")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {

                    me.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(me.getPosition().latitude,
                            me.getPosition().longitude));
                    /*CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(4,
                          4));*/
                    CameraUpdate zoom=CameraUpdateFactory.zoomTo(18);
                    if(i == 1){
                        googleMap.moveCamera(center);
                        googleMap.animateCamera(zoom);
                        i = 0;
                    }
                }
            });
        }
        initializeMarkers();
        googleMap.setOnMarkerClickListener(this);
        Calendar cal = Calendar.getInstance();
        String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        String month = String.valueOf(cal.get(Calendar.MONTH)+1);
        String year = String.valueOf(cal.get(Calendar.YEAR));
        String hour = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
        String date = day + "/" + month + "/" + year + " at: " + hour;
        //Toast.makeText(getApplicationContext(), date, 100).show();
        TestAdapter mDbHelper = new TestAdapter(MyActivity.this);
        mDbHelper.createDatabase();
        mDbHelper.open();

        Cursor testdata = mDbHelper.getTestData();

        for (int i = 0; i < events.size(); i++){

            events.get(i).eventExpirationDate = LoadFromDb.GetColumnValueString(testdata, "eventExpiriationDate");

            if(events.get(i).eventExpirationDate.equals(date)){
                events.get(i).eventMarker.remove();
                events.remove(i);
            }

            if (testdata != null){
                testdata.moveToNext();
            }
            if (testdata.isAfterLast()){
                testdata = null;
            }
        }
    }

    public boolean onMarkerClick(Marker marker) {
        if(!marker.equals(me)){

            TestAdapter mDbHelper = new TestAdapter(MyActivity.this);
            mDbHelper.createDatabase();
            mDbHelper.open();

            Cursor testdata = mDbHelper.getTestData();

            for (int i = 0; i < events.size(); i++){

                String eventName = LoadFromDb.GetColumnValueString(testdata, "eventName");
                String eventDescription = LoadFromDb.GetColumnValueString(testdata, "eventDescription");
                final String eventURL = LoadFromDb.GetColumnValueString(testdata, "eventURL");
                final String websiteURL = LoadFromDb.GetColumnValueString(testdata, "websiteURL");
                final String eventTag = LoadFromDb.GetColumnValueString(testdata, "eventTag");
                final String eventAddress = LoadFromDb.GetColumnValueString(testdata, "address");
                if(marker.equals(events.get(i).eventMarker)){
                    AlertDialog.Builder markerDialog = new AlertDialog.Builder(MyActivity.this);
                    markerDialog.setTitle(eventName + "\n");
                    markerDialog.setMessage(eventTag + "\n" + "event discription \n" + eventDescription);
                    Button facebook = new Button(this);
                    Button web = new Button(this);
                    Button takeMeThere = new Button(this);
                    LinearLayout lin = new LinearLayout(getApplicationContext());
                    lin.addView(facebook);
                    lin.addView(web);
                    lin.addView(takeMeThere);
                    LinearLayout.LayoutParams def = new LinearLayout.LayoutParams(100, 100);
                    def.setMargins(50,50,50,50);
                    def.gravity = Gravity.CENTER;
                    facebook.setLayoutParams(def);
                    facebook.setBackgroundResource(R.drawable.facebook_icon);
                    web.setBackgroundResource(R.drawable.web);
                    web.setLayoutParams(def);
                    takeMeThere.setText("Take me There");
                    markerDialog.setView(lin);
                    facebook.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(eventURL));
                            startActivity(browserIntent);
                        }
                    });
                    web.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteURL));
                            startActivity(browserIntent);

                        }
                    });
                    takeMeThere.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + eventAddress));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }
                    });
                    markerDialog.setPositiveButton("look for an random event", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Random rand = new Random();
                            int o =  rand.nextInt((events.size() - 1 - 0) + 1) + 0;
                                  CameraUpdate animated = CameraUpdateFactory.newLatLng(new LatLng(events.get(o).eventMarker.getPosition().latitude,
                                          events.get(o).eventMarker.getPosition().longitude));
                                  googleMap.moveCamera(animated);
                            }

                    });
                    markerDialog.show();
                }


                if (testdata != null){
                    testdata.moveToNext();
                }
                if (testdata.isAfterLast()){
                    testdata = null;
                }
            }

        }
        return true;
    }
}