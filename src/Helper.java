import java.text.SimpleDateFormat;

/**
 * Helper class to provide logging and other utility functions.
 */
public class Helper {

  // Create a date time formatter
  static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

  /**
   * Log client information.
   *
   * @param log The log message.
   */
  public static void ClientLog(String log) {
    // Prompt user to input connection information
    System.out.println("<Client log at " + formatter.format(System.currentTimeMillis()) + ">: " + log);
  }

  /**
   * Log server information.
   *
   * @param log The log message.
   */
  public static void ServerLog(String log) {
    // Prompt user to input connection information
    System.out.println("<Server log at " + formatter.format(System.currentTimeMillis()) + ">: " + log);
  }
}
