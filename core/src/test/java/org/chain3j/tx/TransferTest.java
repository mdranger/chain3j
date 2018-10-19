package org.chain3j.tx;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import org.chain3j.crypto.Credentials;
import org.chain3j.crypto.SampleKeys;
import org.chain3j.protocol.Chain3j;
import org.chain3j.protocol.core.Request;
import org.chain3j.protocol.core.methods.response.McGasPrice;
import org.chain3j.protocol.core.methods.response.TransactionReceipt;
import org.chain3j.utils.Convert;
import org.chain3j.utils.TxHashVerifier;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
// import static org.mockito.Matchers.any;
// import static org.mockito.Mockito.mock;
// import static org.mockito.Mockito.when;

public class TransferTest extends ManagedTransactionTester {

    protected TransactionReceipt transactionReceipt;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        transactionReceipt = prepareTransfer();
    }

    @Test
    public void testSendFunds() throws Exception {
        assertThat(sendFunds(SampleKeys.CREDENTIALS, ADDRESS,
                BigDecimal.TEN, Convert.Unit.ETHER),
                is(transactionReceipt));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testTransferInvalidValue() throws Exception {
        sendFunds(SampleKeys.CREDENTIALS, ADDRESS,
                new BigDecimal(0.1), Convert.Unit.WEI);
    }

    protected TransactionReceipt sendFunds(Credentials credentials, String toAddress,
                                           BigDecimal value, Convert.Unit unit) throws Exception {
        return new Transfer(chain3j, getVerifiedTransactionManager(credentials))
                .sendFunds(toAddress, value, unit).send();
    }
}
