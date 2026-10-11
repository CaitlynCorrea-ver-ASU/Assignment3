import org.json.JSONArray;
import org.json.JSONObject;

import java.net.*;
import java.io.*;
import java.util.Scanner;

/**
 * Hangman Game Client - Student Starter Code
 *
 * Your task: Implement the protocol communication for all game features.
 *
 * What's provided:
 * - Menu structure for the required game states
 * - Name handling as a complete example
 * - Some method stubs as examples
 *
 * What you need to implement:
 * - Protocol requests/responses for the required game operations
 * - Proper response handling and display
 */
public class HangmanClient {
    static Socket sock;
    static ObjectOutputStream oos;
    static ObjectInputStream in;

    static Scanner scanner = new Scanner(System.in);
    static boolean inGame = false;
    static boolean hasName = false;
    static String playerName = "";

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Expected arguments: <host(String)> <port(int)>");
            System.exit(1);
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        try {
            sock = new Socket(host, port);
            oos = new ObjectOutputStream(sock.getOutputStream());
            in = new ObjectInputStream(sock.getInputStream());

            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║     WELCOME TO HANGMAN GAME!           ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println();

            boolean running = true;
            while (running) {
                if (!hasName) {
                    running = showInitialMenu();
                } else if (!inGame) {
                    running = showMainMenu();
                } else {
                    running = showGameMenu();
                }
                System.out.println();
            }

            closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initial menu - before name is set
     */
    static boolean showInitialMenu() {
        System.out.println("────────────────────────────────────────");
        System.out.println("  1. Set Your Name");
        System.out.println("  2. Quit");
        System.out.println("────────────────────────────────────────");
        System.out.print("Enter choice: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                setName();
                return true;
            case "2":
                quit();
                return false;
            default:
                System.out.println("Invalid choice. Please try again.");
                return true;
        }
    }

    /**
     * Main menu - after name set, no active game
     */
    static boolean showMainMenu() {
        System.out.println("────────────────────────────────────────");
        System.out.println("MAIN MENU:");
        System.out.println("  1. Start New Game");
        System.out.println("  2. Quit");
        System.out.println("────────────────────────────────────────");
        System.out.print("Enter choice: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                startGame();
                return true;
            case "2":
                quit();
                return false;
            default:
                System.out.println("Invalid choice. Please try again.");
                return true;
        }
    }

    /**
     * Game menu - during active game
     * Natural input: type one letter to guess
     * Commands: 1, 2, 0 for special actions
     */
    static boolean showGameMenu() {
        System.out.println("\n────────────────────────────────────────");
        System.out.println("Type one letter to guess");
        System.out.println("Or choose:");
        System.out.println("  1 - Show game state");
        System.out.println("  2 - Give up (return to main menu)");
        System.out.println("  0 - Quit game");
        System.out.println("────────────────────────────────────────");
        System.out.print("Your input: ");
        String input = scanner.nextLine().trim();

        // Handle special commands
        if (input.equals("1")) {
            // TODO: Send a state request and display the response
            return true;
        } else if (input.equals("2")) {
            giveUp();
            return true;
        } else if (input.equals("0")) {
            quit();
            return false;
        }

        if (input.isEmpty()) {
            System.out.println("Please enter a letter or command.");
            return true;
        }

        // TODO: Send the input as a guess request. The server is responsible
        // for validating that it contains exactly one alphabetic letter.

        return true;
    }

    /**
     * TODO: Implement the rest for giveUp so it is handled correctly
     */
    static void giveUp() {
        System.out.print("\nAre you sure you want to give up? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (confirm.equals("yes") || confirm.equals("y")) {
            // TODO: Send a give-up request. Set inGame to false only after
            // the server confirms that the game ended successfully.
            System.out.println("[TODO: Send a give-up request to the server]");
        } else {
            System.out.println("\nContinuing game...");
        }
    }

    /**
     * EXAMPLE IMPLEMENTATION: Set player name
     * This is provided as a complete example.
     * Use this as a reference for implementing other methods.
     */
    static void setName() {
        System.out.print("\nEnter your name: ");
        String name = scanner.nextLine().trim();

        // Create request according to YOUR protocol design
        JSONObject request = new JSONObject();
        request.put("type", "name");
        request.put("name", name);

        // Send request and get response
        JSONObject response = sendRequest(request);
        if (response != null) {
            if (response.getBoolean("ok")) {
                hasName = true;
                playerName = name;
                System.out.println("\n" + response.getString("message"));
                System.out.println();
            } else {
                System.out.println("✗ Error: " + response.getString("message"));
            }
        }
    }

    /**
     * TODO: Implement start game
     * Should send a start request to the server and handle the response
     */
    static void startGame() {
        System.out.println("\n[TODO: Implement start game - send start request to server]");
    }


    /**
     * Quit game
     */
    static boolean quit() {
        JSONObject request = new JSONObject();
        request.put("type", "quit");

        JSONObject response = sendRequest(request);
        if (response != null && response.getBoolean("ok")) {
            System.out.println("\n" + response.getString("message"));
            System.out.println("Thanks for playing!");
        }
        return false; // Stop the main loop
    }

    /**
     * Helper: Send request and receive response
     * This handles the basic communication pattern
     */
    static JSONObject sendRequest(JSONObject request) {
        try {
            String req = request.toString();
            oos.writeObject(req);
            oos.flush();

            String res = (String) in.readObject();
            return new JSONObject(res);
        } catch (Exception e) {
            System.out.println("Error communicating with server: " + e.getMessage());
            return null;
        }
    }

    /**
     * Close connection
     */
    static void closeConnection() {
        try {
            if (oos != null) oos.close();
            if (in != null) in.close();
            if (sock != null) sock.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
