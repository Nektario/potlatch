package com.mocca_capstone.potlatch.network;

import java.io.Serializable;

/**
 * Created by nektario on 10/20/2014.
 */
public interface ApiCommand extends Serializable {
    public void execute(GiftApiClient apiClient);
}
