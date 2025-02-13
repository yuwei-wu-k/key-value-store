import java.io.*;
import java.net.*;

/**
 * TCPClient.
 */
public class TCPClient {
  public static void main(String[] args) throws IOException {
    // Prompt user to input connection information
    Helper.ClientLog("Input the hostname or IP address and the port number of the server in the form of " + "<hostname> <port number>, for example: \"localhost 9086\".");
    // To enable user input from console using BufferReader
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    // Reading connection info using readLine
    String input = reader.readLine();
    // Splitting the input line to get hostname or IP address and port number
    String[] parts = input.split(" ");
    // First part is the hostname or ip address
    String host = parts[0];
    // Second part is the port number
    int port = Integer.parseInt(parts[1]);

    try (Socket client = new Socket(host, port);
         DataInputStream is = new DataInputStream(client.getInputStream());
         DataOutputStream os = new DataOutputStream(client.getOutputStream())) {

      client.setSoTimeout(3000); // Set timeout to 3 seconds

      // Pre-populate the key-value store
      prePopulateStore(os, is);

      // Perform minimum operations
      performMinimumOperations(os, is);

      // Interactive mode
      userInteraction(os, is);

    } catch (SocketTimeoutException e) {
      Helper.ClientLog("Server did not respond within the timeout period.");
    } catch (IOException e) {
      Helper.ClientLog("Error communicating with server: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Pre-populate the key-value store with the data from the file PREPOPULATION.
   *
   * @param os output stream
   * @param is input stream
   * @throws IOException if an I/O error occurs
   */
  private static void prePopulateStore(DataOutputStream os, DataInputStream is) throws IOException {
    try (BufferedReader fileReader = new BufferedReader(new FileReader("src/PREPOPULATION"))) {
      String line;
      while ((line = fileReader.readLine()) != null) {
        String[] kv = line.split(" ");
        if (kv.length == 2) {
          os.writeUTF("PUT " + kv[0] + " " + kv[1]);
          os.flush();
          Helper.ClientLog("Pre-populated key-value store with key = " + kv[0] + " and value = " + kv[1]);
          Helper.ClientLog("Server response: " + is.readUTF());
        }
      }
    }
  }

  /**
   * Perform minimum operations on the key-value store from the file MINIMUM_OPERATION.
   *
   * @param os output stream
   * @param is input stream
   * @throws IOException if an I/O error occurs
   */
  private static void performMinimumOperations(DataOutputStream os, DataInputStream is) throws IOException {
    try (BufferedReader fileReader = new BufferedReader(new FileReader("src/MINIMUM_OPERATION"))) {
      String line;
      while ((line = fileReader.readLine()) != null) {
        os.writeUTF(line);
        os.flush();
        Helper.ClientLog("Performed operation: " + line);
        Helper.ClientLog("Server response: " + is.readUTF());
      }
    }
  }

  /**
   * Interactive mode to allow user to input requests to the server.
   *
   * @param os output stream
   * @param is input stream
   * @throws IOException if an I/O error occurs
   */
  private static void userInteraction(DataOutputStream os, DataInputStream is) throws IOException {
    BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
    Helper.ClientLog("Enter your requests (PUT <key> <value>, GET <key>, DELETE <key>):");

    while (true) {
      String input = consoleReader.readLine();
      if (input.equalsIgnoreCase("exit")) {
        break;
      }
      os.writeUTF(input);
      os.flush();
      Helper.ClientLog("Sent request: " + input);
      Helper.ClientLog("Server response: " + is.readUTF());
    }
  }
}