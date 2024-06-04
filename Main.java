import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.Math;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner keyboard = new Scanner(System.in);
        ArrayList<String> list = new ArrayList<>();

        System.out.println("Welcome to the bingo card generator! ");
        int longestWordLen = getSourceFile(keyboard, list);
        runMenu(keyboard, list, longestWordLen);
        keyboard.close();
    }

    /**
     * Fills {@code list} with words from a file
     * @param keyboard for user input. Must not be null.
     * @param list the list to be filled. Previous content will be cleared. Must not be null.
     * @return the length of the longest word in the file
     */
    private static int getSourceFile(Scanner keyboard, ArrayList<String> list) {
        if (!list.isEmpty()) {
            list.clear();
        }
        boolean done = true;
        int longest = -1;

        do {
            System.out.println("What is the is the data file name? (Note: it must be within the same " +
                "directory as this program)\nOptions:");
            listFiles();
            System.out.print("Enter a file name: ");
            File file = new File(".\\" + keyboard.nextLine());

            // attempt to read file
            try {
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    String temp = scanner.nextLine(); 
                    list.add(temp);
                    if (temp.length() > longest) {
                        longest = temp.length();
                    }
                }
                scanner.close();

                // repeat if file was empty
                if (longest == -1) {
                    System.out.println("Error: File was empty."); 
                    done = false;
                } else {
                    done = true;
                }
            } catch (FileNotFoundException error) {
                System.out.println("Error: File not found.");
                done = false;
            }
        } while (!done);
        System.out.println("File found sucessfully. There are " + list.size() + " words.");
        return longest;
    }

    /**
     * Displays and runs menu options
     * @param keyboard for user input. Must not be null.
     * @param list list of words for the card. Must not be null.
     */
    private static void runMenu(Scanner keyboard, ArrayList<String> list, int longestWordLen) {
        boolean quit = false;
        boolean freespace = true; // initial include freespace
        boolean alphabetical = false; // initial alphabetical mode off
        int width = (list.size() >= 25) ? 5 : (int) Math.sqrt(list.size());  // initial width. default 5 if enough words.
        do {
            System.out.println("\nWould you like to generate a bingo card with a width of " + width + ", " + 
                    ((freespace) ? "a free space" : "NO free space") + ", and " + 
                    ((alphabetical) ? "alphabetically sorted?" : "not alphabetically sorted?"));
            String menuOptions = "\t1. Yes, generate card\n\t2. No, change width\n\t3. No, " +
                    "toggle Free space\n\t4. No, change data file\n\t5. No, toggle alphabetical mode\n\t6. Quit";
            int input = getIntFromUser(keyboard, 1, 6, menuOptions);
            System.out.println();

            switch (input) {
                case 1:
                    // generate card
                    printCard(list, width, freespace, longestWordLen, alphabetical);
                    break;
                case 2:
                    // change width
                    int maxWidth = (int) Math.sqrt(list.size());
                    width = getIntFromUser(keyboard, 1, maxWidth, "Enter a width, max " + maxWidth);
                    break;
                case 3:
                    // toggle freespace
                    freespace = !freespace;
                    System.out.println("Free space " + ((freespace) ? "added" : "removed"));
                    break;
                case 4:
                    // change file
                    getSourceFile(keyboard, list);
                    width = (list.size() >= 25) ? 5 : (int) Math.sqrt(list.size());
                    break;
                case 5:
                    // toggle alphabetical mode
                    alphabetical = !alphabetical;
                    System.out.println("Alphabetical mode turned " + ((alphabetical) ? "on" : "off"));
                    break;
                case 6:
                    // quit
                    quit = true;
                    break;
            }
        } while (!quit);
        System.out.println("Thank you!");
    }

    /**
     * Promts user for an integer
     * @param keyboard for user input. Must not be null.
     * @param low lower bound, inclusive
     * @param high upper bound, inclusive
     * @param message instructions for the user
     * @return an integer between {@code low} and {@code high}, inclusive.
     */
    private static int getIntFromUser(Scanner keyboard, int low, int high, String message) {
        boolean done = true;
        int result = 0;
        do {
            System.out.println(message);
            System.out.print("Enter a number between " + low + " and " + high + ": ");
            String input = keyboard.nextLine();

            // validate
            try {
                result = Integer.parseInt(input);
                if (result > high || result < low) {
                    System.out.println("Invalid. Out of range.");
                    done = false;
                } else {
                    done = true;
                }
            } catch (NumberFormatException error) {
                System.out.println("Invalid. Not a number.");
                done = false;
            }
        } while (!done);
        return result;
    }

    /**
     * Generates and prints the bingo card
     * @param list list of words for the card. Must not be null.
     * @param width width of bingo card. Must be less than the square root of list.size().
     * @param freespace whether to include a FREE space on the card.
     * @param longestWordLen length of the longest word in the card, for formatting
     */
    private static void printCard(ArrayList<String> list, int width, boolean freespace, int longestWordLen, boolean alphabetical) {
        Random random = new Random();
        int numBingoWords = width * width;
        int freespaceIndex = width * width / 2;
        int endIndex = list.size() - 1;
        
        // place unique values at end of list and generate random numbers within a decreasingly 
        // smaller range, guaranteeing unique values every funnction call without the need to reset  
        // the array for generating new cards. Worst case O(sqrt(N)) card generation.
        for (int i = 0; i < numBingoWords; i++, endIndex--) {
            int index = random.nextInt(0, endIndex + 1);
            // swap the random word with the word at the end of the working list
            String temp = list.get(index);
            list.set(index, list.get(endIndex));
            list.set(endIndex, temp);
        }

        // alphabetical mode: prints card in alphabetical order
        if (alphabetical) {
            Collections.sort(list.subList(list.size() - numBingoWords, list.size()));
        }

        // print out last numBingoWords words of list
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < numBingoWords; i++) {
            if (i % width == 0) result.append('\n');
            String format = "%1$-" + (longestWordLen + 1) + "s";
            String word = list.get(list.size() - numBingoWords + i);
            if (freespace && i == freespaceIndex) {
                word = "FREE";
            }
            result.append(String.format(format, word));
        }
        result.append('\n');
        System.out.println(result.toString());
    }

    /**
     * Lists files in this directory
     */
    private static void listFiles() {
        String[] files = new File(".").list();
        for (String file : files) {
            System.out.println("\t" + file);
        }
        System.out.println();
    }
    
}
