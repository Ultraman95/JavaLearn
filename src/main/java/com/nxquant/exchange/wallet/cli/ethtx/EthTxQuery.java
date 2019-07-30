package com.nxquant.exchange.wallet.cli.ethtx;

import com.nxquant.exchange.wallet.cli.Erc20Collect;
import com.nxquant.exchange.wallet.model.Receipt;
import com.nxquant.exchange.wallet.eth.ERC20Api;
import com.nxquant.exchange.wallet.eth.EthApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;

public class EthTxQuery {
    private static Logger logger = LoggerFactory.getLogger(EthTxQuery.class);
    private static Properties props = new Properties();
    private static EthApi ethApi = new EthApi();
    private static com.nxquant.exchange.wallet.eth.ERC20Api ERC20Api = new ERC20Api();
    private static String ethip;

    private static String ethTxPath;
    private  BufferedWriter out;

    private ArrayList<TxInfo> txs = new ArrayList<TxInfo>();

    private TxInfoFile txInfoFile = new TxInfoFile();

    static {
        InputStream stream;
        try {
            stream = new FileInputStream("ethtxquery.properties");
        } catch (FileNotFoundException e1) {
            stream = Erc20Collect.class.getClassLoader().getResourceAsStream("ethtxquery.properties");
        }

        try {
            props.load(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ethip = props.getProperty("ETHServer");
        ethTxPath = props.getProperty("EthTxPath");

        Boolean ret = ethApi.createConnection(ethip);
        ERC20Api.setEthApi(ethApi);
    }

    public boolean loadData(){
        txs = txInfoFile.readTxFile(ethTxPath);

        Calendar calendar= Calendar.getInstance();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String date =  dateFormat.format(calendar.getTime());

        String filePath = "./txdata/tx" + date;
        txInfoFile.createFile(filePath);

        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, true)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return txs.size() > 0;
    }

    public boolean query(String txid){
        Receipt receipt = ethApi.getReceipt(txid);
        if(receipt != null) {
            return true;
        }

        return false;
    }

    public void writetx(TxInfo txInfo){
        String content = txInfo.getFromAddress() + "," + txInfo.getToAddress() + "," + txInfo.getAmount() + ","+txInfo.getTxid()+"\n";
        txInfoFile.writeContent(this.out, content);
    }

    public void filterTx(){
        for(TxInfo txInfo :txs){
            if(query(txInfo.getTxid()) ==false){
                writetx(txInfo);
            }
        }
    }

    public static void main(String[] args) {
        EthTxQuery ethTxQuery = new EthTxQuery();
        ethTxQuery.loadData();
        ethTxQuery.filterTx();
    }
}
