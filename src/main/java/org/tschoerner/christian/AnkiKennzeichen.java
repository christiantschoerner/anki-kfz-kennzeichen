package org.tschoerner.christian;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author Christian Tschörner
 */
public class AnkiKennzeichen {

    private static final File databaseCSV = new File("C:\\Users\\Christian\\Desktop\\AnkiKennzeichen\\kfzkennzeichen-deutschland.csv");
    private static final File imageFolder = new File("C:\\Users\\Christian\\Desktop\\AnkiKennzeichen\\images");
    private static final File ankiDeck = new File("C:\\Users\\Christian\\Desktop\\AnkiKennzeichen\\ankideck.txt");

    public static void main(String[] args) throws IOException {
        Collection<Plate> plates = getPlatesFromCsv();

        for(Plate plate : plates) {
            System.out.println(String.format("%s T 1E - %s", plate.getDatabaseName(), plate.getCityName()));
            downloadPlateImage(plate, 100);
        }

        createAnkiDeck(plates);

        System.out.println("Anzahl Kennzeichen: " + plates.size());
    }

    private static void createAnkiDeck(Collection<Plate> plates) throws IOException {
        if(ankiDeck.exists()){
            ankiDeck.delete();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ankiDeck))) {
            for(Plate plate : plates){
                File imageFile = new File(imageFolder + File.separator + plate.getDatabaseName() + ".png");
                String filename = imageFile.getName();
                String question = "<img src=\"" + filename + "\">";
                String answer = plate.getCityName();
                if(plate.getAlternativeDatabaseNames().size() > 1){
                    answer += "<br> (Auch für: " + plate.getAlternativeNamesAsString() + ")";
                }

                writer.write(question + "\t" + answer);
                writer.newLine();
            }
        }

        System.out.println("Karten-Datei erstellt: " + ankiDeck.getAbsolutePath());
    }

    private static boolean downloadPlateImage(Plate plate, long delayBetweenFiles){
        String downloadUrl = String.format(
                "https://cdn.onlinestreet.de/img/autokennzeichen/nummernschild-%s-T-1E.png?width=1000",
                plate.getDatabaseName()
        );
        File file = new File(imageFolder.getAbsolutePath() + File.separator + plate.getDatabaseName() + ".png");

        if(file.exists()){
            System.out.println(file.getName() + " already exists");
            return true;
        }

        try (InputStream in = new URL(downloadUrl).openStream()) {
            Files.copy(in, Paths.get(file.getAbsolutePath()));
            System.out.println("Image saved as: " + file.getName());

            Thread.sleep(delayBetweenFiles);

            return true;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();

            return false;
        }
    }

    private static Collection<Plate> getPlatesFromCsv(){
        HashMap<String, Plate> plates = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(databaseCSV), StandardCharsets.ISO_8859_1))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                if(values.length > 1) {
                    String databaseName = values[0];
                    String cityName = values[1];

                    plates.put(databaseName, new Plate(cityName, databaseName, new ArrayList<>()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return plates.values();
    }

    private static Collection<Plate> getPlatesFromCsvOld(){
        HashMap<String, Plate> plates = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(databaseCSV))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");

                if(values.length > 5) {
                    String cityName = values[2];
                    List<String> plateList = Arrays.asList(values[5].split(" {2}"));

                    for(String plate : plateList) {
                        plates.put(plate, new Plate(cityName, plate, plateList));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return plates.values();
    }
}