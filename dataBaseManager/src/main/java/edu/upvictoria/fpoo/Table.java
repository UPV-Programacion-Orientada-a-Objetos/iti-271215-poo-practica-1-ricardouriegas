package edu.upvictoria.fpoo;

import java.util.ArrayList;
import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Object;

/**
 * This class should receive a csv and generate a table
 * A table is define as a Matrix of objects
 */
public class Table {
    /**
     * The csv saves only the data
     * Strings are saved with double quotes
     * Numbers are saved without quotes
     * Theres not a header
     * Theres a .meta file that saves the metadata of the table
     * the meta data is: <COLUMN_NAME> <DATA_TYPE> <CONSTRAINT>*
     * <CONSTRAINT> ::= PRIMARY KEY | FOREIGN KEY | NULL | NOT_NULL | UNIQUE
     */

    private List<HashMap<String, Object>> table;
    private String path;

    public Table(File csv) {

    }

    public void readCSV(File csv) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(csv));
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                HashMap<String, Object> row = new HashMap<>();
                for (int i = 0; i < values.length; i++) {
                    row.put("column" + i, values[i]);
                }
                table.add(row);
            }
        } catch (SecurityException e) {
            throw new RuntimeException("You do not have permission to read the file");
        } catch (IOException e) {
            throw new RuntimeException("There was an error reading the file");
        }
    }
}
