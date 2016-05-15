package io;

import chat.RegisteredUser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repository of all the registered users.
 * 
 * @author Claudio Cusano
 */
public class UsersRegistry {

    String filename;
    
    /**
     * Create the repository object.
     * 
     * @param filename path to the file containing the data
     */
    public UsersRegistry(String filename) {
        this.filename = filename;
    }
    
    /**
     * Load the data from the file.
     * 
     * @return all the registered users
     */
    public Map<String, RegisteredUser> loadUSers() {
        ConcurrentHashMap<String, RegisteredUser> users = new ConcurrentHashMap<>();
        
        FileInputStream fis;
        BufferedReader br;
        try {
            fis = new FileInputStream(filename);
            br = new BufferedReader(new InputStreamReader(fis));
        } catch (IOException ex) {
            Logger.getLogger(UsersRegistry.class.getName()).log(Level.WARNING, null, ex);
            return users;
        }
 
        while (true) {
            String line = "";
            try {
                line = br.readLine();
            } catch (IOException ex) {
                Logger.getLogger(UsersRegistry.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (line == null)
                break;
            String[] data = line.split(",");
            if (data.length == 3) {
                char[] pwd = data[2].toCharArray();
                RegisteredUser u = new RegisteredUser(data[0], data[1], pwd);
                users.put(data[0], u);
            }
        }
        
        return users;
    }
    
    /**
     * Write the data about the users on the file.
     * 
     * @param users list of registered users
     */
    public void saveUsers(Map<String, RegisteredUser> users) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filename), "utf-8"))) {
            for (RegisteredUser u : users.values()) {
                String pwd = new String(u.getPassword());
                String line = u.getNickname() + "," + u.getEmail() + "," + pwd + "\n";
                writer.write(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(UsersRegistry.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
}
