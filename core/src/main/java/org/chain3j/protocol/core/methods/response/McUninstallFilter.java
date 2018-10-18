package org.chain3j.protocol.core.methods.response;

import org.chain3j.protocol.core.Response;

/**
 * eth_uninstallFilter.
 */
public class McUninstallFilter extends Response<Boolean> {
    public boolean isUninstalled() {
        return getResult();
    }
}
