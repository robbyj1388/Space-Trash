/**
 * This is a tempory Logger.java, im waiting on Nathan to give me a finalized optimized one -Noah
 */

package com.example;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Logger {
    List<String[]> log = new ArrayList<>();

    /**
     * Key terms to remember
     * leftPos, x, y: left paddle pos
     * rightPos, x, y: right paddle pos
     * resolution, w, h: The resolution of the display
     */

    public Logger() {

    }
    /**
     * 
     * @param time The current game time
     * @param additionals Please follow this format "leftPos, 0.32, 0.53; rightPos, 0.21, 0.35"
     */
    public void log(double time, String[] additionals) {
        String[] s = {Double.toString(time), String.join(",", additionals)};

        log.add(s);
    }
    /**
     * Clears the logger file (i.e. new game)
     */
    public void clearLog() {
        log.clear();
    }
    /**
     * Writes the entire log file into the ./log/ directory
     */
    public void write() {
        Date d = new Date();
        String path = String.format("./log/log-%s.txt", Long.toString(d.getTime()));
        try( FileWriter wrt = new FileWriter(path)) {
            for(int i = 0; i < log.size(); i++ ) {
                wrt.write(String.join(",", log.get(i)) + "\n");
            }
            System.out.println("Successfully put log into log output at " + path);
        }
        catch(IOException er) {
            System.out.println("ERROR WRITING LOG");
            System.out.println(er);
        }

    }

}

