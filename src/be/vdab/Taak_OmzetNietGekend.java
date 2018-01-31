package be.vdab;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
public class Taak_OmzetNietGekend {
    private static final String URL = "jdbc:mysql://localhost/bieren?useSSL=false";
    private static final String USER = "cursist";
    private static final String PASSWORD = "cursist";
    private static final String SELECT_BROUWERS_NULL_OMZET = 
            "select brouwers.naam, count(*) as aantalbieren " + 
            "from brouwers inner join bieren on brouwers.id = bieren.brouwerid " +
            "where omzet is null " +
            "group by brouwers.naam";
    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                Statement statement = connection.createStatement()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            try (ResultSet resultSet = statement.executeQuery(SELECT_BROUWERS_NULL_OMZET)) {
                while (resultSet.next()) {
                    System.out.println(resultSet.getString("brouwers.naam") + " " + resultSet.getInt("aantalbieren"));
                }
            }
            connection.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
}
