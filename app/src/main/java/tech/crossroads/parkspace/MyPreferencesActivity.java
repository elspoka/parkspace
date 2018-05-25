package tech.crossroads.parkspace;


import android.content.Intent;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import android.os.Bundle;

import android.view.MenuItem;


public class MyPreferencesActivity extends PreferenceActivity {

    boolean directionsStatus=false;

    //ListPreference listPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        super.onCreate(savedInstanceState);

        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.custom_title_bar);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        android.app.ActionBar ab = getActionBar();
        ab.setTitle("Settings");


//        Preference goToLocationSettings = (Preference) findPreference("goToLocationSettings");
//        goToLocationSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//
//            public boolean onPreferenceClick(Preference preference) {
//                Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(viewIntent);
//
//                return true;
//            }
//        });
       // Window w = getWindow();
//        w.setFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //(Html.fromHtml("<font color='#ff0000'>Settings</font>"))

//        final ListPreference listPreference = (ListPreference) findPreference("directionsType");
//
//        listPreference.setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(android.preference.Preference preference) {
//                Toast.makeText(MyPreferencesActivity.this, "changed",Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });

//                .setOnPreferenceChangeListener(new android.preference.Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(android.preference.Preference preference, Object o) {
//
//                //CharSequence currText = listPreference.getEntry();
//                //String currValue = listPreference.getValue();
//
//
//
//                return false;
//            }
//
//        });

       // ActionBar ad= getSupport
       // actionBar.setTitle("Settings");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // this takes the user 'back', as if they pressed the left-facing triangle icon on the main android toolbar.
                // if this doesn't work as desired, another possibility is to call `finish()` here.
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        //NavUtils.navigateUpFromSameTask(this);
        Intent intent= new Intent(MyPreferencesActivity.this,MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.backright,
                R.anim.backleft);
        //finish();
    }

//    @Override
//    public boolean onPreferenceChange(Preference preference, Object o) {
//
//
//        SharedPreferences sharedPref = MyPreferencesActivity.this.getPreferences(Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putBoolean("parkverse_status", directionsStatus);
//        editor.commit();
//
//
//        CharSequence currText = listPreference.getEntry();
//        String currValue = listPreference.getValue();
//
//        //if(currValue >= 0)
//            Toast.makeText(MyPreferencesActivity.this, currValue,Toast.LENGTH_SHORT).show();
//
//        return false;
//    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);





        }


    }



}
