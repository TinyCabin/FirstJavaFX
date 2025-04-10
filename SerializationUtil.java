package com.example.AutkaFX;
import java.io.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;


public class SerializationUtil {

    // Metoda do zapisu stanu salonów do pliku
    public static void saveCarShowroomState(CarShowroomContainer container, String filename) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename))) {
            // Serializacja kontenera
            outputStream.writeObject(container);
            System.out.println("Car showroom state saved successfully.");
        } catch (IOException e) {
            System.err.println("Error while saving car showroom state: " + e.getMessage());
        }
    }


    // Metoda do odczytu stanu salonów z pliku
    public static CarShowroomContainer loadCarShowroomState(String filename) {
        CarShowroomContainer container = null;
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename))) {
            // Deserializacja kontenera
            container = (CarShowroomContainer) inputStream.readObject();
            System.out.println("Car showroom state loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error while loading car showroom state: " + e.getMessage());
        }
        return container;
    }

}
