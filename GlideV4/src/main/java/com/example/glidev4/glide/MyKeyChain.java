package com.example.glidev4.glide;

import com.facebook.crypto.CryptoConfig;
import com.facebook.crypto.MacConfig;
import com.facebook.crypto.exception.KeyChainException;
import com.facebook.crypto.keychain.KeyChain;
import java.security.SecureRandom;
import java.util.Arrays;

public class MyKeyChain implements KeyChain {
  private final CryptoConfig mCryptoConfig = CryptoConfig.KEY_256;
  private final SecureRandom mSecureRandom;

  private byte[] mCipherKey;
  private boolean mSetCipherKey;
  private byte[] mMacKey;
  private boolean mSetMacKey;

  public MyKeyChain() {
    this.mSecureRandom = new SecureRandom();
  }

  @Override public byte[] getCipherKey() throws KeyChainException {
    if (!this.mSetCipherKey) {
      mCipherKey = Arrays.copyOfRange("abcabcabcabcabcabcabcabcabcabcabcabc".getBytes(), 0,
          mCryptoConfig.keyLength);
      this.mSetCipherKey = true;
    }
    return this.mCipherKey;
  }

  @Override public byte[] getMacKey() throws KeyChainException {
    if (!this.mSetMacKey) {
      this.mMacKey = Arrays.copyOfRange("abcabcabcabcabcabcabcabcabcabcabcabc".getBytes(), 0,
          MacConfig.DEFAULT.keyLength);
      this.mSetMacKey = true;
    }
    return this.mMacKey;
  }

  @Override public byte[] getNewIV() throws KeyChainException {
    byte[] iv = new byte[this.mCryptoConfig.ivLength];
    this.mSecureRandom.nextBytes(iv);
    return iv;
  }

  @Override public void destroyKeys() {

  }
}
