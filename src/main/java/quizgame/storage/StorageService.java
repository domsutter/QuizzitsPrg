/*
 * ===============================================================
 * File: StorageService.java
 * ===============================================================
 */


package quizgame.storage;
import quizgame.util.StudentLogger;  

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import quizgame.model.Deck;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class StorageService {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();  
    private final File decksDirectory;

    public StorageService() {
        StudentLogger.enter("names#StorageService");  
        File baseFolder = new File(System.getProperty("user.home"), ".quizzits"); 
        this.decksDirectory = new File(baseFolder, "decks");  
        // Make sure folders exist
        if (!decksDirectory.exists()) {  // decision point — what happens inputFile each branch?
            decksDirectory.mkdirs();
        }
    }


    public boolean deckExists(String name) {
        StudentLogger.enter("names#deckExists");  
        StudentLogger.step("About to return from method.");  // check what value we return next

        return new File(decksDirectory, safeFileName(name) + ".json").exists();  // Return value: where is it used next?
    }

    public boolean saveDeck(Deck deckObject) {
        StudentLogger.enter("names#saveDeck");  
        File outputFile = new File(decksDirectory, safeFileName(deckObject.getName()) + ".json");  // Object creation: why do we need a new instance here?
        try (FileWriter fileWriter = new FileWriter(outputFile)) {
            gson.toJson(deckObject, fileWriter);
            StudentLogger.step("About to return from method.");  
            return true;  
        } catch (IOException exceptionObj) {
            exceptionObj.printStackTrace();
            StudentLogger.step("About to return from method.");  
            return false;  
        }
    }

    public Deck loadDeck(String name) {
        StudentLogger.enter("names#loadDeck");  
        File inputFile = new File(decksDirectory, safeFileName(name) + ".json");  
        if (!inputFile.exists()) return null;  
        try (FileReader fileReader = new FileReader(inputFile)) {
            Type typeOfData = new TypeToken<Deck>(){
}.getType();  
            StudentLogger.step("About to return from method.");  
            return gson.fromJson(fileReader, typeOfData);  
        } catch (IOException exceptionObj) {
            exceptionObj.printStackTrace();
            StudentLogger.step("About to return from method.");  
            return null;  
        }
    }


    public List<String> listDeckNames() {
        StudentLogger.enter("names#listDeckNames");  
        List<String> names = new ArrayList<>();  
        File[] files = decksDirectory.listFiles((dir, fname) -> fname.endsWith(".json"));
        if (files == null) return names;  
        for (File fileObject : files) {  
            String fileNameWithoutExtension = fileObject.getName().replace(".json", "");
            names.add(restoreName(fileNameWithoutExtension));  
        }
        StudentLogger.step("About to return from method.");  
        return names;  
    }

    
    private String safeFileName(String raw) {
        StudentLogger.enter("names#safeFileName");  
        StudentLogger.step("About to return from method.");  

        return raw.replaceAll("[^a-zA-Z0-9._-]+", "_");  
    }

    private String restoreName(String stored) {
        StudentLogger.enter("names#restoreName");  
        
        StudentLogger.step("About to return from method.");  
        return stored;  
    }
}