import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * UDPServer.
 */
public class UDPServer {
  private static final int BUFFER_SIZE = 1024; // Buffer size for receiving data
  private static final ConcurrentHashMap<Integer, Integer> keyValueStore = new ConcurrentHashMap<>();

  public static void main(String[] args) {
    // Prompt user to input connection information
    Helper.ServerLog("Input the port number of the server as <port number>, for example: \"9086\".");

    // To enable user input from console using BufferedReader
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    try {
      // Reading connection info using readLine
      String input = reader.readLine();
      int port = Integer.parseInt(input);

      // Create a UDP socket and bind it to the specified port
      try (DatagramSocket udpSocket = new DatagramSocket(port)) {
        Helper.ServerLog("UDP server started on port " + port);

        while (true) {
          // Receive a packet from a client
          byte[] buffer = new byte[BUFFER_SIZE];
          DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
          udpSocket.receive(requestPacket);

          // Process the request in a new thread
          new Thread(() -> handleClient(udpSocket, requestPacket)).start();
        }
      } catch (IOException e) {
        Helper.ServerLog("Error starting UDP server: " + e.getMessage());
      }
    } catch (IOException e) {
      Helper.ServerLog("Error reading user input: " + e.getMessage());
    }
  }

  /**
   * Handle a client request.
   *
   * @param udpSocket      the UDP socket
   * @param requestPacket the request packet from the client
   */
  private static void handleClient(DatagramSocket udpSocket, DatagramPacket requestPacket) {
    try {
      // Get the client's address and port
      InetAddress clientAddress = requestPacket.getAddress();
      int clientPort = requestPacket.getPort();

      // Get the request data as a string
      String request = new String(requestPacket.getData(), 0, requestPacket.getLength());
      Helper.ServerLog("Received request from " + clientAddress + ":" + clientPort + ": " + request);

      // Process the request
      String response = processRequest(request, clientAddress.toString(), clientPort);

      // Log the response
      Helper.ServerLog("Sending response to " + clientAddress + ":" + clientPort + ": " + response);

      // Send the response back to the client
      byte[] responseBuffer = response.getBytes();
      DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, clientAddress, clientPort);
      udpSocket.send(responsePacket);
    } catch (IOException e) {
      Helper.ServerLog("Error handling client request: " + e.getMessage());
    }
  }

  /**
   * Process the request and generate a response.
   *
   * @param request       the client request
   * @param clientAddress the client's address
   * @param clientPort    the client's port
   * @return the response to send back to the client
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