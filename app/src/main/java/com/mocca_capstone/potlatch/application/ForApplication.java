/**
 * Created by nektario on 7/30/2014.
 */

package com.mocca_capstone.potlatch.application;

import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Qualifier
@Retention(RUNTIME)
public @interface ForApplication {
}

