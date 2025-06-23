import org.jasypt.util.password.StrongPasswordEncryptor;

import java.io.*;
import java.util.Properties;
import java.util.Scanner;

public class Secrets {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.println("Do you want to read configuration (read), encrypt secret (encrypt) or verify hashed secret work (verify)?");
            switch(scan.nextLine()) {
                case "read":
                    readConfig();
                    break;
                case "encrypt":
                    System.out.println("Secret identifier: ");
                    String secretID = scan.nextLine();
                    System.out.println("Input secret: ");
                    String secret = scan.nextLine();
                    encrypt(secret, secretID);
                    break;
                case "verify":
                    System.out.println("Secret identifier: ");
                    String hashedValueName = scan.nextLine();
                    System.out.println("Input original secret string: ");
                    String input =  scan.nextLine();
                    System.out.println(verify(hashedValueName, input));
                    break;
                case "exit":
                    System.out.println("Exiting!");
                    System.exit(0);
                default:
                    System.out.println("Invalid option! Try again!");
                    break;
            }
        }
    }

    public static void readConfig() {
        try (FileInputStream fis = new FileInputStream("config.properties")) { //Retrieves config.properties file
            BufferedReader br = new BufferedReader(new InputStreamReader(fis)); //new InputStreamReader(fis) converts byte stream into char stream
            String line;
            while ((line = br.readLine()) != null) { //Loops through file and outputs every line
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void encrypt(String secret, String secretID) {
        try (FileInputStream fis = new FileInputStream("config.properties")) { //Retrieves config.properties
            Properties properties = new Properties();
            properties.load(fis);

            StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();

            String hashedSecret = passwordEncryptor.encryptPassword(secret); //Hashes password

            properties.setProperty(secretID, hashedSecret);

            try(FileOutputStream output = new FileOutputStream("config.properties")) {
                properties.store(output, null); //Store new values in config.properties
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Encrypted secret '" + secretID + "': " + hashedSecret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Boolean verify(String hashedValueId, String originalString) {
        StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream("config.properties")) { //Read from config.properties
            properties.load(fis);
            String encryptedString = properties.getProperty(hashedValueId); //Get hashed value

            boolean isPasswordValid = passwordEncryptor.checkPassword(originalString, encryptedString);
            return isPasswordValid;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
