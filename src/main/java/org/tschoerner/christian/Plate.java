package org.tschoerner.christian;

import java.util.List;

/**
 * @author Christian Tschörner
 */
public class Plate {

    String cityName;
    String databaseName;
    List<String> alternativeDatabaseNames;

    public Plate(String cityName, String databaseName, List<String> alternativeDatabaseNames) {
        this.cityName = cityName;
        this.databaseName = databaseName;
        this.alternativeDatabaseNames = alternativeDatabaseNames;
    }

    public String getCityName() {
        return cityName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public List<String> getAlternativeDatabaseNames() {
        return alternativeDatabaseNames;
    }

    public String getAlternativeNamesAsString() {
        StringBuilder string = new StringBuilder();
        for(String names : getAlternativeDatabaseNames()){
            string.append(string.isEmpty() ? names : ", " + names);
        }

        return string.toString();
    }
}
