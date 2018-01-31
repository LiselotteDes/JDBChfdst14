package be.vdab;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
// Ook een goeie oplossing
public class Taak_BierenVanEenSoort {
    private static final String URL = "jdbc:mysql://localhost/bieren?useSSL=false";
    private static final String USER = "cursist";
    private static final String PASSWORD = "cursist";
    private static final String SELECT_BIEREN_VAN_SOORT = 
            "select bieren.naam " +
            "from bieren inner join soorten on bieren.soortid = soorten.id " +
            "where soorten.naam = ?";
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Geef de naam van een soort bieren: ");
            String soortNaam = scanner.nextLine();
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                    PreparedStatement statement = connection.prepareStatement(SELECT_BIEREN_VAN_SOORT)) {
                connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                connection.setAutoCommit(false);
                statement.setString(1, soortNaam);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        do {
                            System.out.println(resultSet.getString("naam"));
                        } while (resultSet.next());
                    } else {
                        System.out.println("Soort niet gevonden");
                    }
                }
                connection.commit();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
}
