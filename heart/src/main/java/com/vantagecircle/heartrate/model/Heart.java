package com.vantagecircle.heartrate.model;

import com.vantagecircle.heartrate.annotation.HeartData;

import javax.inject.Inject;

/**
 * Created by bapidas on 27/11/17.
 */

public class Heart {
    private String gender;
    private int birthYear;

    @Inject
    public Heart(@HeartData String gender, @HeartData int birthYear) {
        this.gender = gender;
        this.birthYear = birthYear;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }
}
