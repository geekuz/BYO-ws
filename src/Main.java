import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * A more efficient file counter utility inspired by wc.
 *
 * Refactored by Gemini Code Assist
 */
public class Main {

    // A record is a modern, concise way to create an immutable data carrier class.
    public record FileStats(long lineCount, long wordCount, long charCount, long byteCount) {}

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("File Counter Utility. Usage: [option] <filepath>. Type 'exit' to quit.");
            System.out.println("Options: -c (bytes), -l (lines), -w (words), -m (chars)");

            while (true) {
                System.out.print("> ");
                String line = sc.nextLine();
                if ("exit".equalsIgnoreCase(line.trim())) {
                    break;
                }

                // Simplified and more robust argument parsing
                String[] parts = line.trim().split("\\s+");
                if (parts.length == 0 || parts[0].isEmpty()) {
                    continue; // Skip empty lines
                }

                String option = null;
                String filePathStr;

                if (parts.length == 1) {
                    filePathStr = parts[0];
                } else if (parts.length == 2 && parts[0].startsWith("-")) {
                    option = parts[0];
                    filePathStr = parts[1];
                } else {
                    // Handle cases where the file path might have spaces, assuming option is first
                    if (parts[0].startsWith("-")) {
                        option = parts[0];
                        filePathStr = line.substring(line.indexOf(parts[1])).trim();
                    } else {
                        filePathStr = line.trim();
                    }
                }

                Path path = Paths.get(filePathStr);
                if (!Files.exists(path)) {
                    System.err.println("Error: File not found -> " + path);
                    continue;
                }

                try {
                    // The magic happens here: one call to process the file
                    FileStats stats = calculateStats(path);
                    String fileNameForDisplay = path.getFileName().toString();

                    // Use a final variable for the option to use in a switch expression (Java 14+)
                    // or a standard switch.
                    final String finalOption = option;

                    switch (finalOption) {
                        case "-c":
                            System.out.printf("%8d %s%n", stats.byteCount(), fileNameForDisplay);
                            break;
                        case "-l":
                            System.out.printf("%8d %s%n", stats.lineCount(), fileNameForDisplay);
                            break;
                        case "-w":
                            System.out.printf("%8d %s%n", stats.wordCount(), fileNameForDisplay);
                            break;
                        case "-m":
                            System.out.printf("%8d %s%n", stats.charCount(), fileNameForDisplay);
                            break;
                        case null:
                            // Default behavior: print lines, words, and bytes
                            System.out.printf("%8d %8d %8d %s%n",
                                    stats.lineCount(), stats.wordCount(), stats.byteCount(), fileNameForDisplay);
                            break;
                        default:
                            System.err.println("Error: Invalid option '" + finalOption + "'");
                            break;
                    }
                } catch (IOException e) {
                    System.err.println("An error occurred while processing the file: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Calculates byte, line, word, and character counts in a single pass for maximum efficiency.
     * This is the core of the performance improvement.
     *
     * @param path The path to the file to analyze.
     * @return A FileStats record containing all the calculated counts.
     * @throws IOException If an I/O error occurs.
     */
    private static FileStats calculateStats(Path path) throws IOException {
        long lineCount = 0;
        long wordCount = 0;
        long charCount = 0;
        // Getting byte size from file metadata is extremely fast and doesn't require reading the file.
        long byteCount = Files.size(path);

        boolean inWord = false;
        // Use a try-with-resources on a Reader to ensure it's closed automatically.
        // This reads the file character by character, which is very memory-efficient.
        try (Reader reader = Files.newBufferedReader(path)) {
            int ch;
            while ((ch = reader.read()) != -1) {
                charCount++;
                char character = (char) ch;

                // Standard `wc -l` counts newline characters.
                if (character == '\n') {
                    lineCount++;
                }

                // Check if the character is whitespace to determine word boundaries.
                if (Character.isWhitespace(character)) {
                    inWord = false;
                } else if (!inWord) {
                    wordCount++;
                    inWord = true;
                }
            }
        }

        return new FileStats(lineCount, wordCount, charCount, byteCount);
    }
}