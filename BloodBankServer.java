import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;

public class BloodBankServer {
    private static List<Donor> donors = new ArrayList<>();
    private static BloodInventory inventory = new BloodInventory();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Serve homepage
        server.createContext("/", exchange -> {
            byte[] response = java.nio.file.Files.readAllBytes(new File("index1.html").toPath());
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        });

        // Add Donor
        server.createContext("/addDonor", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                Map<String, String> params = parseParams(exchange);
                donors.add(new Donor(params.get("name"), params.get("bloodGroup"), params.get("contact")));
                sendSuccessResponse(exchange, "Donor added successfully!");
            }
        });

        // Donate Blood
        server.createContext("/donateBlood", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                Map<String, String> params = parseParams(exchange);
                inventory.addBlood(params.get("bloodGroup"), Integer.parseInt(params.get("units")));
                sendSuccessResponse(exchange, "Blood donated successfully!");
            }
        });

        // Request Blood
        server.createContext("/requestBlood", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                Map<String, String> params = parseParams(exchange);
                boolean success = inventory.requestBlood(params.get("bloodGroup"), Integer.parseInt(params.get("units")));
                String msg = success ? "Blood request fulfilled successfully!" : "Insufficient blood units available.";
                sendResponse(exchange, msg, success);
            }
        });

        // Show Donors
        server.createContext("/showDonors", exchange -> {
            StringBuilder sb = new StringBuilder("<h2>Donors List</h2>");
            sb.append("<table border='1' cellpadding='8' style='border-collapse:collapse;width:100%'>");
            sb.append("<tr><th>Name</th><th>Blood Group</th><th>Contact</th></tr>");
            donors.forEach(d -> sb.append("<tr><td>").append(d.getName()).append("</td><td>")
                    .append(d.getBloodGroup()).append("</td><td>").append(d.getContact()).append("</td></tr>"));
            sb.append("</table>");
            sendHtmlResponse(exchange, sb.toString());
        });

        // Show Inventory
        server.createContext("/showInventory", exchange -> {
            StringBuilder sb = new StringBuilder("<h2>Blood Inventory</h2>");
            sb.append("<table border='1' cellpadding='8' style='border-collapse:collapse;width:100%'>");
            sb.append("<tr><th>Blood Group</th><th>Units</th></tr>");
            inventory.getEntries().forEach(entry -> sb.append("<tr><td>").append(entry.getKey())
                    .append("</td><td>").append(entry.getValue()).append("</td></tr>"));
            sb.append("</table>");
            sendHtmlResponse(exchange, sb.toString());
        });

        System.out.println("Server started at http://localhost:8080/");
        server.start();
    }

    private static Map<String, String> parseParams(HttpExchange exchange) throws IOException {
        String query = new String(exchange.getRequestBody().readAllBytes());
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                String value = entry[1].replace("+", " ");
                // Decode URL encoded characters
                value = java.net.URLDecoder.decode(value, "UTF-8");
                result.put(entry[0], value);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }

    private static void sendSuccessResponse(HttpExchange exchange, String message) throws IOException {
        sendResponse(exchange, message, true);
    }

    private static void sendResponse(HttpExchange exchange, String message, boolean success) throws IOException {
        String type = success ? "success" : "error";
        String response = "<script>showMessage('" + message + "', '" + type + "');</script>";
        sendHtmlResponse(exchange, response);
    }

    private static void sendHtmlResponse(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}