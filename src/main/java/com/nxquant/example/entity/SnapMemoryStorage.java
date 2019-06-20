package com.nxquant.example.entity;

public class SnapMemoryStorage {

    private MemoryStorage memoryStorage;

    /**
     * 上一个全量快照的位置
     */
    private long lastSnapOffset;

    /**
     * 打此全量快照时，对应数据输入源的Offset
     */
    private long sourceOffset;
}
