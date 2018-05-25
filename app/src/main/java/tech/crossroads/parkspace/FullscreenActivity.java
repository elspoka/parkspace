package tech.crossroads.parkspace;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    NfcAdapter nfcAdapter;
    private static final boolean AUTO_HIDE = true;
    public ProgressBar pBar;
    final int PERMISSION_ACCESS_COARSE_LOCATION=2;
    final int PERMISSION_ACCESS_FINE_LOCATION=3;
    final int PERMISSION_WRITE_EXTERNAL_STORAGE=4;

    final int PERMISSION_MULTIPLE=123;

    boolean permission1 =false;
    boolean permission2 = false;
    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
//    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View view, MotionEvent motionEvent) {
//            if (AUTO_HIDE) {
//                delayedHide(AUTO_HIDE_DELAY_MILLIS);
//            }
//            return false;
//        }
//    };

    ImageView iv;

    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            goToMainActivity=false;
            processIntent(getIntent());
        }

        if ( (ActivityCompat.checkSelfPermission(FullscreenActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)&&
                (ActivityCompat.checkSelfPermission(FullscreenActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED))
        {
            goToMainActivity();
        }




       // if(appStatus) {

//            if (goToMainActivity) {
//                final Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        if (status == 0) {
//                            Intent intent = new Intent(FullscreenActivity.this, IntroActivity.class);
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.right_to_left,
//                                    R.anim.left_to_right);
//                            finish();
//                        } else {
//                            Intent intent = new Intent(FullscreenActivity.this, MainActivity.class);
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.right_to_left,
//                                    R.anim.left_to_right);
//                            finish();
//                        }
//
//                    }
//                }, 2000);
//            }

        }

    public void goToMainActivity(){
        if (goToMainActivity) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (status == 0) {
                        Intent intent = new Intent(FullscreenActivity.this, IntroActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_to_left,
                                R.anim.left_to_right);
                        finish();
                    } else {
                        Intent intent = new Intent(FullscreenActivity.this, MainActivity.class);
                        writeMinusOneTicket();
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_to_left,
                                R.anim.left_to_right);
                        finish();
                    }

                }
            }, 1000);
        }
    }
//        else
//        {Intent intent = new Intent(FullscreenActivity.this, NoTicketsActivity.class);
//            startActivity(intent);
//            overridePendingTransition(R.anim.right_to_left,
//                    R.anim.left_to_right);
//            finish();
//        }


    //}


    @Override
    public void onBackPressed(){
        finish();

    }

    int ticketsNumber=0;

    ImageView viewThess;
    boolean goToMainActivity=true;
    int status;
    private boolean appStatus= false;

    private void appReadStatusAndRewrite() {

        if (isExternalStorageReadable()) {
            File root = android.os.Environment.getExternalStorageDirectory();
            File file = new File(root.getAbsolutePath() + "/external_storage_dir/vs/vs0");
            if (file.exists()) {




                String readTickets = readExt(file);
                ticketsNumber = Integer.valueOf(readTickets);
                if (ticketsNumber > 0) {
                    appStatus =true;
                    ticketsNumber--;
                    if (isExternalStorageWritable()) {
                        String writeTickets = String.valueOf(ticketsNumber);
                        writeToSDFile(writeTickets);
                    }

                }

                //Toast.makeText(FullscreenActivity.this, readExt(file), Toast.LENGTH_SHORT).show();


            } else {

                if (isExternalStorageWritable()) {
                    writeToSDFile("100");
                    //Toast.makeText(FullscreenActivity.this, "file written", Toast.LENGTH_SHORT).show();
                }
            }


        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fullscreen);

        if ( (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    PERMISSION_MULTIPLE);
        }

//        if(Build.VERSION.SDK_INT >=25) {
//
//            ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
//            ShortcutInfo shortcut = new ShortcutInfo.Builder(this, "cartId")
//                    .setShortLabel("Notifications")
//                    .setLongLabel("Activate or deactivate notifications")
//                    .setIcon(Icon.createWithResource(this, R.drawable.one_shadow_re))
//                    .setIntent(new Intent(this, FullscreenActivity.class))
//                    .build();
//            shortcutManager.setDynamicShortcuts(Arrays.asList(shortcut));
//        }

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
//                    PERMISSION_WRITE_EXTERNAL_STORAGE);
//        }


//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.ACCESS_COARSE_LOCATION },
//                    PERMISSION_ACCESS_COARSE_LOCATION);
//        }


        String tutorialStatus = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("tech.crossroads.parkverse.tutorial", "0");
        status=Integer.valueOf(tutorialStatus);
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("tech.crossroads.parkverse.tutorial", "1").commit();

        //appReadStatusAndRewrite();

        pBar = (ProgressBar) findViewById(R.id.progressBar);
        iv = (ImageView) findViewById(R.id.bunner);
        viewThess = (ImageView) findViewById(R.id.thessalonikiStart);

//        iv.setOnClickListener(new View.OnClickListener()
//        {
//            public void onClick(View v)
//            {
//
////                if(status==0) {
////                    Intent intent = new Intent(FullscreenActivity.this, IntroActivity.class);
////                    startActivity(intent);
////                    overridePendingTransition(R.anim.right_to_left,
////                            R.anim.left_to_right);
////                    finish();
////                }
////                else{Intent intent = new Intent(FullscreenActivity.this, MainActivity.class);
////                    startActivity(intent);
////                    overridePendingTransition(R.anim.right_to_left,
////                            R.anim.left_to_right);
////                    finish();
////                }
//
//                //pBar.setVisibility(View.VISIBLE);
//
//               // new ProgressTask().execute();
//                //Intent i = new Intent(FullscreenActivity.this, MyPreferencesActivity.class);
//               // startActivity(i);
//            }
//
//
//        });

//        ScaleAnimation fade_in =  new ScaleAnimation(0.8f, 1f, 0.8f, 1f, Animation.RELATIVE_TO_SELF, 2f, Animation.RELATIVE_TO_SELF, 2f);
//        fade_in.setDuration(2500);     // animation duration in milliseconds
//        fade_in.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.
//        viewThess.startAnimation(fade_in);




        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            //Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            //finish();
        }
        //readFromIntent(getIntent());
        //processIntent(getIntent());


