package com.nxquant.exchange.wallet.cli.ethtx;

import com.nxquant.exchange.wallet.cli.Erc20Collect;
import com.nxquant.exchange.wallet.eth.ERC20Api;
import com.nxquant.exchange.wallet.eth.EthApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Properties;

public class Erc20Send {
    private static Logger logger = LoggerFactory.getLogger(Erc20Send.class);
    private static Properties props = new Properties();
    private static EthApi ethApi = new EthApi();
    private static com.nxquant.exchange.wallet.eth.ERC20Api ERC20Api = new ERC20Api();
    private static String ethip;

    private static String ethTxPath;
    private static String ethPwdPath;
    private BufferedWriter out;

    private static String contractName;

    private ArrayList<TxInfo> txs = new ArrayList<TxInfo>();
    private ArrayList<AddrPwd> addrPwds = new ArrayList<AddrPwd>();
    private HashMap<String, String> addrPwdMap = new  HashMap<String, String>();

    private TxInfoFile txInfoFile = new TxInfoFile();

    private static double Erc20TransferFee = -1.0;

    static {
        InputStream stream;
        try {
            stream = new FileInputStream("erc20send.properties");
            // System.out.println("init config success");
        } catch (FileNotFoundException e1) {
            stream = Erc20Collect.class.getClassLoader().getResourceAsStream("erc20send.properties");
        }

        try {
            props.load(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ethip = props.getProperty("ETHServer");
        ethTxPath = props.getProperty("Erc20TxPath");
        ethPwdPath = props.getProperty("EthPwdPath");
        contractName = props.getProperty("ContractName");
        Erc20TransferFee = Double.valueOf(props.getProperty("Erc20TransferFee"));

        Boolean ret = ethApi.createConnection(ethip);
        ERC20Api.setEthApi(ethApi);
    }

    public boolean loadData(){
        txs = txInfoFile.readTxFile(ethTxPath);
        addrPwds = txInfoFile.readPwdFile(ethPwdPath);
        for(AddrPwd addrPwd : addrPwds){
            String addr = addrPwd.getAddress();
            String pwd = addrPwd.getPwd();
            addrPwdMap.put(addr,pwd);
        }

        Calendar calendar= Calendar.getInstance();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String date =  dateFormat.format(calendar.getTime());

        String filePath = "./transaction/tx" + date;
        txInfoFile.createFile(filePath);

        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, true)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return txs.size() > 0;
    }

    public boolean sendTx(TxInfo txInfo){
        String pwd = addrPwdMap.get(txInfo.getFromAddress());
        if(pwd == null){
            String errmsg = "pwd empty:"+ txInfo.getFromAddress() + ", txid: " +txInfo.getTxid()+ ", ErrMsg:"+ ERC20Api.getErrorMsg()+"\n";
            txInfoFile.writeContent(out, errmsg);
            logger.error(errmsg);
            return false;
        }

        boolean ret = ERC20Api.getEthApi().unlockAccount(txInfo.getFromAddress(), pwd);
        if(ret == false){
            String errmsg = "failed unlock :"+txInfo.getFromAddress() + ", txid: " +txInfo.getTxid()+ ", ErrMsg:"+ ERC20Api.getErrorMsg()+"\n";
            txInfoFile.writeContent(out, errmsg);
            logger.error(errmsg);
            return false;
        }

        BigDecimal amount = new BigDecimal(txInfo.getAmount()).multiply( (  new BigDecimal(String.valueOf(10))).pow(18) ).setScale(0, BigDecimal.ROUND_DOWN);
        BigInteger amountLong = new BigInteger(amount.toString());

        String txid = ERC20Api.transfer(txInfo.getFromAddress(), contractName, txInfo.getToAddress(), amountLong, Erc20TransferFee);
        if(txid == null){
            String errmsg = "ERC20 tx:  from: " +txInfo.getFromAddress() +", to: "+txInfo.getToAddress()+", balance: "+ txInfo.getAmount() +", ErrMsg: "+ ERC20Api.getErrorMsg()+"\n";
            txInfoFile.writeContent(out, errmsg);
            logger.error(errmsg);
            return false;
        }

        String succmsg = txInfo.getFromAddress() + "," + txInfo.getToAddress()+  "," +txInfo.getAmount() + ","+txInfo.getTxid()+ ","+txid+"\n";

        txInfoFile.writeContent(out, succmsg);

        return true;
    }

    public void resendTx(){
        System.out.println("transaction  start !!!!!" );
        int i = 1;
        for(TxInfo txInfo :txs){
            String msg = "from:" +txInfo.getFromAddress() +", to:"+txInfo.getToAddress()+", balance:"+txInfo.getAmount() + ", origin txid:" + txInfo.getTxid();
            System.out.println("the: " + i + " transaction, sending : " + msg);
            if(sendTx(txInfo) == false){
                String errmsg = "error ERC20:,  from:" +txInfo.getFromAddress() +", to:"+txInfo.getToAddress()+", balance:"+txInfo.getAmount() + ", origin txid:" + txInfo.getTxid();
                logger.error(errmsg);
            }

            i++;
            try{
                Thread.sleep(500);
            }catch (Exception ex){
                ex.printStackTrace();
            }

        }

        System.out.println("transaction  end !!!!!" );
    }

    public static void main(String[] args) {
        Erc20Send erc20Send = new Erc20Send();
        erc20Send.loadData();
        erc20Send.resendTx();
    }
}
