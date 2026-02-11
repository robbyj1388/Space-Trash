package com.example;

import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;

public class Logger {
    File logFile = new File("log" + System.currentTimeMillis() + ".txt");
    PrintWriter writer;

    /**
    * Logs an entry to the log file with a timestamp.
    * @param time The time a given event occurs.  Use gameTime variable, or use -1 if time has no impact on the event.
    * @param entry The event being logged.
    */
    public void logEntry(String time, String entry) {
        try {
            if (writer == null) {
                writer = new PrintWriter(logFile);
            }

            if (time.equals("-1")) {
                writer.println(entry);
            } else {
                writer.println(time + ": " + entry);
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the PrintWriter.
     */
    public void close() {
        writer.close();
    }
}

