package com.nxquant.exchange.wallet.cli;

import org.apache.commons.cli.*;

/**
 * Created by Administrator on 2018-09-20.
 */
public class EthTool {
    private static Options options = new Options();
    private static String ETH_SYMBOL = "eth";
    private static String ERC20_SYMBOL = "erc20";
    private static String TRANSFER_SYMBOL = "transfer";
    private static String QUERY_SYMBOL = "query";

    private static int ETH_FLAG= 1;
    private static int ERC20_FLAG = 2;
    private static int TRANSFER_FLAG = 1;
    private static int QUERY_FLAG = 2;

    private static long REMAINING_VALUE = 0;

    private static Erc20Collect erc20Collect = new Erc20Collect();

    public static void initialCommand(){
        options.addOption("h", "help", false, "show help");
        options.addOption("t", "type", true, "[required] value: com.unifex.chainapi.eth|erc20");
        options.addOption("o", "operator", true, "[required] value: transfer|query");
        options.addOption("r", "remaining", true, "[option] remaining value in transfer erc20, digital value, default is 0");
    }

    private static void help() {
        // This prints out some help
        HelpFormatter formater = new HelpFormatter();
        formater.printHelp("ethtool", options);
        System.exit(0);
    }

    private static int parseType(String cmd) {

        if(cmd.compareTo(ETH_SYMBOL) == 0){
            return ETH_FLAG;
        }

        if(cmd.compareTo(ERC20_SYMBOL) == 0){
            return ERC20_FLAG;
        }

        return -1;
    }

    private static int parseOperator(String cmd) {
        if(cmd.compareTo(TRANSFER_SYMBOL) == 0){
            return TRANSFER_FLAG;
        }

        if(cmd.compareTo(QUERY_SYMBOL) == 0){
            return QUERY_FLAG;
        }

        return -1;
    }

    private static void parseRemaining(String cmd) {
        try{
            REMAINING_VALUE = Long.valueOf(cmd);
            if(REMAINING_VALUE < 0 ){
                System.out.println("remaining value must >= 0 ");
                System.exit(0);
            }
        }catch (NumberFormatException ex){
            System.out.println(ex.getMessage());
            System.exit(0);
        }
    }

    public static void process(int type, int operator){
        if(operator == TRANSFER_FLAG){
            switch(type){
                case 1:{
                    System.out.println("start transferETH:");
                    erc20Collect.transferETH();
                    System.out.println("end transferETH:");
                    break;
                }
                case 2: {
                    System.out.println("start transferERC20:");
                    erc20Collect.transferERC20(String.valueOf(REMAINING_VALUE));
                    System.out.println("end transferERC20:");
                    break;
                }
                default: {
                    help();
                    break;
                }
            }

        } else if(operator == QUERY_FLAG) {
            switch(type){
                case 1:{
                    System.out.println("start queryETH:");
                    erc20Collect.queryETH();
                    System.out.println("end queryETH:");
                    break;
                }
                case 2: {
                    System.out.println("start queryErc20:");
                    erc20Collect.queryErc20();
                    System.out.println("end queryErc20:");
                    break;
                }
                default: {
                    help();
                    break;
                }
            }
        } else {
            help();
        }
    }

   // private static String[] args1 = { "-t", "com.unifex.chainapi.eth","-o", "query" };

    public static void main(String[] args) {
        initialCommand();
        CommandLineParser parser = new BasicParser();
        CommandLine cmd = null;
        int type = -1;
        int operator = -1;
        REMAINING_VALUE = 0;

        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption("h")){
                help();
            }

            if (cmd.hasOption("t")){
                String str = cmd.getOptionValue("t");
                type = parseType(str);
            } else{
                help();
            }

            if (cmd.hasOption("o")){
                String str = cmd.getOptionValue("o");
                operator = parseOperator(str);
            }else{
                help();
            }

            if (cmd.hasOption("r")){
                String str = cmd.getOptionValue("r");
                parseRemaining(str);
            }

            if(type < 0 || operator < 0){
                help();
            }

            process(type,operator);
        }catch (ParseException e) {
            help();
        }
    }
}
