package org.chain3j.protocol.core.methods.response;

import org.chain3j.protocol.core.Response;

/**
 * eth_submitWork.
 */
public class McSubmitWork extends Response<Boolean> {

    public boolean solutionValid() {
        return getResult();
    }
}
