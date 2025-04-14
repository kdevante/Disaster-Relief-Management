package edu.ucalgary.oop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages language settings and text translation for the application.
 * Supports loading language files in XML format.
 * Uses only basic Java I/O for parsing.
 * 
 * @author Devante Kwizera
 * @version 1.0
 * @since 2025-04-13
 */
public class LanguageManager {
    private static final String DEFAULT_LANGUAGE = "en-CA";
    private static final String LANGUAGE_DIR = "data";
    
    private Map<String, String> translations;
    private String currentLanguage;
    
    /**
     * Constructor that attempts to load the specified language.
     * Falls back to default if the specified language is not available.
     * 
     * @param languageCode The language code to load (e.g., "en-CA", "fr-CA")
     */
    public LanguageManager(String languageCode) {
        translations = new HashMap<>();
        
        if (languageCode == null || !isValidLanguageCode(languageCode) || !loadLanguage(languageCode)) {
            System.out.println("Warning: Could not load language '" + languageCode + 
                            "'. Falling back to default language '" + DEFAULT_LANGUAGE + "'.");
            loadLanguage(DEFAULT_LANGUAGE);
        }
    }
    
    /**
     * Loads a language file.
     * 
     * @param languageCode The language code to load
     * @return true if successful, false otherwise
     */
    public boolean loadLanguage(String languageCode) {
        String filename = LANGUAGE_DIR + File.separator + languageCode + ".xml";
        File languageFile = new File(filename);
        
        if (!languageFile.exists()) {
            return false;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(languageFile))) {
            // Clear previous translations
            translations.clear();
            
            String line;
            String key = null;
            String value = null;
            boolean inTranslation = false;
            boolean inKey = false;
            boolean inValue = false;
            StringBuilder buffer = new StringBuilder();
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Check for start of translation
                if (line.contains("<translation>")) {
                    inTranslation = true;
                    key = null;
                    value = null;
                    continue;
                }
                
                // Check for end of translation
                if (line.contains("</translation>")) {
                    if (key != null && value != null) {
                        translations.put(key, value);
                    }
                    inTranslation = false;
                    continue;
                }
                
                // Only process lines within a translation tag
                if (!inTranslation) {
                    continue;
                }
                
                // Check for key tags
                if (line.contains("<key>")) {
                    inKey = true;
                    buffer.setLength(0); // Clear the buffer
                    
                    // Extract key if on same line
                    int startIdx = line.indexOf("<key>") + 5;
                    int endIdx = line.indexOf("</key>");
                    if (endIdx > startIdx) {
                        key = line.substring(startIdx, endIdx).trim();
                        inKey = false;
                    } else {
                        // Key spans multiple lines or is on next line
                        buffer.append(line.substring(startIdx));
                    }
                    
                    continue;
                }
                
                // Check for end of key
                if (line.contains("</key>")) {
                    int endIdx = line.indexOf("</key>");
                    if (endIdx > 0) {
                        buffer.append(" ").append(line.substring(0, endIdx).trim());
                    }
                    key = buffer.toString().trim();
                    inKey = false;
                    continue;
                }
                
                // Check for value tags
                if (line.contains("<value>")) {
                    inValue = true;
                    buffer.setLength(0); // Clear the buffer
                    
                    // Extract value if on same line
                    int startIdx = line.indexOf("<value>") + 7;
                    int endIdx = line.indexOf("</value>");
                    if (endIdx > startIdx) {
                        value = line.substring(startIdx, endIdx).trim();
                        inValue = false;
                    } else {
                        // Value spans multiple lines or is on next line
                        buffer.append(line.substring(startIdx));
                    }
                    
                    continue;
                }
                
                // Check for end of value
                if (line.contains("</value>")) {
                    int endIdx = line.indexOf("</value>");
                    if (endIdx > 0) {
                        buffer.append(" ").append(line.substring(0, endIdx).trim());
                    }
                    value = buffer.toString().trim();
                    inValue = false;
                    continue;
                }
                
                // Add content to buffer if inside a tag
                if (inKey) {
                    buffer.append(" ").append(line.trim());
                } else if (inValue) {
                    buffer.append(" ").append(line.trim());
                }
            }
            
            currentLanguage = languageCode;
            return !translations.isEmpty();
        } catch (IOException e) {
            System.err.println("Error loading language file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets a translated string for the given key.
     * 
     * @param key The translation key
     * @return The translated string, or the key itself if not found
     */
    public String getString(String key) {
        return translations.getOrDefault(key, key);
    }
    
    /**
     * Gets a translated string with format arguments.
     * 
     * @param key The translation key
     * @param args Arguments for string formatting
     * @return The formatted, translated string
     */
    public String getString(String key, Object... args) {
        String template = getString(key);
        try {
            return String.format(template, args);
        } catch (Exception e) {
            // If format fails, return the unformatted template
            return template;
        }
    }
    
    /**
     * Gets the current language code.
     * 
     * @return The current language code
     */
    public String getCurrentLanguage() {
        return currentLanguage;
    }
    
    /**
     * Checks if a language file exists.
     * 
     * @param languageCode The language code to check
     * @return true if the language file exists, false otherwise
     */
    public static boolean languageExists(String languageCode) {
        if (!isValidLanguageCode(languageCode)) {
            return false;
        }
        
        String filename = LANGUAGE_DIR + File.separator + languageCode + ".xml";
        File languageFile = new File(filename);
        return languageFile.exists();
    }
    
    /**
     * Validates a language code format (aa-BB).
     * 
     * @param code The language code to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidLanguageCode(String code) {
        return code != null && code.matches("[a-z]{2}-[A-Z]{2}");
    }
    
    /**
     * Gets all available language codes.
     * 
     * @return Array of available language codes
     */
    public static String[] getAvailableLanguages() {
        File dir = new File(LANGUAGE_DIR);
        File[] files = dir.listFiles((d, name) -> name.matches("[a-z]{2}-[A-Z]{2}\\.xml"));
        
        if (files == null) {
            return new String[0];
        }
        
        String[] languages = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            String filename = files[i].getName();
            languages[i] = filename.substring(0, filename.length() - 4); // Remove .xml
        }
        
        return languages;
    }
}