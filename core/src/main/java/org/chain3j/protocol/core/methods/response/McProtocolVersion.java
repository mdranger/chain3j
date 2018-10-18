package org.chain3j.protocol.core.methods.response;

import org.chain3j.protocol.core.Response;

/**
 * eth_protocolVersion.
 */
public class McProtocolVersion extends Response<String> {
    public String getProtocolVersion() {
        return getResult();
    }
}
