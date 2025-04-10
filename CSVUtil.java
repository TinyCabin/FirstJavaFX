package com.example.AutkaFX;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

public class CSVUtil {

    public static void exportShowroomState(CarShowroom showroom, String filename) {
        try (PrintWriter writer = new PrintWriter(new File(filename))) {
            StringBuilder sb = new StringBuilder();

            Field[] fields = Vehicle.class.getDeclaredFields();
            Arrays.sort(fields, Comparator.comparingInt(f -> f.isAnnotationPresent(CSVColumn.class) ? f.getAnnotation(CSVColumn.class).order() : Integer.MAX_VALUE));

            for (Field field : fields) {
                if (field.isAnnotationPresent(CSVColumn.class)) {
                    sb.append(field.getAnnotation(CSVColumn.class).name());
                    sb.append(',');
                }
            }
            sb.append('\n');

            for (Map.Entry<Vehicle, Integer> entry : showroom.getInventory().entrySet()) {
                Vehicle vehicle = entry.getKey();
                for (Field field : fields) {
                    if (field.isAnnotationPresent(CSVColumn.class)) {
                        field.setAccessible(true);
                        sb.append(field.get(vehicle));
                        sb.append(',');
                    }
                }
                sb.append('\n');
            }

            writer.write(sb.toString());

            System.out.println("Exported showroom state to " + filename);

        } catch (FileNotFoundException | IllegalAccessException e) {
            System.out.println(e.getMessage());
        }
    }


    public static CarShowroom importShowroomState(String filename) {
        CarShowroom showroom = new CarShowroom("Imported Showroom", 100);
        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

            br.readLine(); // skip the header

            while ((line = br.readLine()) != null) {
                String[] vehicleData = line.split(cvsSplitBy);
                Vehicle vehicle = new Vehicle(vehicleData[0], vehicleData[1], ItemCondition.NEW, Double.parseDouble(vehicleData[2]), Integer.parseInt(vehicleData[3]), Double.parseDouble(vehicleData[4]), Double.parseDouble(vehicleData[5]));
                showroom.addVehicle(vehicle);
            }

            System.out.println("Imported showroom state from " + filename);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return showroom;
    }

    // Metoda do oznaczenia produktów jako nowe po imporcie
    public static void markItemsAsNew(CarShowroomContainer container) {
        if (container != null) {
            for (Map.Entry<String, CarShowroom> entry : container.getShowrooms().entrySet()) {
                CarShowroom showroom = entry.getValue();
                if (showroom != null) {
                    for (Vehicle item : showroom.getInventory().keySet()) {
                        item.setCondition(ItemCondition.NEW);
                    }
                }
            }
            System.out.println("All items marked as new.");
        }
    }

}
