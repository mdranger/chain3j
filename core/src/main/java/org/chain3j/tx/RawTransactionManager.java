package org.chain3j.tx;

import java.io.IOException;
import java.math.BigInteger;

import org.chain3j.crypto.Credentials;
import org.chain3j.crypto.Hash;
import org.chain3j.crypto.RawTransaction;
import org.chain3j.crypto.TransactionEncoder;
import org.chain3j.protocol.chain3j;
import org.chain3j.protocol.core.DefaultBlockParameterName;
import org.chain3j.protocol.core.methods.response.EthGetTransactionCount;
import org.chain3j.protocol.core.methods.response.EthSendTransaction;
import org.chain3j.tx.exceptions.TxHashMismatchException;
import org.chain3j.tx.response.TransactionReceiptProcessor;
import org.chain3j.utils.Numeric;
import org.chain3j.utils.TxHashVerifier;

/**
 * TransactionManager implementation using Ethereum wallet file to create and sign transactions
 * locally.
 *
 * <p>This transaction manager provides support for specifying the chain id for transactions as per
 * <a href="https://github.com/ethereum/EIPs/issues/155">EIP155</a>.
 */
public class RawTransactionManager extends TransactionManager {

    private final chain3j chain3j;
    final Credentials credentials;

    private final byte chainId;

    protected TxHashVerifier txHashVerifier = new TxHashVerifier();

    public RawTransactionManager(chain3j chain3j, Credentials credentials, byte chainId) {
        super(chain3j, credentials.getAddress());

        this.chain3j = chain3j;
        this.credentials = credentials;

        this.chainId = chainId;
    }

    public RawTransactionManager(
            chain3j chain3j, Credentials credentials, byte chainId,
            TransactionReceiptProcessor transactionReceiptProcessor) {
        super(transactionReceiptProcessor, credentials.getAddress());

        this.chain3j = chain3j;
        this.credentials = credentials;

        this.chainId = chainId;
    }

    public RawTransactionManager(
            chain3j chain3j, Credentials credentials, byte chainId, int attempts, long sleepDuration) {
        super(chain3j, attempts, sleepDuration, credentials.getAddress());

        this.chain3j = chain3j;
        this.credentials = credentials;

        this.chainId = chainId;
    }

    public RawTransactionManager(chain3j chain3j, Credentials credentials) {
        this(chain3j, credentials, ChainId.NONE);
    }

    public RawTransactionManager(
            chain3j chain3j, Credentials credentials, int attempts, int sleepDuration) {
        this(chain3j, credentials, ChainId.NONE, attempts, sleepDuration);
    }

    protected BigInteger getNonce() throws IOException {
        EthGetTransactionCount ethGetTransactionCount = chain3j.ethGetTransactionCount(
                credentials.getAddress(), DefaultBlockParameterName.PENDING).send();

        return ethGetTransactionCount.getTransactionCount();
    }

    public TxHashVerifier getTxHashVerifier() {
        return txHashVerifier;
    }

    public void setTxHashVerifier(TxHashVerifier txHashVerifier) {
        this.txHashVerifier = txHashVerifier;
    }

    @Override
    public EthSendTransaction sendTransaction(
            BigInteger gasPrice, BigInteger gasLimit, String to,
            String data, BigInteger value) throws IOException {

        BigInteger nonce = getNonce();

        RawTransaction rawTransaction = RawTransaction.createTransaction(
                nonce,
                gasPrice,
                gasLimit,
                to,
                value,
                data);

        return signAndSend(rawTransaction);
    }

    public EthSendTransaction signAndSend(RawTransaction rawTransaction)
            throws IOException {

        byte[] signedMessage;

        if (chainId > ChainId.NONE) {
            signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId, credentials);
        } else {
            signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        }

        String hexValue = Numeric.toHexString(signedMessage);
        EthSendTransaction ethSendTransaction = chain3j.ethSendRawTransaction(hexValue).send();

        if (ethSendTransaction != null && !ethSendTransaction.hasError()) {
            String txHashLocal = Hash.sha3(hexValue);
            String txHashRemote = ethSendTransaction.getTransactionHash();
            if (!txHashVerifier.verify(txHashLocal, txHashRemote)) {
                throw new TxHashMismatchException(txHashLocal, txHashRemote);
            }
        }

        return ethSendTransaction;
    }
}