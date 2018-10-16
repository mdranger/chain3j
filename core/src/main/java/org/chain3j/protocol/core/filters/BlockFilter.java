package org.chain3j.protocol.core.filters;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.chain3j.protocol.chain3j;
import org.chain3j.protocol.core.Request;
import org.chain3j.protocol.core.methods.response.EthFilter;
import org.chain3j.protocol.core.methods.response.EthLog;

/**
 * Handler for working with block filter requests.
 */
public class BlockFilter extends Filter<String> {

    public BlockFilter(chain3j chain3j, Callback<String> callback) {
        super(chain3j, callback);
    }

    @Override
    EthFilter sendRequest() throws IOException {
        return chain3j.ethNewBlockFilter().send();
    }

    @Override
    void process(List<EthLog.LogResult> logResults) {
        for (EthLog.LogResult logResult : logResults) {
            if (logResult instanceof EthLog.Hash) {
                String blockHash = ((EthLog.Hash) logResult).get();
                callback.onEvent(blockHash);
            } else {
                throw new FilterException(
                        "Unexpected result type: " + logResult.get() + ", required Hash");
            }
        }
    }

    /**
     * Since the block filter does not support historic filters, the filterId is ignored
     * and an empty optional is returned.
     * @param filterId
     * Id of the filter for which the historic log should be retrieved
     * @return
     * Optional.empty()
     */
    @Override
    protected Optional<Request<?, EthLog>> getFilterLogs(BigInteger filterId) {
        return Optional.empty();
    }
}

