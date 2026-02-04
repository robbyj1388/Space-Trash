/**
 * This is a tempory Logger.java, im waiting on Nathan to give me a finalized optimized one -Noah
 */

package com.example;

import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;

public class Logger {
    File logFile = new File("log" + System.currentTimeMillis() + ".txt");
    PrintWriter writer;

    public void logEntry(String entry) {
        try {
            if (writer == null) {
                writer = new PrintWriter(logFile);
            }
            writer.println(System.currentTimeMillis() + ": " + entry);;
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

