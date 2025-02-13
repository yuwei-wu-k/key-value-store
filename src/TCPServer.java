import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TCPServer.
 */
public class TCPServer {
  private static final ConcurrentHashMap<Integer, Integer> keyValueStore = new ConcurrentHashMap<>();

  public static void main(String[] args) throws IOException {
    // Prompt user to input connection information
    Helper.ServerLog("Input the port number of the server as <port number>, for example: " + "\"9086\".");
    // To enable user input from console using BufferReader
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    // Reading connection info using readLine
    String input = reader.readLine();

    // Register service on port
    ServerSocket server;
    try {
      int port = Integer.parseInt(input);
      server = new ServerSocket(port);
      Helper.ServerLog("TCP server started on port: " + port);
    } catch (IOException e) {
      // Prompt user with input error
      Helper.ServerLog("Cannot connect to the given port number, please check your input and" + " try again.");
      throw new RuntimeException(e);
    }

    while (true) {
      Socket client = server.accept();
      Helper.ServerLog("Client connected: " + client.getInetAddress());
      new Thread(() -> handleClient(client)).start();
    }
  }

  /**
   * Handle a client connection.
   *
   * @param client TCP client socket
   */
  private static void handleClient(Socket client) {
    try (DataInputStream is = new DataInputStream(client.getInputStream());
         DataOutputStream os = new DataOutputStream(client.getOutputStream())) {

      while (true) {
        String request = is.readUTF(); // Read the next request
        String response = processRequest(request, client.getInetAddress().toString(), client.getPort());
        os.writeUTF(response);
        os.flush();
      }

    } catch (EOFException e) {
      Helper.ServerLog("Client disconnected: " + client.getInetAddress());
    } catch (IOException e) {
      Helper.ServerLog("Error handling client: " + e.getMessage());
    } finally {
      try {
        client.close();
        Helper.ServerLog("Client connection closed: " + client.getInetAddress());
      } catch (IOException e) {
        Helper.ServerLog("Error closing client socket: " + e.getMessage());
      }
    }
  }

  /**
   * Process TCP client request including PUT, GET, and DELETE commands.
   *
   * @param request       client request
   * @param clientAddress client IP address
   * @param clientPort    client port
   * @return response to the client
   */
  private static String processRequest(String request, String clientAddress, int clientPort) {
    // Log the received request
    Helper.ServerLog("Received request from " + clientAddress + ":" + clientPort + ": " + request);

    String[] parts = request.split(" ", 3);
    if (parts.length < 2) {
      String error = "ERROR: Invalid request format";
      Helper.ServerLog(error);
      return error;
    }

    String command = parts[0].toUpperCase();
    String keyStr = parts[1];

    try {
      int key = Integer.parseInt(keyStr);
      switch (command) {
        case "PUT":
          if (parts.length < 3) {
            String error = "ERROR: Missing value for PUT";
            Helper.ServerLog(error);
            return error;
          }
          String valueStr = parts[2];
          try {
            int value = Integer.parseInt(valueStr);
            keyValueStore.put(key, value);
            String success = "PUT succeeded: key = " + key + ", value = " + value;
            Helper.ServerLog(success);
            return success;
          } catch (NumberFormatException e) {
            String error = "ERROR: Invalid value format (must be an integer)";
            Helper.ServerLog(error);
            return error;
          }

        case "GET":
          Integer value = keyValueStore.get(key);
          if (value == null) {
            String error = "ERROR: Key not found: " + key;
            Helper.ServerLog(error);
            return error;
          }
          String success = "GET succeeded: key = " + key + ", value = " + value;
          Helper.ServerLog(success);
          return success;

        case "DELETE":
          if (keyValueStore.remove(key) != null) {
            success = "DELETE succeeded: key = " + key;
            Helper.ServerLog(success);
            return success;
          } else {
            String error = "ERROR: Key not found: " + key;
            Helper.ServerLog(error);
            return error;
          }

        default:
          String error = "ERROR: Unknown command: " + command;
          Helper.ServerLog(error);
          return error;
      }
    } catch (NumberFormatException e) {
      String error = "ERROR: Invalid key format (must be an integer)";
      Helper.ServerLog(error);
      return error;
    } catch (Exception e) {
      String error = "ERROR: Unexpected error: " + e.getMessage();
      Helper.ServerLog(error);
      return error;
    }
  }
}