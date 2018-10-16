package org.chain3j.protocol;

import java.util.concurrent.ScheduledExecutorService;

import org.chain3j.protocol.core.Ethereum;
import org.chain3j.protocol.core.JsonRpc2_0Web3j;
import org.chain3j.protocol.rx.Web3jRx;

/**
 * JSON-RPC Request object building factory.
 */
public interface chain3j extends Ethereum, Web3jRx {

    /**
     * Construct a new chain3j instance.
     *
     * @param web3jService chain3j service instance - i.e. HTTP or IPC
     * @return new chain3j instance
     */
    static chain3j build(Web3jService web3jService) {
        return new JsonRpc2_0Web3j(web3jService);
    }

    /**
     * Construct a new chain3j instance.
     *
     * @param web3jService chain3j service instance - i.e. HTTP or IPC
     * @param pollingInterval polling interval for responses from network nodes
     * @param scheduledExecutorService executor service to use for scheduled tasks.
     *                                 <strong>You are responsible for terminating this thread
     *                                 pool</strong>
     * @return new chain3j instance
     */
    static chain3j build(
            Web3jService web3jService, long pollingInterval,
            ScheduledExecutorService scheduledExecutorService) {
        return new JsonRpc2_0Web3j(web3jService, pollingInterval, scheduledExecutorService);
    }

    /**
     * Shutdowns a chain3j instance and closes opened resources.
     */
    void shutdown();
}
