package edu.ucalgary.oop;

/**
 * Main class for the Disaster Management System.
 * Handles program initialization and command-line arguments.
 * 
 * @author Devante Kwizera
 * @version 1.0
 * @since 2025-04-13
 */
public class Main {
    private static final String DEFAULT_LANGUAGE = "en-CA";
    
    /**
     * Main entry point for the application.
     * 
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        try {
            // Parse command-line arguments
            String languageCode = parseLanguageCode(args);
            
            // Initialize and run the CLI
            CLI cli = new CLI(languageCode);
            cli.initialize();
            cli.run();
        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
            
            // Log the error
            try {
                DatabaseManager dbManager = new DatabaseManager();
                dbManager.logError("Fatal error in main", e);
            } catch (Exception logError) {
                System.err.println("Could not log error: " + logError.getMessage());
            }
            
            System.exit(1);
        }
    }
    
    /**
     * Parses command-line arguments to extract the language code.
     * 
     * @param args Command-line arguments
     * @return The language code to use
     */
    private static String parseLanguageCode(String[] args) {
        if (args.length > 0) {
            String languageArg = args[0];
            
            // Validate language code format
            if (LanguageManager.isValidLanguageCode(languageArg)) {
                if (LanguageManager.languageExists(languageArg)) {
                    return languageArg;
                } else {
                    System.out.println("Warning: Language file for '" + languageArg + 
                                     "' not found. Using default language (" + DEFAULT_LANGUAGE + ").");
                }
            } else {
                System.out.println("Warning: Invalid language code format '" + languageArg + 
                                 "'. Language code should be in format 'aa-BB'. Using default language (" + 
                                 DEFAULT_LANGUAGE + ").");
            }
        }
        
        // No language specified or invalid, use default
        return DEFAULT_LANGUAGE;
    }
}