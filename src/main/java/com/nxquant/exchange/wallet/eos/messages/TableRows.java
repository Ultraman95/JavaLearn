package com.nxquant.exchange.wallet.eos.messages;

import java.util.List;
import java.util.Map;

public class TableRows {
    public List<Map<String, Object>> rows ;
    public boolean more;

    @Override
    public String toString() {
        return "TableRows{" +
                "rows=" + rows +
                ", more=" + more +
                '}';
    }
}
