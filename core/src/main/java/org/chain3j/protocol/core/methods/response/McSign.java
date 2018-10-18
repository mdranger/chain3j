package org.chain3j.protocol.core.methods.response;

import org.chain3j.protocol.core.Response;

/**
 * eth_sign.
 */
public class McSign extends Response<String> {
    public String getSignature() {
        return getResult();
    }
}
