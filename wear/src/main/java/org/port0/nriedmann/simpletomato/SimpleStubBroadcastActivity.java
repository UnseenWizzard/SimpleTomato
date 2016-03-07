package org.port0.nriedmann.simpletomato;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Example shell activity which simply broadcasts to our receiver and exits.
 */
public class SimpleStubBroadcastActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = new Intent();
        i.setAction("org.port0.nriedmann.simpletomato.SHOW_NOTIFICATION");
        i.putExtra(SimpleTomatoPostNotificationReceiver.CONTENT_KEY, getString(R.string.title));
        sendBroadcast(i);
        finish();
    }
}
