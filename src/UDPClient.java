import java.io.*;
import java.net.*;

/**
 * UDPClient.
 */
public class UDPClient {
  private static final int TIMEOUT_MS = 3000;
  private static final int BUFFER_SIZE = 1024;

  public static void main(String[] args) {
    // Prompt user to input connection information
    Helper.ClientLog("Input the hostname or IP address and the port number of the server in the form of <hostname> <port number>, for example: \"localhost 9086\".");

    // To enable user input from console using BufferedReader
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    try {
      // Reading connection info using readLine
      String input = reader.readLine();
      String[] parts = input.split(" ");

      // Validate user input
      if (parts.length < 2) {
        Helper.ClientLog("ERROR: Invalid input format. Please provide hostname and port number.");
        return;
      }

      String host = parts[0];
      int port = Integer.parseInt(parts[1]);

      // Create a UDP socket
      try (DatagramSocket udpSocket = new DatagramSocket()) {
        udpSocket.setSoTimeout(TIMEOUT_MS); // Set timeout for server response

        // Pre-populate the key-value store
        prePopulateStore(udpSocket, host, port);

        // Perform minimum operations
        performMinimumOperations(udpSocket, host, port);

        // Interactive mode
        userInteraction(udpSocket, host, port);
      } catch (SocketTimeoutException e) {
        Helper.ClientLog("Server did not respond within the timeout period.");
      } catch (IOException e) {
        Helper.ClientLog("Error communicating with server: " + e.getMessage());
        e.printStackTrace();
      }
    } catch (IOException e) {
      Helper.ClientLog("Error reading user input: " + e.getMessage());
    }
  }

  /**
   * Pre-populate the key-value store with the data from the file PREPOPULATION.
   *
   * @param udpSocket the UDP socket
   * @param host      the server host
   * @param port      the server port
   * @throws IOException if an I/O error occurs
   */
  private static void prePopulateStore(DatagramSocket udpSocket, String host, int port) throws IOException {
    try (BufferedReader fileReader = new BufferedReader(new FileReader("src/PREPOPULATION"))) {
      String line;
      while ((line = fileReader.readLine()) != null) {
        String[] kv = line.split(" ");
        if (kv.length == 2) {
          String request = "PUT " + kv[0] + " " + kv[1];
          sendRequest(udpSocket, host, port, request);
          Helper.ClientLog("Pre-populated key-value store with key = " + kv[0] + " and value = " + kv[1]);
          String response = receiveResponse(udpSocket);
          Helper.ClientLog("Server response: " + response);
        }
      }
    }
  }

  /**
   * Perform minimum operations on the key-value store from the file MINIMUM_OPERATION.
   *
   * @param udpSocket the UDP socket
   * @param host      the server host
   * @param port      the server port
   * @throws IOException if an I/O error occurs
   */
  private static void performMinimumOperations(DatagramSocket udpSocket, String host, int port) throws IOException {
    try (BufferedReader fileReader = new BufferedReader(new FileReader("src/MINIMUM_OPERATION"))) {
      String line;
      while ((line = fileReader.readLine()) != null) {
        sendRequest(udpSocket, host, port, line);
        Helper.ClientLog("Performed operation: " + line);
        String response = receiveResponse(udpSocket);
        Helper.ClientLog("Server response: " + response);
      }
    }
  }

  /**
   * Interactive mode to allow user to input requests to the server.
   *
   * @param udpSocket the UDP socket
   * @param host      the server host
   * @param port      the server port
   * @throws IOException if an I/O error occurs
   */
  private static void userInteraction(DatagramSocket udpSocket, String host, int port) throws IOException {
    BufferedReader bufferReader = new BufferedReader(new InputStreamReader(System.in));
    Helper.ClientLog("Enter your requests (PUT <key> <value>, GET <key>, DELETE <key>):");

    while (true) {
      String input = bufferReader.readLine();
      if (input.equalsIgnoreCase("exit")) {
        break;
      }
      sendRequest(udpSocket, host, port, input);
      Helper.ClientLog("Sent request: " + input);
      String response = receiveResponse(udpSocket);
      Helper.ClientLog("Server response: " + response);
    }
  }

  /**
   * Send a request to the server.
   *
   * @param udpSocket the UDP socket
   * @param host      the server host
   * @param port      the server port
   * @param request   the request to send
   * @throws IOException if an I/O error occurs
   */
  private static void sendRequest(DatagramSocket udpSocket, String host, int port, String request) throws IOException {
    InetAddress address = InetAddress.getByName(host);
    byte[] buffer = request.getBytes();
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
    udpSocket.send(packet);
  }

  /**
   * Receive a response from the server.
   *
   * @param udpSocket the UDP socket
   * @return the server's response
   * @throws IOException if an I/O error occurs
   */
  private static String receiveResponse(DatagramSocket udpSocket) throws IOException {
    byte[] buffer = new byte[BUFFER_SIZE];
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    udpSocket.receive(packet);
    return new String(packet.getData(), 0, packet.getLength());
  }
}