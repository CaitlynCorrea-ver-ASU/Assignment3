
import mysteryservice.MysteryService;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.*;
import java.io.*;
import java.util.Scanner;

/**
 */
class SockClient {
  static Socket sock = null;
  static String host = "localhost";
  static int port = 8888;
  static OutputStream out;
  // Using and Object Stream here and a Data Stream as return. Could both be the
  // same type I just wanted
  // to show the difference. Do not change these types.
  static ObjectOutputStream os;
  static DataInputStream in;

  public static void main(String args[]) {

    if (args.length != 2) {
      System.out.println("Expected arguments: <host(String)> <port(int)>");
      System.exit(1);
    }

    try {
      host = args[0];
      port = Integer.parseInt(args[1]);
    } catch (NumberFormatException nfe) {
      System.out.println("[Port|sleepDelay] must be an integer");
      System.exit(2);
    }

    try {
      connect(host, port); // connecting to server
      System.out.println("Client connected to server.");
      boolean requesting = true;
      while (requesting) {
        System.out.println(
            "What would you like to do: 1 - echo, 2 - add, 3 - string concatenation, 4 - calculate many, 5 - playlist manager, 0 - quit");
        Scanner scanner = new Scanner(System.in);
        int choice = Integer.parseInt(scanner.nextLine());
        // You can assume the user put in a correct input, you do not need to handle
        // errors here
        // You can assume the user inputs a String when asked and an int when asked. So
        // you do not have to handle user input checking
        JSONObject json = new JSONObject(); // request object
        switch (choice) {
          case 0:
            System.out.println("Choose quit. Thank you for using our services. Goodbye!");
            requesting = false;
            break;
          case 1:
            System.out.println("Choose echo, which String do you want to send?");
            String message = scanner.nextLine();
            json.put("type", "echo");
            json.put("data", message);
            break;
          case 2:
            System.out.println("Choose add, enter first number:");
            String num1 = scanner.nextLine();
            json.put("type", "add");
            json.put("num1", num1);

            System.out.println("Enter second number:");
            String num2 = scanner.nextLine();
            json.put("num2", num2);
            break;
          case 3:
            System.out.println("Choose string concatenation, enter first string:");
            String str1 = scanner.nextLine();
            System.out.println("Enter second string:");
            String str2 = scanner.nextLine();
            json.put("type", "stringconcatenation");
            json.put("string1", str1);
            json.put("string2", str2);
            break;

          case 4:
            System.out.println("Choose operation: add, multiply, average");
            String operation = scanner.nextLine().trim();

            System.out.println("Enter integers separated by commas (example: 1,2,3):");
            String input = scanner.nextLine();

            String[] values = input.trim().isEmpty() ? new String[0] : input.split(",");

            JSONArray numList = new JSONArray();

            for (String value : values) {
              String trimmedValue = value.trim();
              try {
                numList.put(Integer.parseInt(trimmedValue));
              } catch (NumberFormatException e) {
                // Preserve invalid values so the server can validate them.
                numList.put(trimmedValue);
              }
            }

            json.put("type", "calculatemany");
            json.put("numList", numList);
            json.put("operation", operation);
            break;

          case 5:
            System.out.println("Playlist Manager");
            System.out.println("Choose action: add, remove, list, clear");
            String action = scanner.nextLine().trim().toLowerCase();

            json.put("type", "playlist");
            json.put("action", action);

            if (action.equals("add")) {
              System.out.println("Enter song title:");
              String song = scanner.nextLine();
              System.out.println("Enter artist name:");
              String artist = scanner.nextLine();

              json.put("song", song);
              json.put("artist", artist);

            } else if (action.equals("remove")) {
              System.out.println("Enter song title to remove:");
              String song = scanner.nextLine();

              json.put("song", song);
            }
            break;
        }
        if (!requesting) {
          continue;
        }

        // write the whole message
        os.writeObject(json.toString());
        // make sure it wrote and doesn't get cached in a buffer
        os.flush();

        // handle the response
        // - not doing anything other than printing some things, make this better
        // !! you will most likely need to parse the response for the other 2 services!
        String i = (String) in.readUTF();
        JSONObject res = new JSONObject(i);
        System.out.println("Got response: " + res);

        if (res.getBoolean("ok")) {
          String type = res.getString("type");

          if (type.equals("echo")) {
            System.out.println(res.getString("echo"));

          } else if (type.equals("stringconcatenation")) {
            System.out.println(res.getString("result"));

          } else if (type.equals("calculatemany")) {
            String operation = res.getString("operation");

            System.out.println("Operation: " + operation);
            System.out.println("Count: " + res.getInt("count"));

            switch (operation) {
              case "add":
                System.out.println("Sum: " + res.getInt("sum"));
                break;
              case "multiply":
                System.out.println("Product: " + res.getInt("product"));
                break;
              case "average":
                System.out.println("Average: " + res.getDouble("average"));
                break;
              default:
                System.out.println("Unknown CalculateMany Operation...");
                break;
            }
          } else if (type.equals("playlist")) {
            String action = res.getString("action");

            if (action.equals("add") || action.equals("remove")) {
              System.out.println(res.getString("message"));
              System.out.println("Songs in playlist: " + res.getInt("songCount"));

            } else if (action.equals("list")) {
              JSONArray songs = res.getJSONArray("songs");
              System.out.println("Playlist (" + res.getInt("songCount") + " songs):");

              if (songs.length() == 0) {
                System.out.println("Playlist is empty.");
              }

              for (int j = 0; j < songs.length(); j++) {
                JSONObject song = songs.getJSONObject(j);
                System.out.println((j + 1) + ". " + song.getString("song") + " - " + song.getString("artist"));
              }

            } else if (action.equals("clear")) {
              System.out.println(res.getString("message"));
            }
          } else {
            System.out.println(res.getInt("result"));
          }

        } else {
          System.out.println(res.getString("message"));
        }

      }
      // want to keep requesting services so don't close connection
      // overandout();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void overandout() throws IOException {
    // closing things, could
    in.close();
    os.close();
    sock.close(); // close socked after sending
  }

  public static void connect(String host, int port) throws IOException {
    // open the connection
    sock = new Socket(host, port); // connect to host and socket on port 8888

    // get output channel
    out = sock.getOutputStream();

    // create an object output writer (Java only)
    os = new ObjectOutputStream(out);

    in = new DataInputStream(sock.getInputStream());
  }
}