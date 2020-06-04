package com.github.derrop.simplecommand;

import org.junit.Test;

import static org.junit.Assert.*;

public class CommandPropertiesTest {

    @Test
    public void testCommandProperties() {
        CommandProperties properties = CommandProperties.parseLine("1=2 \"a b c\"=\"c d e\" --test");

        assertEquals("2", properties.getString("1"));
        assertEquals(2, properties.getInt("1"));

        assertEquals("c d e", properties.getString("a b c"));

        assertTrue(properties.getBoolean("test"));
    }

}
