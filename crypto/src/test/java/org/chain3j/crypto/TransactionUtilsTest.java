package org.chain3j.crypto;

import org.junit.Test;

import static org.chain3j.crypto.TransactionUtils.generateTransactionHashHexEncoded;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TransactionUtilsTest {
Integer chainId=101;
    @Test
    public void testGenerateTransactionHash() {
        assertThat(generateTransactionHashHexEncoded(
                TransactionEncoderTest.createContractTransaction(chainId), SampleKeys.CREDENTIALS),
                is("0xc30edd044a9a8da6efa6b1d589c7b0cf2fd349b637b010b3242c9897930d4c1e"));
    }

    @Test
    public void testGenerateEip155TransactionHash() throws CipherException{
        assertThat(generateTransactionHashHexEncoded(
                TransactionEncoderTest.createContractTransaction(chainId), 101,
                SampleKeys.CREDENTIALS),
                is("0x568c7f6920c1cee8332e245c473657b9c53044eb96ed7532f5550f1139861e9e"));
    }
}
