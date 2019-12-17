package com.nxquant.exchange.base.core.work.exception;

import com.lmax.disruptor.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisruptorExceptionHandler implements ExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void handleEventException(final Throwable ex, final long sequence, final Object event) {
        logger.error("Disruptor Error : sequence={}, event={}", sequence, event, ex);
    }

    @Override
    public void handleOnStartException(final Throwable ex) {
        logger.error("Disruptor Error : during onStart()", ex);
    }

    @Override
    public void handleOnShutdownException(final Throwable ex) {
        logger.error("Disruptor Error : during onShutdown()", ex);
    }
}
