package edu.upvictoria.fpoo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class should receive a csv and generate a table object 
 */
public class Table {

    public Table(File csv) {
        // transform the csv into the data strcuture: List<List<String>>
        Map<String, List<String>> table = csvToTable(csv);
        
    }

     private Map<String, List<String>> csvToTable(File csv) {
        Map<String, List<String>> table = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
            String primeraLinea = br.readLine(); // Leer la primera línea para obtener los nombres de las columnas
            String[] nombresColumnas = primeraLinea.split(","); // Suponiendo que las columnas están separadas por comas

            for (String columna : nombresColumnas) {
                table.put(columna, new ArrayList<>());
            }

            String linea;
            while ((linea = br.readLine()) != null) {
                String[] valores = linea.split(","); // Suponiendo que las columnas están separadas por comas
                for (int i = 0; i < nombresColumnas.length && i < valores.length; i++) {
                    table.get(nombresColumnas[i]).add(valores[i]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return table;
    }

    public void readByColumn() {

    }

    public void readByRow() {

    }

    public void writeByColumn() {

    }

    public void writeByRow() {

    }

    public void write() {

    }

    public void read() {

    }
}
