package com.nxquant.exchange.wallet.cli.ethtx;

import java.io.*;
import java.util.ArrayList;

public class TxInfoFile {

    public ArrayList<TxInfo>  readTxFile(String path){

        ArrayList<TxInfo> txInfos = new ArrayList<TxInfo>();
        File file = new File(path);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;

            while ((tempString = reader.readLine()) != null) {

                String[] txdata = tempString.split(",");
                if(txdata == null || txdata.length != 4){
                    System.out.println("readTxFile error data in line");
                    continue;
                }

                TxInfo txInfo = new TxInfo();
                txInfo.setFromAddress(txdata[0]);
                txInfo.setToAddress(txdata[1]);
                txInfo.setAmount(txdata[2]);
                txInfo.setTxid(txdata[3]);

                txInfos.add(txInfo);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }

        return txInfos;
    }

    public  ArrayList<AddrPwd>  readPwdFile(String path){

        ArrayList<AddrPwd> addrPwds = new ArrayList<AddrPwd>();
        File file = new File(path);
        BufferedReader reader = null;
        try {

            reader = new BufferedReader(new FileReader(file));
            String tempString = null;

            while ((tempString = reader.readLine()) != null) {

                String[] txdata = tempString.split(",");
                if(txdata == null || txdata.length != 2){
                    System.out.println("readPwdFile error data in line");
                    continue;
                }

                AddrPwd addrPwd = new AddrPwd();
                addrPwd.setAddress(txdata[0]);
                addrPwd.setPwd(txdata[1]);

                addrPwds.add(addrPwd);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }

        return addrPwds;
    }

    public  void createFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            System.out.println("file exits");
        } else {
            try {
                File fileParent = file.getParentFile();
                if (fileParent != null) {
                    if (!fileParent.exists()) {
                        fileParent.mkdirs();
                    }
                }
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeContent(BufferedWriter out, String content){
        try {
            out.write(content);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
