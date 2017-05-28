package com.github.jeffersonschmitt.jksttfilmes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.github.jeffersonschmitt.jksttfilmes.service.FilmesIntentService;

/**
 * Created by Jefferson Schmitt on 28/05/2017.
 */

public class FilmesReceiver extends BroadcastReceiver {

  @Override public void onReceive(Context context, Intent intent) {

    Intent intentService= new Intent(context, FilmesIntentService.class);
    context.startService(intentService);

  }
}