//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                // Actions to do after 10 seconds
//
//
//
//            }
//        }, 10000);


       // mVisible = true;
       // mControlsView = findViewById(R.id.fullscreen_content_controls);
       // mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        //mContentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toggle();
//            }
//        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
       // findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//
//        // Trigger the initial hide() shortly after the activity has been
//        // created, to briefly hint to the user that UI controls
//        // are available.
//        //delayedHide(100);
//    }
//
//    private void toggle() {
//        if (mVisible) {
//            hide();
//        } else {
//            show();
//        }
//    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }



    private class ProgressTask extends AsyncTask <Void,Void,Void>{
        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            //my stuff is here
            Intent intent = new Intent(FullscreenActivity.this, MainActivity.class);
            startActivity(intent);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            pBar.setVisibility(View.INVISIBLE);
        }
    }

    private void readFromIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            buildTagViews(msgs);
        }
    }
    private void buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) return;

        String text = "";
//        String tagId = new String(msgs[0].getRecords()[0].getType());
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16"; // Get the Text Encoding
        int languageCodeLength = payload[0] & 0063; // Get the Language Code, e.g. "en"
        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");

        try {
            // Get the Text
            text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("UnsupportedEncoding", e.toString());
        }

        //tvNFCContent.setText("NFC Content: " + text);
       // Toast.makeText(FullscreenActivity.this,text,Toast.LENGTH_LONG);
    }

    void processIntent(Intent intent) {
        //textView = (TextView) findViewById(R.id.textView);
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        //textView.setText(new String(msg.getRecords()[0].getPayload()));
        //Toast.makeText(FullscreenActivity.this,new String(msg.getRecords()[0].getPayload()),Toast.LENGTH_LONG).show();

        String payload=String.valueOf(msg.getRecords()[0].getPayload());

        boolean iO=isOnline();
        //if(payload.contains("verse")) {

        String modeStr = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("parkspace.nfcAction", "1");
        int mode= Integer.valueOf(modeStr);

            if (iO) {
                switch (mode) {
                    case 1:
                        Intent intent1 = new Intent(FullscreenActivity.this, SendLocation.class);
                        startActivity(intent1);
                        break;
                    case 2:
                        Intent intent2 = new Intent(FullscreenActivity.this, SetReminderOnScanActivity.class);
                        startActivity(intent2);
                        break;
                    case 3:
                        Intent intent3 = new Intent(FullscreenActivity.this, SelectShareActivity.class);
                        startActivity(intent3);
                        break;
                }

            } else if (!iO) {
                Intent intent2 = new Intent(FullscreenActivity.this, NoInternetActivity.class);
                startActivity(intent2);
            }

       // }
    }

    public boolean isOnline() {
        //gia ckeck an yparxei internet//
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission1 = true;
                    // All good!

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                                PERMISSION_WRITE_EXTERNAL_STORAGE);
                    }

                    if ( (ActivityCompat.checkSelfPermission(FullscreenActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)&&
                            (ActivityCompat.checkSelfPermission(FullscreenActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED))
                    {
                        goToMainActivity();
                    }


                } else {
                    Toast.makeText(this, "The application requires your location to work properly", Toast.LENGTH_SHORT).show();
                }

                break;
            case PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission2 = true;

                    // All good!
                    appReadStatusAndRewrite();

                    if ( (ActivityCompat.checkSelfPermission(FullscreenActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)&&
                            (ActivityCompat.checkSelfPermission(FullscreenActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED))
                    {
                        goToMainActivity();
                    }


                } else {
                    Toast.makeText(this, "The application requires external storage permission to work properly", Toast.LENGTH_SHORT).show();
                }

                break;

            case PERMISSION_MULTIPLE:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                   // if ( (ActivityCompat.checkSelfPermission(FullscreenActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)&&
                           // (ActivityCompat.checkSelfPermission(FullscreenActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED))
                    appReadStatusAndRewrite();
                    {
                        goToMainActivity();
                    }

                } else {
                    // Permission Denied
                    Toast.makeText(FullscreenActivity.this, "All permissions need to be granted", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
        }
    }

    public void writeMinusOneTicket(){
        if(isExternalStorageReadable()) {
            File root = android.os.Environment.getExternalStorageDirectory();
            File file = new File(root.getAbsolutePath() + "/external_storage_dir/vs/vs0");
            if (file.exists()) {
                String readTickets = readExt(file);
                ticketsNumber = Integer.valueOf(readTickets);
                if (ticketsNumber > 0) {
                    ticketsNumber--;
                    if (isExternalStorageWritable()) {
                        String writeTickets = String.valueOf(ticketsNumber);
                        writeToSDFile(writeTickets);
                    }
                }
                //Toast.makeText(MainActivity.this, readExt(file), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void writeToSDFile(String text){

        // Find the root of the external storage.
        // See http://developer.android.com/guide/topics/data/data-  storage.html#filesExternal

        File root = android.os.Environment.getExternalStorageDirectory();
        //tv.append("\nExternal file system root: "+root);

        // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder

        File dir = new File (root.getAbsolutePath() + "/external_storage_dir/vs");
        dir.mkdirs();
        File file = new File(dir, "vs0");

        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.println(text);
            //pw.println("Hello");
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //Log.i(TAG, "******* File not found. Did you" +
            //     " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //tv.append("\n\nFile written to "+file);
    }

    private String readExt(File file ){
        String data = "";
        try {
            FileInputStream fis = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                data = data + strLine;
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;

    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }



    public void writePlusOneTicket(){
        if(isExternalStorageReadable()) {
            File root = android.os.Environment.getExternalStorageDirectory();
            File file = new File(root.getAbsolutePath() + "/external_storage_dir/vs/vs0");
            if (file.exists()) {
                String readTickets = readExt(file);
                ticketsNumber = Integer.valueOf(readTickets);
                if (ticketsNumber >= 0) {
                    ticketsNumber++;
                    if (isExternalStorageWritable()) {
                        String writeTickets = String.valueOf(ticketsNumber);
                        writeToSDFile(writeTickets);
                    }
                }
                //Toast.makeText(MainActivity.this, readExt(file), Toast.LENGTH_SHORT).show();
            }
        }
    }









    //////////////



//    @SuppressLint("InlinedApi")
//    private void show() {
//        // Show the system bar
//        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
//        mVisible = true;
//
//        // Schedule a runnable to display UI elements after a delay
//        mHideHandler.removeCallbacks(mHidePart2Runnable);
//        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
//    }
//
//    /**
//     * Schedules a call to hide() in [delay] milliseconds, canceling any
//     * previously scheduled calls.
//     */
//    private void delayedHide(int delayMillis) {
//        mHideHandler.removeCallbacks(mHideRunnable);
//        mHideHandler.postDelayed(mHideRunnable, delayMillis);
//    }
}
