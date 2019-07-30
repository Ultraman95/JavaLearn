package com.nxquant.exchange.wallet.ripple;

import com.nxquant.exchange.wallet.model.BlockInfo;
import com.nxquant.exchange.wallet.model.RippleAccountModel;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.math.BigDecimal;

public class TestRipple {
    // offical test net
   private String testFaucet = "http://192.168.44.163:5005";
    //private String testFaucet = "http://118.89.148.178:5005";
    private String genesisAccount = "rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh";
    private String genesisSecret = "snoPBrXtMeMyMHUVTgbuqAfg1SUTb";

    private String masterAccount = "rUfSWLedmvBJTSdj4xB9GgbGX5LuayXVkt";
    private String masterSecret = "snHkm3DxxjMgcVjzbjd3X23bfRQNZ";
    private String masterPubKey = "aB4qgdMQPQaTUqh2aSrw5AY1n81YdtQxnTiaRmihvfxsJspVPRXq";

    private String regularAccount = "rEjNqD5YKqTjEmapN1ByFomThN2r7UXzWs";
    private String regularSecret = "spAz3tuVmUz4ynZiDWL3TQUakA6ei";
    private String regularPubKey = "aB4TwRhYixT2qbbDKAXPFmzACWVFhKKuTDJtgLEjLAoau5kpx2ez";

    @Test
    public void testCreateConnection() {
        RippleApi ripple = new RippleApi();
        Boolean connected = ripple.createConnection(testFaucet);
        if (!connected) {
            System.out.println("Connection failure: "+ripple.getErrorMsg());
            return;
        }
        long index = ripple.getLatestBlockNum();
        if (index < 0) {
            System.out.println("Error: "+ripple.getErrorMsg());
        } else {
            System.out.println(index);
        }
    }

    @Test
    public void testGetBlockByNumber() {
        RippleApi ripple = new RippleApi();
        Boolean connected = ripple.createConnection(testFaucet);
        if (!connected) {
            System.out.println("Connection failure: "+ripple.getErrorMsg());
            return;
        }
        ArrayList<BlockInfo> blocks = ripple.getBlockByNumber(14);
        if (blocks == null) {
            System.out.println(ripple.getErrorMsg());
        }
        System.out.println(blocks);
    }

    @Test
    public void testGetBalance() {
        RippleApi ripple = new RippleApi();
        if (!ripple.createConnection(testFaucet)) {
            System.out.println("Connection failure: "+ripple.getErrorMsg());
            return;
        }
        BigDecimal balance = ripple.getAccountBalance("rat9LQuVXn7rRodCFW7uKqMpEes1ExuhTP");
        if (balance == null) {
            System.out.println("Error: "+ripple.getErrorMsg());
        } else {
            System.out.println("Info: balance of "+balance);
        }
    }

    @Test
    public void testCreateNewAddress() {
        RippleApi ripple = new RippleApi();
        if (!ripple.createConnection(testFaucet)) {
            System.out.println("Connection failure: "+ripple.getErrorMsg());
            return;
        }
        RippleAccountModel address = ripple.createNewAddress("");
        // assertEquals("Error: unmatched address ", masterAccount, address);
        System.out.println("Info: generated address of "+address.getAccount());
    }

    @Test
    public void testSetAccountForAddress() {
        RippleApi ripple = new RippleApi();
        if (!ripple.createConnection(testFaucet)) {
            System.out.println("Connection failure: "+ripple.getErrorMsg());
            return;
        }
        Boolean status = ripple.setAccountForAddress("r3UQtLkmjEKza5m3b4mWjgA4j5wEdjSxrD", "sszZwyiQKRwdRQMSUDuVEKRRkuop6");
        if (!status.booleanValue()) {
            System.out.println("Error: "+ripple.getErrorMsg());
        }
    }

    @Test
    public void testTransfer() {
        RippleApi ripple = new RippleApi();
        if (!ripple.createConnection(testFaucet)) {
            System.out.println("Connection failure: "+ripple.getErrorMsg());
            return;
        }

        String hash = ripple.transfer("rpZ3yuZprt4H1cAwmmPkpakc3pu3axi8AM", "rs7ix6S8ou8LMTDCdAiBN2o8cNXKPPBp4g", "ss4Bu4j6UevjdFEmMp3Dj5pA4TcN3", 20.0, 10.);
        if (hash == null) {
            System.out.println("Error: "+ripple.getErrorMsg());
        } else {
            System.out.println("Info: transaction hash of "+hash);
        }
    }
}
