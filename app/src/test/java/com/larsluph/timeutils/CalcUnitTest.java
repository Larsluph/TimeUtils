package com.larsluph.timeutils;

import static org.junit.Assert.*;

import com.larsluph.timeutils.calc.CalcActivity;

import org.junit.Test;

public class CalcUnitTest {
    @Test
    public void testValidDate() {
        assertTrue(CalcActivity.validateDate("01-01"));
        assertTrue(CalcActivity.validateDate("01/01"));

        assertTrue(CalcActivity.validateDate("01-01-2000"));
        assertTrue(CalcActivity.validateDate("01/01-2000"));
        assertTrue(CalcActivity.validateDate("01-01/2000"));
        assertTrue(CalcActivity.validateDate("01/01/2000"));
        assertTrue(CalcActivity.validateDate("31-12-2022"));
    }

    @Test
    public void testValidTime() {
        assertTrue(CalcActivity.validateTime("00:00"));
        assertTrue(CalcActivity.validateTime("00:00:00"));

        assertTrue(CalcActivity.validateTime("12:30"));
        assertTrue(CalcActivity.validateTime("12:30:30"));

        assertTrue(CalcActivity.validateTime("23:59"));
        assertTrue(CalcActivity.validateTime("23:59:59"));
    }

    @Test
    public void testOOBTime() {
        assertFalse(CalcActivity.validateTime("24:59:59"));
        assertFalse(CalcActivity.validateTime("23:60:59"));
        assertFalse(CalcActivity.validateTime("23:59:60"));

        // testing OOBs
        assertFalse(CalcActivity.validateTime("12:60"));
    }
}
