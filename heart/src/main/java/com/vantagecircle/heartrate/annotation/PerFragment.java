package com.vantagecircle.heartrate.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by bapidas on 08/11/17.
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PerFragment {

}
