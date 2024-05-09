package edu.upvictoria.fpoo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {
    private List<HashMap<String, Object>> table;
    private List<String> columnNames;
    private Map<String, String> columnTypes;

    public Table() {
        table = new ArrayList<>();
        columnNames = new ArrayList<>();
        columnTypes = new HashMap<>();
    }

    // Method to load data from a CSV file and its metadata from a .meta file
    public static Table load(String csvFilename, String metaFilename) throws IOException {
        Table table = new Table();

        // Read metadata from .meta file
        try (BufferedReader metaReader = new BufferedReader(new FileReader(metaFilename))) {
            String line;
            while ((line = metaReader.readLine()) != null) {
                String[] parts = line.split("\\s+"); // Split by whitespace
                if (parts.length >= 2) {
                    String columnName = parts[0];
                    String dataType = parts[1];
                    table.addColumn(columnName, dataType);
                }
            }
        }

        // Read data from CSV file
        try (BufferedReader csvReader = new BufferedReader(new FileReader(csvFilename))) {
            String line;
            while ((line = csvReader.readLine()) != null) {
                String[] values = line.split(",");
                HashMap<String, Object> row = new HashMap<>();
                for (int i = 0; i < values.length; i++) {
                    row.put(table.getColumnName(i), values[i]);
                }
                table.addRow(row);
            }
        }

        return table;
    }

    // Method to write data to a CSV file
    public void writeToCSV(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Write header row with column names
            writer.write(String.join(",", columnNames));
            writer.newLine();

            // Write data rows
            for (HashMap<String, Object> row : table) {
                StringBuilder rowString = new StringBuilder();
                for (String columnName : columnNames) {
                    rowString.append(row.get(columnName)).append(",");
                }
                rowString.deleteCharAt(rowString.length() - 1); // Remove last comma
                writer.write(rowString.toString());
                writer.newLine();
            }
        }
    }

    // Method to write metadata to a .meta file
    public void writeToMeta(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String columnName : columnNames) {
                String dataType = columnTypes.get(columnName);
                writer.write(columnName + " " + dataType);
                writer.newLine();
            }
        }
    }

    // Method to add a row to the table
    public void addRow(HashMap<String, Object> row) {
        table.add(row);
    }

    // Method to add a column name and data type to the table
    public void addColumn(String columnName, String dataType) {
        columnNames.add(columnName);
        columnTypes.put(columnName, dataType);
    }

    // Method to get all rows in the table
    public List<HashMap<String, Object>> getTable() {
        return table;
    }

    // Method to get column names
    public List<String> getColumnNames() {
        return columnNames;
    }

    // Method to get column name by index
    public String getColumnName(int index) {
        return columnNames.get(index);
    }

    // Method to get data type of a column
    public String getColumnType(String columnName) {
        return columnTypes.get(columnName);
    }

    // Method to get row
    public HashMap<String, Object> getRow(int index) {
        return table.get(index);
    }

    // Method get rows (list of rows)
    public List<HashMap<String, Object>> getRows() {
        return table;
    }

    
}
