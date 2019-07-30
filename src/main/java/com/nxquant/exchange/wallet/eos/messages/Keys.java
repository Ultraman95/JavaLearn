package com.nxquant.exchange.wallet.eos.messages;

import java.util.List;

public class Keys {

    public List<KeyPair> keys;

    public static class KeyPair{
        public String publicKey;
        public String privateKey;

        public KeyPair(String publicKey, String privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }
    }
}
