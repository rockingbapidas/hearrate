package com.vantagecircle.heartrate.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Created by bapidas on 27/11/17.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface HeartData {

}
