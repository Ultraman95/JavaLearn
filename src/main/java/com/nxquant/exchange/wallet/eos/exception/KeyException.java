package com.nxquant.exchange.wallet.eos.exception;

public class KeyException extends EOSException {

    public KeyException(String message) {
        super(message);
    }

    public KeyException(String message, Throwable cause) {
        super(message, cause);
    }

}
