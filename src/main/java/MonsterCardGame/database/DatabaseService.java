/*
package MonsterCard.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseService {  // Klasse Databaseservice

    private static DatabaseService instance;    // Statische Variable für die Singleton-Instanz

    // Konstanten für die Datenbankverbindung
    private static final String DB_URL = "jdbc:postgresql://localhost/mtcg";    // URL der DB
    private static final String USER = "postgres";  // Benutzername
    private static final String PASS = "postgres";  // Passwort

    public static DatabaseService getInstance() {  // Statische Methode, um die Singleton-Instanz zu erhalten oder zu erstellen
        if (DatabaseService.instance == null) {
            DatabaseService.instance = new DatabaseService();   // Wenn die Instanz nicht existiert -> neue wird erstellt
        }
        return DatabaseService.instance;    // Gibt die Singleton-Instanz zurück
    }

    public Connection getConnection() {     // Methode, um eine Verbindung zur Datenbank herzustellen
        try {
            return DriverManager.getConnection(DB_URL, USER, PASS);     // Versuch eine Verbindung mit Datenbank-Details zu erstellen
        } catch (SQLException e) {
            e.printStackTrace();    // Bei einem SQL-Fehler -> Fehlermeldung 
        }
        return null;    // NULL, falls die Verbindung nicht hergestellt werden konnte
    }
}
*/

package MonsterCardGame.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseService {

    private static volatile DatabaseService instance;

    // Database credentials should ideally be loaded from a configuration file
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/mtcg";
    private static final String USER = "postgres";
    private static final String PASS = "postgres";

    private DatabaseService() {
        // Private constructor to prevent instantiation
    }

    public static DatabaseService getInstance() {
        if (instance == null) {
            synchronized (DatabaseService.class) {
                if (instance == null) {
                    instance = new DatabaseService();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
}
