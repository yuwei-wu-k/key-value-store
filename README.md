# key-value-store

## Assignment Overview

The objective of this assignment is to design and implement a single-server key-value store 
that supports basic operations: PUT, GET, and DELETE. The server must be single-threaded and must respond to only one request at a time.
The scope of the assignment is to implement the server program using two different communication protocols, UDP and TCP. 
The client and server programs must use sockets for communication, and the user can choose to run the file with either UDP or TCP.
The key-value store was implemented using a concurrent hash map, enabling efficient data retrieval and manipulation.
Additionally, the client implementation incorporated functions for pre-populating the key-value store, 
executing a set of predefined operations, and providing real-time user interaction. 
The system was designed to log all operations with timestamps, handle malformed requests, and ensure responsiveness with timeouts. 
This assignment reinforced concepts related to network communication, concurrency, and structured data storage 
while ensuring proper software engineering practices.

### Technical Impression
Implementing the key-value store using both TCP and UDP provided me valuable insights into the differences 
between these communication protocols. I've already known how to implement TCP as this is the previous homework.
But UDP is a bit harder as it focuses on low-latency, connectionless messaging. After researching, I found that I need 
to use `DatagramPacket` for containing the data and `DatagramSocket` for sending and receiving the data.

Another challenge for me is to think about how to handle the prepopulated data and the user input data. I discussed with
classmates and decided to use BufferedReader to read the file with prepopulated data and then read the minimum_operation 
file to complete the minimum operation requirement. For the user input, I used Scanner to read the input from the user.

Besides, the protocol needed to distinguish between different commands while ensuring that malformed requests 
were handled gracefully. As exception handling played a crucial role, I ensured my implementation could deal with issues 
such as invalid input formats, network timeouts, and unexpected client-server disconnections.

### Use Case Application
A key-value store provides flexibility and allows us to scale the applications that have unstructured data. 
Web applications can use key-value stores to store information about a user’s session and preferences. 
When using a user key, all the data is accessible, and key-value stores are ideal for rapid reads and write operations.

Key-value stores are particularly useful for real-time recommendations and advertising, 
as they can quickly retrieve and display fresh content. 
A practical application of this technology is in distributed caching systems, commonly used in large-scale web applications. 
A key-value store supporting both TCP and UDP communication can function as a lightweight, efficient caching layer, 
reducing response times for frequently accessed data.

For instance, an e-commerce platform could leverage such a system to store session data, product inventory statuses, 
and user preferences, ensuring fast retrieval while minimizing the load on the primary database. 
By utilizing UDP for high-speed operations that tolerate occasional data loss and TCP for critical transactions 
requiring reliability, this implementation effectively balances performance and consistency.

### Build and Run Instructions
1. Clone the repository to your local machine

2. Under the /src directory, compile the server and client files

   ` javac TCPClient.java TCPServer.java UDPClient.java UDPServer.java`

3. Run the server program
   - ` java TCPServer` or `java UDPServer`
   - `9086` (or any other port number)

4. Run the client program
   - ` java TCPClient` or `java UDPClient`
   - `localhost` (or the server's IP address)

5. Follow the prompts to interact with the key-value store using PUT, GET, and DELETE commands

### Additional Notes
- The PREPOPULATE file contains sample key-value pairs for preloading the store
- The MINIMUM_OPERATIONS file contains a set of predefined operations for the client to execute