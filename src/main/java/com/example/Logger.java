package com.example;

import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;

/**
 * Key terms for logging
 * 
 * PLEASE follow this formatting when writing logged events
 * CMD arg0 arg1 arg2
 * 
 * 
 * "Resolution width height" - What the window resolution is
 * "LeftPaddle x y" - Where left paddle is
 * "RightPaddle x y" - Where right paddle is
 * "MeSpawn x velocity id shape" - Meteor Spawns 
 * "MeDeflect id" Meteor deflect event
 * "End 0" Ends the game (Im having issues with Python seeing just End as End\n so just trust it'll be for error code later or smth)
 * 
 */


public class Logger {
    File logFile = new File("log" + System.currentTimeMillis() + ".txt");
    PrintWriter writer;
    boolean log = false; //Sometimes a game isnt specifically running
    /**
     * Starts up a new log file
     */
    public void newLog() {
        close();
        log = true;
        logFile = new File("log" + System.currentTimeMillis() + ".txt");
    }

    /**
    * Logs an entry to the log file with a timestamp.
    * @param time The time a given event occurs.  Use gameTime variable, or use -1 if time has no impact on the event.
    * @param entry The event being logged.
    */
    public void logEntry(String time, String entry) {
        if(!log) {
            return;
        }
        try {
            if (writer == null) {
                writer = new PrintWriter(logFile);
            }

            //Im leaving -1 in just because it makes reading it simpler 
            writer.println(time + ": " + entry);

            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the PrintWriter.
     */
    public void close() {
        if(writer != null) {

            writer.flush();
            log = false;
            writer.close();
        }
    }
}