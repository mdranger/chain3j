package org.chain3j.crypto;

import org.chain3j.crypto.Credentials;
import org.chain3j.crypto.Hash;
import org.chain3j.crypto.Sign;
import org.chain3j.rlp.RlpDecoder;
import org.chain3j.rlp.RlpEncoder;
import org.chain3j.rlp.RlpList;
import org.chain3j.rlp.RlpString;
import org.chain3j.rlp.RlpType;
import org.chain3j.utils.Bytes;
import org.chain3j.utils.Numeric;


import java.util.ArrayList;
import java.util.List;

/**
 * Create RLP encoded transaction, implementation as per p4 of the
 * <a href="http://gavwood.com/paper.pdf">yellow paper</a>.
 * Added the 
 */
public class TransactionEncoder {

    //All MOAC RawTransaction requires the chainId in the input
    public static byte[] signMessage(
        RawTransaction rawTransaction, Integer chainId, Credentials credentials) throws CipherException {
   
        //Check if the input chainId is the same as the Transaction, if not, return error
        if (chainId != rawTransaction.getChainId()){
            throw new CipherException("Mismatch chainId in the rawTransaction!");
        }
        return signMessage(rawTransaction, credentials);
    }

    public static byte[] signMessage(RawTransaction rawTransaction, Credentials credentials) {

        byte[] encodedTransaction = encode(rawTransaction);

        //The SignatureData process will hash the input encodedTransaction
        // and sign with private key
        Sign.SignatureData signatureData = Sign.signMessage(
                encodedTransaction, credentials.getEcKeyPair());
        // Note, if the input is true, more hash will be done 
                // byte[] hashtrans = Hash.sha3(encodedTransaction);
        // Sign.SignatureData signatureData = Sign.signMessage(
        //         hashtrans, credentials.getEcKeyPair(), false);

        byte id = rawTransaction.getChainId().byteValue();//.toByteArray()[0];
        //int encodeV = signatureData.getV() + 8 + id * 2;
        byte v = (byte) (signatureData.getV() + (id << 1) + 8);
        Sign.SignatureData sdata = new Sign.SignatureData(
                v, signatureData.getR(), signatureData.getS());

        RlpList decode = (RlpList) RlpDecoder.decode(encodedTransaction).getValues().get(0);

        List<RlpType> list = decode.getValues().subList(0, 9);
        list.add(9, RlpString.create(sdata.getV()));
        list.add(10, RlpString.create(Bytes.trimLeadingZeroes(sdata.getR())));
        list.add(11, RlpString.create(Bytes.trimLeadingZeroes(sdata.getS())));

        RlpList rlpList = new RlpList(list);

        // byte [] result = RlpEncoder.encode(rlpList);
        // String hexRes = Numeric.toHexString(result);

        return RlpEncoder.encode(rlpList);
    }

    // private static byte[] encode(RawTransaction rawTransaction) {
    //     List<RlpType> values = asRlpValues(rawTransaction);
    //     RlpList rlpList = new RlpList(values);
    //     return RlpEncoder.encode(rlpList);
    // }
    private static byte[] encode(RawTransaction rawTransaction, Sign.SignatureData signatureData) {
        List<RlpType> values = asRlpValues(rawTransaction, signatureData);
        RlpList rlpList = new RlpList(values);
        return RlpEncoder.encode(rlpList);
    }

    //Encode without signature
    public static byte[] encode(RawTransaction rawTransaction) {
        return encode(rawTransaction, null);
    }

    //Encode with signature 
    public static byte[] encode(RawTransaction rawTransaction, byte chainId) {
        Sign.SignatureData signatureData = new Sign.SignatureData(
                chainId, new byte[] {}, new byte[] {});
        return encode(rawTransaction, signatureData);
    }

    static List<RlpType> asRlpValues(
            RawTransaction rawTransaction, Sign.SignatureData signatureData) {
        List<RlpType> result = new ArrayList<>();

        result.add(RlpString.create(rawTransaction.getNonce()));
        result.add(RlpString.create(rawTransaction.getGasPrice()));
        result.add(RlpString.create(rawTransaction.getGasLimit()));

        // an empty to address (contract creation) should not be encoded as a numeric 0 value
        String to = rawTransaction.getTo();
        if (to != null && to.length() > 0) {
            // addresses that start with zeros should be encoded with the zeros included, not
            // as numeric values
            result.add(RlpString.create(Numeric.hexStringToByteArray(to)));
        } else {
            result.add(RlpString.create(""));
        }

        result.add(RlpString.create(rawTransaction.getValue()));

        // value field will already be hex encoded, so we need to convert into binary first
        byte[] data = Numeric.hexStringToByteArray(rawTransaction.getData());
        result.add(RlpString.create(data));

        // Get sharding Flag
        result.add(RlpString.create(0));//result.add(RlpString.create(rawTransaction.getShardingFlag()));
        result.add(RlpString.create(Numeric.hexStringToByteArray("")));//result.add(RlpString.create(rawTransaction.getVia()));
        result.add(RlpString.create(rawTransaction.getChainId()));
        result.add(RlpString.create(0));//result.add(RlpString.create(rawTransaction.getVia()));
        result.add(RlpString.create(0));//result.add(RlpString.create(rawTransaction.getVia()));

        if (signatureData != null) {
            result.add(RlpString.create(signatureData.getV()));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getR())));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getS())));
        }

        return result;
    }

    static List<RlpType> asRlpValues(RawTransaction rawTransaction) {
        List<RlpType> result = new ArrayList<RlpType>();

        result.add(RlpString.create(rawTransaction.getNonce()));
        result.add(RlpString.create(""));//result.add(RlpString.create(rawTransaction.getSystemContract()));
        result.add(RlpString.create(rawTransaction.getGasPrice()));
        result.add(RlpString.create(rawTransaction.getGasLimit()));

        // an empty to address (contract creation) should not be encoded as a numeric 0 value
        String to = rawTransaction.getTo();
        if (to != null && to.length() > 0) {
            // addresses that start with zeros should be encoded with the zeros included, not
            // as numeric values
            result.add(RlpString.create(Numeric.hexStringToByteArray(to)));
        } else {
            result.add(RlpString.create(""));
        }

        result.add(RlpString.create(rawTransaction.getValue()));

        // value field will already be hex encoded, so we need to convert into binary first
        byte[] data = Numeric.hexStringToByteArray(rawTransaction.getData());
        result.add(RlpString.create(data));

        result.add(RlpString.create(0));//result.add(RlpString.create(rawTransaction.getShardingFlag()));
        result.add(RlpString.create(Numeric.hexStringToByteArray("")));//result.add(RlpString.create(rawTransaction.getVia()));
        result.add(RlpString.create(rawTransaction.getChainId()));
        result.add(RlpString.create(0));//result.add(RlpString.create(rawTransaction.getVia()));
        result.add(RlpString.create(0));//result.add(RlpString.create(rawTransaction.getVia()));
        return result;
    }
}
