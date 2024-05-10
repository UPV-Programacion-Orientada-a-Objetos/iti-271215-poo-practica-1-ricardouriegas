package edu.upvictoria.fpoo;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class Table {
    private List<HashMap<String, Object>> table; // [1] <Key, Value> the key is
    private List<String> columnNames;
    private HashMap<String, String> columnTypes;
    private List<String> constraints;
    // Compile regex pattern
    private static final Pattern number_pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    public Table() {
        // Initialize table, columnNames, and columnTypes
        table = new ArrayList<HashMap<String, Object>>();
        columnNames = new ArrayList<>();
        columnTypes = new HashMap<>();
        // constraints = new ArrayList<>();
    }

    // Method to load data from a CSV file into a Table object
    public static Table load(File csvFile) {
        Table table_obj = new Table();

        // Get the first row of the CSV to save the column names
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String[] columnNames = reader.readLine().split(",");
            for (String columnName : columnNames) {
                table_obj.columnNames.add(columnName.toUpperCase());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading .csv file", e);
        }

        // Read the rest of the CSV file
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            reader.readLine(); // Skip the first row (column names)
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                HashMap<String, Object> row = new HashMap<>();
                for (int i = 0; i < values.length; i++) {
                    row.put(table_obj.columnNames.get(i), parseValue(values[i]));
                }
                table_obj.table.add(row);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading .csv file", e);
        }

        return table_obj;
    }

    private static Object parseValue(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        // Evaluate if the value is a number
        else if (number_pattern.matcher(value).matches()) {
            return Double.parseDouble(value);
        }
        // In the rare case of not being able to
        // parse the value, return it as is
        else {
            return value;
        }
    }

    /**************************************************************************/

    // Method to write data to a CSV file
    public void writeToCSV(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

            // Write the column names
            writer.write(String.join(",", columnNames));

            // Write the rows
            for (HashMap<String, Object> row : table) {
                writer.newLine();
                List<String> values = new ArrayList<>();
                for (String columnName : columnNames) {
                    Object value = row.get(columnName);
                    if (value instanceof String) {
                        values.add("\"" + value + "\"");
                    } else {
                        values.add(value.toString());
                    }
                }
                writer.write(String.join(",", values));
            }
        } catch (SecurityException e) {
            throw new RuntimeException("The program does not have permission to write the database .csv file", e);
        } catch (IOException e) {
            throw new RuntimeException("Error writing .csv file", e);
        }
    }

    // Method to write metadata to a .meta file
    public void writeToMeta(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String columnName : columnNames) {
                String dataType = columnTypes.get(columnName);
                writer.write(columnName + " " + dataType + " " + constraints);
                writer.newLine();
            }
        } catch (SecurityException e) {
            throw new RuntimeException("The program does not have permission to write the database .meta file", e);
        } catch (IOException e) {
            throw new RuntimeException("Error writing .meta file", e);
        }
    }

    // Method to save data to a CSV file and its metadata to a .meta file
    public void save(File csvFile) {
        try {
            writeToCSV(csvFile);
        } catch (SecurityException e) {
            throw new RuntimeException("The program does not have permission to write the database files");
        } catch (RuntimeException e) {
            throw new RuntimeException("Error writing database files");
        }
    }

    // Method to add a row to the table
    public void addRow(HashMap<String, Object> row) {
        // save strings in quotes
        // for (String columnName : columnNames) {
        //     Object value = row.get(columnName);
        //     if (value instanceof String) {
        //         String stringValue = (String) value;
        //         if (!stringValue.startsWith("\"") && !stringValue.endsWith("\"")) {
        //             row.put(columnName, "\"" + stringValue + "\"");
        //         }
        //     }
        // }

        // add the row to the table
        table.add(row);
    }

    // Method to add a column name and data type to the table
    public void addColumn(String columnName, String dataType) {
        columnNames.add(columnName);
        columnTypes.put(columnName, dataType);
    }

    // Method to update a row in the table
    public void updateRow(HashMap<String, Object> row, int index) {
        if (index < 0 || index >= table.size()) {
            return;
        }
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
        // if (!columnNames.contains(columnName)) {
        //     throw new IllegalArgumentException("Column '" + columnName + "' does not exist.");
        // }

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
        // if (!columnNames.contains(columnName)) {
        //     // throw new IllegalArgumentException("Column '" + columnName + "' does not exist.");
        //     return;   
        // }

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

    // Method to delete a row
    public void deleteRow(HashMap<String, Object> rowToDelete) {
        Iterator<HashMap<String, Object>> iterator = table.iterator();
        while (iterator.hasNext()) {
            HashMap<String, Object> row = iterator.next();
            if (row.equals(rowToDelete)) {
                iterator.remove();
                return; // Row found and deleted, exit the method
            }
        }
        // If the row is not found, throw an exception or handle it as needed
        throw new IllegalArgumentException("Row not found in the table.");
    }

    // Method to print the table
    public void print() {
        System.out.println(columnNames);
        for (HashMap<String, Object> row : table) {
            System.out.println(row);
        }
    }

    // Method to write the column name at the beginning of the table
    public void writeColumnNames(List<String> columnName) {
        // write the column names at the beginning of the table
        columnNames = columnName;
    }
}
