package org.chain3j.protocol.core.methods.response;

import org.chain3j.protocol.core.Response;

/**
 * eth_getCode.
 */
public class McGetCode extends Response<String> {
    public String getCode() {
        return getResult();
    }
}
