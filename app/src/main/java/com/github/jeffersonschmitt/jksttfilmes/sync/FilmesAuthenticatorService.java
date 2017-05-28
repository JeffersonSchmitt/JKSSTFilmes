package com.github.jeffersonschmitt.jksttfilmes.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Jefferson Schmitt on 28/05/2017.
 */

public class FilmesAuthenticatorService extends Service {

  private FilmesAuthenticator filmesAuthenticator;

  @Override
  public void onCreate(){
    filmesAuthenticator=new FilmesAuthenticator(this);
  }


  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return filmesAuthenticator.getIBinder();
  }
}
