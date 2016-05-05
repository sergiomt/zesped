package com.zesped.util;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class SilentAuthenticator extends Authenticator {
  private PasswordAuthentication oPwdAuth;

  public SilentAuthenticator(String sUserName, String sAuthStr) {
    oPwdAuth = new PasswordAuthentication(sUserName, sAuthStr);
  }

  protected PasswordAuthentication getPasswordAuthentication() {
    return oPwdAuth;
  }
}
