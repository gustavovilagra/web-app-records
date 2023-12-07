package org.villagra.webRegistrosApp.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.villagra.webRegistrosApp.model.Destination;
import org.villagra.webRegistrosApp.repositories.DestinationDAO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;

import java.sql.SQLException;
import java.util.*;

@WebServlet("/destination")
public class ApiServlet extends HttpServlet {
    DestinationDAO destinationDAO=new DestinationDAO();
    List<String> validTokens = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            System.out.println("valor de la lista actual en /destination=" + validTokens);


            response.setHeader("Access-Control-Allow-Origin", "http://localhost:5501"); // Cambia esto al dominio apropiado
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");


            response.setHeader("Access-Control-Max-Age", "3600"); // Por ejemplo, 1 hora

            String authorizationHeader = request.getHeader("Authorization");

            if (authorizationHeader != null) {
                // Eliminar comillas alrededor del token que el cliente envió
                authorizationHeader = authorizationHeader.replace("\"", "");
                System.out.println("teste GET authorizationHeader=" + authorizationHeader);
                System.out.println("valor de la lista actual en /destination=" + validTokens);
                if (isValidToken(authorizationHeader)) {
                    System.out.println("Token autorizado!");

                    String query = request.getQueryString();
                    Map<String, String> queryParams = parseQueryParameters(query);

                    int page = 1; // Página predeterminada

                    if (queryParams.containsKey("page")) {
                        try {
                            page = Integer.parseInt(queryParams.get("page"));
                        } catch (NumberFormatException e) {
                            System.out.println("Error en handleGetDestinations: " + e.getMessage());
                        }
                    }

                    List<Destination> destinations = destinationDAO.getFilteredDestinations(queryParams, page);

                    // Utiliza la librería Gson para convertir la lista de destinos a JSON
                    Gson gson = new Gson();
                    String jsonResponse = gson.toJson(destinations);

                    System.out.println("Enviando respuesta JSON: " + jsonResponse);

                    // Envía la respuesta JSON al cliente
                    sendJsonResponse(response, jsonResponse);
                } else {
                    System.out.println("El Token proporcionado no es igual al del servidor");
                    String responseText = "El Token proporcionado no es igual al del servidor";
                    sendResponse(response, 403, responseText);
                }
            } else {
                String responseText = "Token no válido o falta de token. Acceso denegado.";
                sendResponse(response, 401, responseText);
            }
        } catch (Exception e) {
            // Maneja errores generales
            e.printStackTrace();
            sendResponse(response, 500, "Error en el servidor.");
        }
    }
    @Override
    protected  void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Agregar los encabezados necesarios para permitir las solicitudes CORS
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5501"); // Cambia el valor al dominio apropiado
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        // Indicar el tiempo de vida (en segundos) del pre-vuelo (pre-flight) CORS
        resp.setHeader("Access-Control-Max-Age", "3600"); // Por ejemplo, 1 hora

        // Establecer el estado de la respuesta
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Verificar credenciales del servidor emisor
        String authorization = request.getHeader("Authorization");
        String expectedCredentials = "Gust@vo:MiDi@Vo$x1989!!";
        System.out.println("GET authorizationHeader=" + authorization);

        if (authorization == null || !authorization.equals("Basic " + Base64.getEncoder().encodeToString(expectedCredentials.getBytes()))) {
            // Credenciales no válidas, responder con un código de estado 401 (No autorizado)
            String responseBody = "Credenciales no válidas. Acceso denegado en el servidor receptor.";
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getOutputStream().write(responseBody.getBytes());
            return;
        }

        // Leer el token enviado en el cuerpo de la solicitud
        try (BufferedReader reader = request.getReader()) {
            String token = reader.readLine();

            //List<String> validTokens = (List<String>) getServletContext().getAttribute("tokens");
            System.out.println("Token recibido del primer servidor: " + token);

            if (token != null) {
                if (!validTokens.contains(token)) {
                    validTokens.add(token);
                    //getServletContext().setAttribute("tokens", validTokens);
                    System.out.println("Token guardado en la lista");
                } else {
                    System.out.println("Token repetido, no es necesario guardarlo en la lista.");
                }

                String responseBody = "Token válido. Acceso permitido en el segundo servidor.";
                response.setStatus(HttpServletResponse.SC_OK);
                response.getOutputStream().write(responseBody.getBytes());
            } else {
                String responseBody = "Token no válido. Acceso denegado en el segundo servidor.";
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getOutputStream().write(responseBody.getBytes());
            }
        }
    }
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:5501"); // Cambia esto al dominio apropiado
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

            response.setHeader("Access-Control-Max-Age", "3600"); // Por ejemplo, 1 hora

            String authorizationHeader = request.getHeader("Authorization");


            if (authorizationHeader != null) {
                // Eliminar comillas alrededor del token que el cliente envio
                authorizationHeader = authorizationHeader.replace("\"", "");
                System.out.println("teste PUT  authorizationHeader=" + authorizationHeader);

                if (isValidToken(authorizationHeader)) {
                    System.out.println("Token PUT autorizado!!!!!!!!!");

                    // Obtén los datos del cuerpo de la solicitud PUT
                    BufferedReader reader = request.getReader();
                    StringBuilder requestBody = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        requestBody.append(line);
                    }

                    // Convierte los datos JSON del cuerpo en un objeto Destination
                    Gson gson = new Gson();
                    Destination newDestination = gson.fromJson(requestBody.toString(), Destination.class);

                    // Agrega la nueva destino a la base de datos
                    destinationDAO.addDestination(newDestination);

                    String responseText = "Destino agregado correctamente.";
                    sendResponse(response, 200, responseText);

                }else {
                    System.out.println("El Toquen fornecido no es igual al del servidor");
                    String responseBody = "El Toquen fornecido no es igual al del servidor";
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getOutputStream().write(responseBody.getBytes());

                }
            }else {
                String responseBody = "Token no válido. Acceso denegado en el segundo servidor.";
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getOutputStream().write(responseBody.getBytes());
            }
        } catch (SQLException e) {

            throw new RuntimeException(e);

        }
    }
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Configurar encabezados CORS
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:5501"); // Cambia esto al dominio apropiado
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

            String authorizationHeader = request.getHeader("Authorization");
            System.out.println("teste DELETE authorizationHeader=" + authorizationHeader);

            if (authorizationHeader != null) {
                // Eliminar comillas alrededor del token que el cliente envió
                authorizationHeader = authorizationHeader.replace("\"", "");
                System.out.println("teste DELETE authorizationHeader=" + authorizationHeader);

                if (isValidToken(authorizationHeader)) {
                    System.out.println("Token DELETE autorizado!!!!!!!!!");

                    // Obtén los datos del cuerpo de la solicitud DELETE
                    BufferedReader reader = request.getReader();
                    StringBuilder requestBody = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        requestBody.append(line);
                    }

                    // Convierte los datos JSON del cuerpo en un objeto Destination
                    Gson gson = new Gson();
                    Destination deletedDestination = gson.fromJson(requestBody.toString(), Destination.class);

                    // Elimina el destino de la base de datos
                    destinationDAO.deleteDestination(deletedDestination);

                    String responseText = "Destino Eliminado correctamente.";
                    sendResponse(response, 200, responseText);
                } else {
                    System.out.println("El Token proporcionado no es igual al del servidor");
                    String responseText = "El Token proporcionado no es igual al del servidor";
                    sendResponse(response, 403, responseText);
                }
            } else {
                String responseText = "Token no válido. Acceso denegado en el segundo servidor.";
                sendResponse(response, 401, responseText);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void sendJsonResponse(HttpServletResponse response, String jsonResponse) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        OutputStream os = response.getOutputStream();
        os.write(jsonResponse.getBytes("UTF-8"));
    }

    private void sendResponse(HttpServletResponse response, int statusCode, String responseText) throws IOException {
        response.setStatus(statusCode);
        response.setCharacterEncoding("UTF-8");
        OutputStream os = response.getOutputStream();
        os.write(responseText.getBytes("UTF-8"));
    }

    private boolean isValidToken(String token) {

        for (String validToken : validTokens) {
            if (token.equals(validToken)) {
                return true;
            }
        }
        return false;

    }

    private Map<String, String> parseQueryParameters(String query) {
        Map<String, String> queryParams = new HashMap<>();
        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    queryParams.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return queryParams;
    }
}

