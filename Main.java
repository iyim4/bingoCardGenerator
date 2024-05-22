import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    
    public static void main(String[] args) throws IOException {
        Scanner console = new Scanner(System.in);
        int width = 5;
        boolean freespace = true;
        ArrayList<String> list;

        // welcome and get file
        System.out.println("Welcome to the bingo card generator! First, what is the is the data file name?\n" + 
        "(Note: it must be in the same file/directory as this program)");
        File file = new File("./" + console.nextLine());
        list = readFile(file);
        System.out.println("File found sucessfully. There are " + list.size() + " words.");

        // split method here!!
        do {
            System.out.println("Would you like to generate a bingo card with a width of " + width +
                ", and " + ((freespace) ? "a freespace?" : "NO freespace?"));
            System.out.println("1. yes\n2. No, change width\n3. No, change freespace");
            int input = 0;  //Q? how to do this efficently???
            while (!console.hasNextInt()) {
                System.out.println("Please enter 1, 2, or 3.");
                console.nextLine();
                if (console.hasNextInt()) {
                    input = console.nextInt();
                    if (1 <= input || input <= 3) break;
                }
            }
            switch (input) {
                case 1:
                    // generate card
                    int freespaceIndex = width * width / 2;
                    printCard(list, width, freespace, freespaceIndex);
                    break;
                case 2:
                    // change width
                    break;
                case 3:
                    // change freespace
                    break;
                default: 
                    //error message
                    break;
            }
            System.out.println("Type y generate another card or any other key to quit.");
        } while (console.nextLine().toLowerCase() != "y");
        System.out.println("Thank you!");
        console.close();
    }

    /**
     * returns an ArrayList of strings from file
     */
    private static ArrayList<String> readFile(File file) {
        ArrayList<String> list = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                list.add(scanner.next());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found.");
            e.printStackTrace();
        }
        return list;
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
            // q? todo: align
            if (i % width == 0) result.append('\n');
            result.append(list.get(list.size() - 1 - i)).append('\t');
        }
        result.append('\n');
        System.out.println(result.toString());
    }
    
}
