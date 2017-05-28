package com.github.jeffersonschmitt.jksttfilmes.sync;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;
import java.nio.channels.UnsupportedAddressTypeException;

/**
 * Created by Jefferson Schmitt on 28/05/2017.
 */

public class FilmesAuthenticator extends AbstractAccountAuthenticator {
  public FilmesAuthenticator(Context context) {
    super(context);
  }

  @Override
  public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
    throw new UnsupportedAddressTypeException();
  }

  @Override public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
      String authTokenType, String[] requiredFeatures, Bundle options)
      throws NetworkErrorException {
    return null;
  }

  @Override public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account,
      Bundle options) throws NetworkErrorException {
    return null;
  }

  @Override public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
      String authTokenType, Bundle options) throws NetworkErrorException {
    throw new UnsupportedAddressTypeException();
  }

  @Override public String getAuthTokenLabel(String authTokenType) {
    throw new UnsupportedAddressTypeException();
  }

  @Override public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account,
      String authTokenType, Bundle options) throws NetworkErrorException {
    throw new UnsupportedAddressTypeException();
  }

  @Override public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account,
      String[] features) throws NetworkErrorException {
    throw new UnsupportedAddressTypeException();
  }
}
