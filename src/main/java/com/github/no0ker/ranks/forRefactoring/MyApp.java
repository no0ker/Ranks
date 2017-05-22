package com.github.no0ker.ranks.forRefactoring;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

interface DataConnection {
    int loadAvgDataOfYear(int currentYear) throws IllegalArgumentException;
    void saveAvgDataOfYear(int currentYear, int averageData) throws Exception;
}

public class MyApp implements DataConnection {
    private static final Logger LOG = LogManager.getLogger(MyApp.class);
    private static final String APP_VERSION = "app v.1.13";
    private static final String SOURCE_DATA_FILENAME = "1.txt";
    private static final String OUTPUT_DATA_FILENAME = "statistika.txt";
    private static final int START_YEAR = 1990;
    private static final int END_YEAR = 2020;

    private static volatile MyApp instance;

    private MyApp() {}

    private static MyApp getInstance(){
        MyApp localInstance = instance;
        if(localInstance == null){
            synchronized (MyApp.class){
                localInstance = instance;
                if(localInstance == null){
                    localInstance = instance = new MyApp();
                }
            }
        }
        return localInstance;
    }


    public static void main(String[] args) {
        try {
            System.out.println(APP_VERSION);
            for (int currYear = START_YEAR; currYear < END_YEAR; ++currYear) {
                int averageValue = MyApp.getInstance().loadAvgDataOfYear(currYear);
                if (averageValue > 0) {
                    System.out.println(currYear + " " + averageValue);
                }
                MyApp.getInstance().saveAvgDataOfYear(currYear, averageValue);
            }
            System.out.println("complete");
        } catch (Exception e) {
            System.out.println("complete with errors");
        }
    }

    public int loadAvgDataOfYear(int currentYear) throws IllegalArgumentException {
        int result = 0;
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(SOURCE_DATA_FILENAME))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                String[] fileRowCells = StringUtils.split(sCurrentLine, " ");
                if (fileRowCells.length < 3) {
                    String errorMessage = "can't parse this row: " + sCurrentLine;
                    LOG.error(errorMessage);
                    throw new IllegalArgumentException(errorMessage);
                } else if (fileRowCells[2].contains(String.valueOf(currentYear))) {
                    try {
                        result += Integer.parseInt(fileRowCells[3]);
                        ++count;
                    } catch (NumberFormatException e) {
                        String errorMessage = "can't parse this row: " + sCurrentLine;
                        LOG.error(errorMessage);
                        throw new IllegalArgumentException(errorMessage, e);
                    }
                }
            }
        } catch (IOException e) {
            String errorMessage = "can't open file to read";
            LOG.error(errorMessage, e);
            throw new IllegalArgumentException(errorMessage, e);
        }
        if(result > 0){
            return result / count;
        } else {
            return 0;
        }
    }

    public void saveAvgDataOfYear(int currentYear, int averageData) throws Exception {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(OUTPUT_DATA_FILENAME, true))) {
            String fileRow = (currentYear - START_YEAR) + " " + currentYear + " " + averageData + "\n";
            bw.write(fileRow);
        } catch (IOException e) {
            String errorMessage = "cant write data to file";
            LOG.error(errorMessage, e);
            throw new RuntimeException(e);
        }
    }
}

