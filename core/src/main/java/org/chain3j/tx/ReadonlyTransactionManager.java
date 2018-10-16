package org.chain3j.tx;

import java.io.IOException;
import java.math.BigInteger;

import org.chain3j.protocol.chain3j;
import org.chain3j.protocol.core.methods.response.EthSendTransaction;

/**
 * Transaction manager implementation for read-only operations on smart contracts.
 */
public class ReadonlyTransactionManager extends TransactionManager {

    public ReadonlyTransactionManager(chain3j chain3j, String fromAddress) {
        super(chain3j, fromAddress);
    }

    @Override
    public EthSendTransaction sendTransaction(
            BigInteger gasPrice, BigInteger gasLimit, String to, String data, BigInteger value)
            throws IOException {
        throw new UnsupportedOperationException(
                "Only read operations are supported by this transaction manager");
    }
}
