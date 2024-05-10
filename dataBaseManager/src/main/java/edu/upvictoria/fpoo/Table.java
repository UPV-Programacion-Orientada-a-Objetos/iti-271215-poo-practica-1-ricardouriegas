package edu.upvictoria.fpoo;

import java.io.*;
import java.util.*;

public class Table {
    private List<HashMap<String, Object>> table; // [1] <Key, Value>
    private List<String> columnNames;
    private HashMap<String, String> columnTypes;

    public Table() {
        // Initialize table, columnNames, and columnTypes
        table = new ArrayList<>();
        columnNames = new ArrayList<>();
        columnTypes = new HashMap<>();
    }

    // Method to load data from a CSV file and its metadata from a .meta file
    public static Table load(File csvFile, File metaFile) {
        Table table = new Table();

        // Read column names from the metadata file
        List<String> columnNames = readColumnNamesFromMeta(metaFile);
        for (String columnName : columnNames) {
            table.addColumn(columnName, "string"); // Assuming all columns are of string type initially
        }

        // Read data from CSV file and populate the table
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                HashMap<String, Object> row = new HashMap<>();
                for (int i = 0; i < parts.length; i++) {
                    String columnName = columnNames.get(i);
                    Object value = parseValue(parts[i]);
                    row.put(columnName, value);
                }
                table.addRow(row);
            }
        } catch (SecurityException e) {
            throw new RuntimeException("The program does not have permission to read the database .csv file");
        } catch (IOException e) {
            throw new RuntimeException("Error reading .csv file");
        }

        return table;
    }

    private static List<String> readColumnNamesFromMeta(File metaFile) {
        List<String> columnNames = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(metaFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                columnNames.add(parts[0]);
            }
        } catch (SecurityException e) {
            throw new RuntimeException("The program does not have permission to read the database .meta file");
        } catch (IOException e) {
            throw new RuntimeException("Error reading .meta file");
        }
        return columnNames;
    }

    private static Object parseValue(String value) {
        if ("null".equals(value)) {
            return null;
        } else {
            return value;
        }
    }

    // Method to write data to a CSV file
    public void writeToCSV(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Write header row with column names
            writer.write(String.join(",", columnNames));
            writer.newLine();

            // Write data rows
            for (HashMap<String, Object> row : table) {
                StringBuilder rowString = new StringBuilder();
                for (String columnName : columnNames) {
                    Object value = row.get(columnName);
                    if (value instanceof String) {
                        rowString.append("\"").append(value).append("\",");
                    } else {
                        rowString.append(value).append(",");
                    }
                }
                rowString.deleteCharAt(rowString.length() - 1); // Remove last comma
                writer.write(rowString.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing .csv file", e);
        } catch (SecurityException e) {
            throw new RuntimeException("The program does not have permission to write the database .csv file", e);
        }
    }

    // Method to write metadata to a .meta file
    public void writeToMeta(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String columnName : columnNames) {
                String dataType = columnTypes.get(columnName);
                writer.write(columnName + " " + dataType);
                writer.newLine();
            }
        } catch (SecurityException e) {
            throw new RuntimeException("The program does not have permission to write the database .meta file", e);
        } catch (IOException e) {
            throw new RuntimeException("Error writing .meta file", e);
        }
    }

    // Method to save data to a CSV file and its metadata to a .meta file
    public void save(File csvFile, File metaFile) {
        try {
            writeToCSV(csvFile.getAbsolutePath());
            writeToMeta(metaFile.getAbsolutePath());
        } catch (SecurityException e) {
            throw new RuntimeException("The program does not have permission to write the database files");
        } catch (RuntimeException e) {
            throw new RuntimeException("Error writing database files");
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

    // Method to update a row in the table
    public void updateRow(HashMap<String, Object> row, int index) {
        table.set(index, row);
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

    // Mehtod to limit the number of rows
    public void limit(int limit) {
        // handle the case where the limit is greater than the number of rows
        if (limit > table.size()) {
            return;
        }
        table = table.subList(0, limit);
    }

    // Method to filter columns (select)
    public void filterColumns(List<String> columns) {
        List<String> columnsToRemove = new ArrayList<>();
        for (String columnName : columnNames) {
            if (!columns.contains(columnName)) {
                columnsToRemove.add(columnName);
            }
        }
        for (String columnName : columnsToRemove) {
            columnNames.remove(columnName);
            columnTypes.remove(columnName);
            for (HashMap<String, Object> row : table) {
                row.remove(columnName);
            }
        }
    }

    // Method sort using java's vanilla function
    public void sort(String columnName) {
        // Check if the column name exists
        if (!columnNames.contains(columnName)) {
            throw new IllegalArgumentException("Column '" + columnName + "' does not exist.");
        }

        // Define a custom comparator to compare rows based on the specified column
        Comparator<HashMap<String, Object>> comparator = (row1, row2) -> {
            Object value1 = row1.get(columnName);
            Object value2 = row2.get(columnName);

            // Handle null values by considering them greater than non-null values
            if (value1 == null && value2 == null) {
                return 0;
            } else if (value1 == null) {
                return 1;
            } else if (value2 == null) {
                return -1;
            }

            // Compare values based on their types
            if (value1 instanceof Comparable && value2 instanceof Comparable) {
                return ((Comparable) value1).compareTo(value2);
            } else {
                throw new IllegalArgumentException("Values in column '" + columnName + "' are not comparable.");
            }
        };

        // Sort the table using the specified comparator
        Collections.sort(table, comparator);
    }

    // Method to sort in reverse order
    public void sortReverse(String columnName) {
        // Check if the column name exists
        if (!columnNames.contains(columnName)) {
            throw new IllegalArgumentException("Column '" + columnName + "' does not exist.");
        }

        // Define a custom comparator to compare rows based on the specified column
        Comparator<HashMap<String, Object>> comparator = (row1, row2) -> {
            Object value1 = row1.get(columnName);
            Object value2 = row2.get(columnName);

            // Handle null values by considering them greater than non-null values
            if (value1 == null && value2 == null) {
                return 0;
            } else if (value1 == null) {
                return 1;
            } else if (value2 == null) {
                return -1;
            }

            // Compare values based on their types in reverse order
            if (value1 instanceof Comparable && value2 instanceof Comparable) {
                return ((Comparable) value2).compareTo(value1); // Note the reversed order here
            } else {
                throw new IllegalArgumentException("Values in column '" + columnName + "' are not comparable.");
            }
        };

        // Sort the table using the specified comparator
        Collections.sort(table, comparator);
    }

    // Method to print the table
    public void print() {
        System.out.println(columnNames);
        for (HashMap<String, Object> row : table) {
            System.out.println(row);
        }
    }
}
