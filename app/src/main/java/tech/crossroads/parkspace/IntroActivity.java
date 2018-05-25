package tech.crossroads.parkspace;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by Xristos on 11/2/2017.
 */

public class IntroActivity extends AppIntro {

    @Override
    public void onBackPressed() {
        //NavUtils.navigateUpFromSameTask(this);
        Intent intent= new Intent(IntroActivity.this,MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.backright,
                R.anim.backleft);
        //finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        addSlide(AppIntroFragment.newInstance("Parkspace", "Find free parking spots nearby shared in real time by other users with Parkspace",R.drawable.logo0512, ContextCompat.getColor(getApplicationContext(),R.color.intro)));
        addSlide(AppIntroFragment.newInstance("How it works", "Parkspace is based on the community of users that share parking spots in real time. Acquire your Parkspace Nfc Sticker to share parking spots easily by just placing the sticker in your car and scanning it when you want to share a parking spot", R.drawable.screenshot3, ContextCompat.getColor(getApplicationContext(),R.color.intro)));
        addSlide(AppIntroFragment.newInstance("More features", "Never forget again where you have parked your car by setting a Parking Reminder to locate it easily", R.drawable.screenshot4, ContextCompat.getColor(getApplicationContext(),R.color.intro)));
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
        startActivity(intent);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
        startActivity(intent);
        //overridePendingTransition(R.anim.right_to_left,                R.anim.left_to_right);
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

}
