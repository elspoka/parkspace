package tech.crossroads.parkspace;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.SharedPreferencesBackupHelper;
import android.os.ParcelFileDescriptor;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Xristos on 23/1/2017.
 */


    public class MyBackupAgent extends BackupAgentHelper {
        static final String Prefs = "myPreferences";
        static final String PREFS_BACKUP_KEY = "50";

        @Override
        public void onCreate() {
            SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this,
                    Prefs);
            addHelper(PREFS_BACKUP_KEY, helper);
        }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
        //Log.d("MyBackups", "restoring");
        super.onRestore(data, appVersionCode, newState);
        // post-processing code goes here

        while (data.readNextHeader()) {
            String value = data.getKey();
            //int Size = data.getDataSize();

        }

        Toast.makeText(MyBackupAgent.this,"finished back up", Toast.LENGTH_SHORT).show();



    }
}
