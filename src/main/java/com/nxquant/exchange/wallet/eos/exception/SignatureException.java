package com.nxquant.exchange.wallet.eos.exception;

public class SignatureException extends EOSException {

    public SignatureException(String message) {
        super(message);
    }

    public SignatureException(String message, Throwable cause) {
        super(message, cause);
    }

}
