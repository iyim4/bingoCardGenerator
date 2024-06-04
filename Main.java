import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.Math;

public class Main {
    private static int longestWordLen; // for card formatting
    
    public static void main(String[] args) throws IOException {
        Scanner keyboard = new Scanner(System.in);
        ArrayList<String> list = new ArrayList<>();

        // welcome and setup
        System.out.println("Welcome to the bingo card generator! ");
        getSourceFile(keyboard, list);
        // length of longest is used for formating, and only in runprogram. consider changing location or smthn.
        int width = (list.size() >= 25) ? 5 : (int) Math.sqrt(list.size());
        runProgram(keyboard, width, true, list);
        keyboard.close();
    }

    // fills list with words and sets lengthOfLongestWord.
    private static void getSourceFile(Scanner keyboard, ArrayList<String> list) {
        if (!list.isEmpty()) {
            list.clear();
        }
        
        boolean done = true;
        do {
            System.out.println("What is the is the data file name? (Note: it must be in the same " +
                "directory as this program)\nOptions:");
            listFiles();
            System.out.print("Enter a file name: ");
            File file = new File("./" + keyboard.nextLine());

            // attempt to read file
            int longest = -1;
            try {
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    String temp = scanner.nextLine(); 
                    list.add(temp);
                    if (temp.length() > longest) {
                        longest = temp.length();
                    }
                }
                longestWordLen = longest;
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
                error.printStackTrace(); //delete
                done = false;
            }
        } while (!done);
        System.out.println("File found sucessfully. There are " + list.size() + " words.");
    }

    private static void runProgram(Scanner keyboard, int width, boolean freespace, ArrayList<String> list) {
        boolean quit = false;
        do {
            System.out.println("\nWould you like to generate a bingo card with a width of " + width +
                ", and " + ((freespace) ? "a freespace?" : "NO freespace?"));
                String menuOptions = "\t1. Yes, generate card\t\n2. No, change width\t\n3. No, change freespace\t\n4. No, change data file\n5. Quit";
            int input = getIntFromUser(keyboard, 1, 5, menuOptions);
            System.out.println();

            switch (input) {
                case 1:
                    // generate card
                    printCard(list, width, freespace, (width * width / 2));
                    break;
                case 2:
                    // change width
                    int maxWidth = (int) Math.sqrt(list.size());
                    width = getIntFromUser(keyboard, 1, maxWidth, "Enter a width, max " + maxWidth);
                    break;
                case 3:
                    // change freespace
                    freespace = !freespace;
                    System.out.println("Freespace " + ((freespace) ? "added" : "removed"));
                    break;
                case 4:
                    // change file
                    getSourceFile(keyboard, list);
                    width = (list.size() >= 25) ? 5 : (int) Math.sqrt(list.size());
                    break;
                case 5:
                    // quit
                    quit = true;
                default: 
                    //error message
                    break;
            }
        } while (!quit);
        System.out.println("Thank you!");
    }

    private static int getIntFromUser(Scanner keyboard, int low, int high, String message) {
        boolean done = true;
        int result = 0;
        do {
            System.out.println(message);
            System.out.print("Enter a number between " + low + " and " + high + ": ");
            String input = keyboard.nextLine();
            try {
                result = Integer.parseInt(input);
                if (result > high || result < low) {
                    System.out.println("Invalid. Out of range");
                    done = false;
                } else {
                    done = true;
                }
            } catch (NumberFormatException error) {
                System.out.println("Invalid. Not a number");
                done = false;
            }
        } while (!done);
        return result;
    }

    // generates and prints card
    private static void printCard(ArrayList<String> list, int width, boolean freespace, int freespaceIndex) {
        Random random = new Random();
        int totNumWords = width * width;
        int endIndex = list.size() - 1;
        // place unique values at end of list and generate random numbers within a decreasingly 
        // smaller range, guaranteeing unique values every time without the need to reset the 
        // array for generating new cards.
        for (int i = 0; i < totNumWords; i++) {
            int index = random.nextInt(0, endIndex + 1);
            // swap the random word with the word at the end of the working list
            String temp = list.get(index);
            list.set(index, list.get(endIndex));
            list.set(endIndex, temp);
            // decrease random number generation range
            endIndex--;
        }

        // print out last totNumElems words of list
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < totNumWords; i++) {
            if (i % width == 0) result.append('\n');
            String format = "%1$-" + (longestWordLen + 1) + "s";
            String word = list.get(list.size() - 1 - i);
            if (i == freespaceIndex && freespace) {
                word = "FREE";
            }
            result.append(String.format(format, word));
        }
        result.append('\n');
        System.out.println(result.toString());
    }

    // lists files in this directory
    private static void listFiles() {
        String[] files = new File(".").list();
        for (String file : files) {
            System.out.println("\t" + file);
        }
        System.out.println();
    }
    
}
