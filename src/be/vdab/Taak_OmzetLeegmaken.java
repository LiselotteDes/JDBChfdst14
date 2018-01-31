package be.vdab;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

public class Taak_OmzetLeegmaken {
    // MEMBERVARIABELEN
    private static final String URL = "jdbc:mysql://localhost/bieren?useSSL=false";
    private static final String USER = "cursist";
    private static final String PASSWORD = "cursist";
    private static final String UPDATE_BEGIN = 
            "update brouwers set omzet = null where id in (";
    private static final String SELECT_BEGIN =
            "select id from brouwers where id in (";
    
    // PRIVATE STATIC METHODS
    private static Set<Integer> vraagBrouwernummers() {
        Set<Integer> brouwernummers = new LinkedHashSet<>();
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Brouwernummer (0 om te stoppen): ");
            for(int nr; 0 != (nr=scanner.nextInt()); ) {
                if (nr < 0) {
                    System.out.print("Geef een positief getal: ");
                } else {
                    if ( ! brouwernummers.add(nr)) {
                        System.out.println(nr + " reeds ingegeven");
                    }
                }
//                System.out.println();
            }
        }
        return brouwernummers;
    }
    private static int updateBrouwers(Set<Integer> ids, Connection connection) throws SQLException {
        // Vervolledig de String(Builder) met het update statement met het juist aantal parameters
        StringBuilder updateSQL = new StringBuilder(UPDATE_BEGIN);
        for(int id : ids) {
            updateSQL.append("?,");
        }
        updateSQL.setCharAt(updateSQL.length()-1, ')');
        // Open een PreparedStatement
        try (PreparedStatement statement = connection.prepareStatement(updateSQL.toString())) {
            int index = 1;
            for (int id : ids) {
                statement.setInt(index++, id);
            }
            int aantalRecordsGewijzigd = statement.executeUpdate();
            System.out.println("Aantal records gewijzigd: " + aantalRecordsGewijzigd);
            return aantalRecordsGewijzigd;
        }
    }
    private static void toonNietGevondenBrouwernummers(Set<Integer> ids, Connection connection) throws SQLException {
        // Maak een StringBuilder met het select statement:
        StringBuilder selectSQL = new StringBuilder(SELECT_BEGIN);
        for (int id : ids) {
            selectSQL.append("?,");
        }
        selectSQL.setCharAt(selectSQL.length() - 1, ')');
        // Open een PreparedStatement
        try (PreparedStatement statement = connection.prepareStatement(selectSQL.toString())) {
            int index = 1;
            for (int id : ids) {
                statement.setInt(index++, id);
            }
            // Open een ResultSet
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ids.remove(resultSet.getInt("id"));
                }
            }
            System.out.println("Volgende nummers werden niet gevonden: ");
            for (int id : ids) {
                System.out.print(id + " ");
            }
            System.out.println();
        }
    }
    // MAIN METHOD
    public static void main(String[] args) {
        Set<Integer> ids = vraagBrouwernummers();
        if (! ids.isEmpty()) {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                connection.setAutoCommit(false);
                if (ids.size() != updateBrouwers(ids, connection)) {
                    toonNietGevondenBrouwernummers(ids, connection);
                }
                connection.commit();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
}
