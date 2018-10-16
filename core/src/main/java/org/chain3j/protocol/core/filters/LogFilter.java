package org.chain3j.protocol.core.filters;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.chain3j.protocol.chain3j;
import org.chain3j.protocol.core.Request;
import org.chain3j.protocol.core.methods.response.EthFilter;
import org.chain3j.protocol.core.methods.response.EthLog;
import org.chain3j.protocol.core.methods.response.Log;

/**
 * Log filter handler.
 */
public class LogFilter extends Filter<Log> {

    private final org.chain3j.protocol.core.methods.request.EthFilter ethFilter;

    public LogFilter(
            chain3j chain3j, Callback<Log> callback,
            org.chain3j.protocol.core.methods.request.EthFilter ethFilter) {
        super(chain3j, callback);
        this.ethFilter = ethFilter;
    }


    @Override
    EthFilter sendRequest() throws IOException {
        return chain3j.ethNewFilter(ethFilter).send();
    }

    @Override
    void process(List<EthLog.LogResult> logResults) {
        for (EthLog.LogResult logResult : logResults) {
            if (logResult instanceof EthLog.LogObject) {
                Log log = ((EthLog.LogObject) logResult).get();
                callback.onEvent(log);
            } else {
                throw new FilterException(
                        "Unexpected result type: " + logResult.get() + " required LogObject");
            }
        }
    }

    @Override
    protected Optional<Request<?, EthLog>> getFilterLogs(BigInteger filterId) {
        return Optional.of(chain3j.ethGetFilterLogs(filterId));
    }
}
