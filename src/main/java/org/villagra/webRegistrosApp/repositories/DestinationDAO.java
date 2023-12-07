package org.villagra.webRegistrosApp.repositories;

import org.villagra.webRegistrosApp.configuration.DatabaseConfig;
import org.villagra.webRegistrosApp.model.Destination;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DestinationDAO {
   // private final Connection connection;
    private static final int PAGE_SIZE = 5; // Tamaño de página

    /*public DestinationDAO() throws SQLException {
        connection = DatabaseConfig.getInstance().getConnection();
    }*/

    public void addDestination(Destination destination) throws SQLException {
        String queryCheck = "SELECT COUNT(*) FROM TBA_DESTINATION WHERE DTN_ID = ?";
        String queryIns = "INSERT INTO TBA_DESTINATION (DTN_ID, DTN_DESTINATION) VALUES (?, ?)";
        String queryUp = "UPDATE TBA_DESTINATION SET DTN_DESTINATION = ? WHERE DTN_ID = ?";

        // Verificar si el registro ya existe
        Connection connection = DatabaseConfig.getInstance().getConnection();
        try (PreparedStatement checkStatement = connection.prepareStatement(queryCheck)) {
            checkStatement.setString(1, destination.getId());
            ResultSet resultSet = checkStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count > 0) {
                // Si el registro existe, realizar una actualización
                try (PreparedStatement updateStatement = connection.prepareStatement(queryUp)) {
                    updateStatement.setString(1, destination.getDestination());
                    updateStatement.setString(2, destination.getId());
                    updateStatement.executeUpdate();
                }
            } else {
                // Si el registro no existe, realizar una inserción
                try (PreparedStatement insertStatement = connection.prepareStatement(queryIns)) {
                    insertStatement.setString(1, destination.getId());
                    insertStatement.setString(2, destination.getDestination());
                    insertStatement.executeUpdate();
                }
            }
        }
    }


    public void deleteDestination(Destination destination) throws SQLException {
        String query = "DELETE FROM TBA_DESTINATION WHERE DTN_ID = ?";
        Connection connection = DatabaseConfig.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, destination.getId());
            statement.executeUpdate();
        }
    }

    public List<Destination> getFilteredDestinations(Map<String, String> queryParams, int page) throws SQLException {
        List<Destination> destinations = new ArrayList<>();
        int offset = (page - 1) * PAGE_SIZE;

        // Construye la consulta SQL básica
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM TBA_DESTINATION WHERE 1=1");

        // Agrega filtros si se proporcionan en los parámetros de consulta
        if (queryParams.containsKey("ID")) {
            queryBuilder.append(" AND DTN_ID = ?");
        }
        if (queryParams.containsKey("DESTINATION")) {
            queryBuilder.append(" AND DTN_DESTINATION = ?");
        }

        // Agrega paginación
        queryBuilder.append(" LIMIT ?, ?");
        Connection connection = DatabaseConfig.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
            int paramIndex = 1;

            // Configura los valores de los parámetros de filtro
            if (queryParams.containsKey("ID")) {
                statement.setString(paramIndex++, queryParams.get("ID"));
            }
            if (queryParams.containsKey("DESTINATION")) {
                statement.setString(paramIndex++, queryParams.get("DESTINATION"));
            }

            statement.setInt(paramIndex++, offset);
            statement.setInt(paramIndex, PAGE_SIZE);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Destination destination = new Destination(
                            resultSet.getString("DTN_ID"),
                            resultSet.getString("DTN_DESTINATION")
                    );
                    destinations.add(destination);
                }
            }
        }
        return destinations;
    }
}
