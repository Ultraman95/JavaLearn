package com.nxquant.exchange.match.core;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.LifecycleAware;
import com.lmax.disruptor.dsl.Disruptor;
//import com.js.trade.directive.CancelOrder;
//import com.js.trade.directive.UpdateOrder;
import com.nxquant.exchange.match.configure.WorkContext;
import com.nxquant.exchange.match.dto.*;
import com.nxquant.exchange.match.dto.Order;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

/**
 * @author shilf
 * kafka-partition消费线程
 */
public class KafKaPartitionWorker implements EventHandler<InputEventData>, LifecycleAware {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private RedoOffset redoOffset;
    private Disruptor<InputEventData> disruptor;
    private boolean disruptorStartStatus = false;

    private OrderBookManager orderBookManager = new OrderBookManager();
    private WorkContext workContext;
    private int receiveCount = 0;
    private KafkaProducer sendProducer;

    KafKaPartitionWorker(RedoOffset redoOffset, List<ExOrderBook> exOrderBookList, WorkContext workContext){
        this.redoOffset = redoOffset;
        this.workContext = workContext;
        orderBookManager.init(exOrderBookList);
        initDisruptor();
    }

    private void initDisruptor(){
        int ringBufferSize = 1 << 15;
        TopicPartition tp = this.redoOffset.getTopicPartition();
        this.disruptor = new Disruptor<>(InputEventData::new, ringBufferSize, new NamedThreadFactory(tp.topic() + "_" + tp.partition()));
        this.disruptor.handleEventsWith(this);
    }

    void start(){
        this.disruptor.start();
    }


    @Override
    public void onEvent(InputEventData eData, long sequence, boolean endOfBatch) {
        Info content = eData.getContent();
        boolean redo  = isReDo(eData.getOffset());
        if(content instanceof Order){
            Order order = (Order) content;
            this.workContext.getMatchService().insertOrder(order, redo, orderBookManager);
        }/*else if(content instanceof CancelOrder){
            CancelOrder cancelOrder = (CancelOrder) content;
            this.workContext.getMatchService().cancelOrder(cancelOrder, redo, orderBookManager);
        }else if(content instanceof UpdateOrder){
            UpdateOrder updateOrder = (UpdateOrder) content;
            this.workContext.getMatchService().updateOrder(updateOrder, redo, orderBookManager);
        }*/
        //sendMsg(endOfBatch);
    }

    void publishDisruptor(ConsumerRecord<String, Info> inputData) {
        long sequence = 0;
        try {
            sequence = this.disruptor.getRingBuffer().next();
            InputEventData eData = this.disruptor.getRingBuffer().get(sequence);
            eData.setData(inputData);
        } catch (Exception exp) {
            logger.error("Match_ERROR: publishDisruptor failure !", exp);
        } finally {
            this.disruptor.getRingBuffer().publish(sequence);
        }
    }

    private void sendMsg(boolean endOfBatch){
        try {
            receiveCount++;
            boolean isEnoughCount = receiveCount > this.workContext.getCommitEnoughCount();
            boolean isEnoughSize = orderBookManager.getRtnInfoList().size() > this.workContext.getCommitEnoughSize();
            if (isEnoughCount || isEnoughSize || endOfBatch) {
                this.sendProducer.beginTransaction();
                //send Msg
                this.sendProducer.commitTransaction();
                orderBookManager.clearRtnInfoList();
                receiveCount = 0;
            }
        }catch (Exception exp){
            logger.error("Match_ERROR: sendMsg Failure !", exp);
        }
    }

    private boolean isReDo(long offset){
        if(offset < redoOffset.getMaxOffset()){
            return true;
        }
        return false;
    }

    @Override
    public void onStart() {
        disruptorStartStatus = true;
    }

    @Override
    public void onShutdown() {
        disruptorStartStatus = false;
    }
}
