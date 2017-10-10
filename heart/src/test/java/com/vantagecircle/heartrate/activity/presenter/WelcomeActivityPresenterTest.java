package com.vantagecircle.heartrate.activity.presenter;

import android.app.Application;
import android.content.Intent;

import com.vantagecircle.heartrate.HeartApplication;
import com.vantagecircle.heartrate.activity.ui.HeartActivity;
import com.vantagecircle.heartrate.data.UserM;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by bapidas on 10/10/17.
 */
public class WelcomeActivityPresenterTest {
    Application application;

    @Test
    public void createUser() throws Exception {
        UserM userM = new UserM("100", "Bapi", "98");
    }

}