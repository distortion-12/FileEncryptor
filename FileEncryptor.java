import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Scanner;

public class FileEncryptor {
    private static final String KEY_FILE = "Secret.key";

    public static void generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256, new SecureRandom());
        SecretKey secretKey = keyGen.generateKey();
        byte[] keyBytes = secretKey.getEncoded();
        try (FileOutputStream fos = new FileOutputStream(KEY_FILE)) {
            fos.write(keyBytes);
        }
    }

    public static SecretKey loadKey() throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(KEY_FILE).toPath());
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static void encryptFile(String filename, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        
        byte[] fileData = Files.readAllBytes(new File(filename).toPath());
        byte[] encryptedData = cipher.doFinal(fileData);
        
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(encryptedData);
        }
    }

    public static void decryptFile(String filename, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        
        byte[] encryptedData = Files.readAllBytes(new File(filename).toPath());
        byte[] decryptedData = cipher.doFinal(encryptedData);
        
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(decryptedData);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter 'E' to encrypt or 'D' to decrypt the file: ");
        String choice = scanner.nextLine().trim().toLowerCase();
        
        try {
            if (choice.equals("e")) {
                System.out.print("Enter the file name to encrypt: ");
                String filename = scanner.nextLine();
                File file = new File(filename);
                if (file.exists()) {
                    generateKey();
                    SecretKey key = loadKey();
                    encryptFile(filename, key);
                    System.out.println("File Encrypted Successfully!");
                } else {
                    System.out.println("File not found!");
                }
            } else if (choice.equals("d")) {
                System.out.print("Enter the file name to decrypt: ");
                String filename = scanner.nextLine();
                File file = new File(filename);
                if (file.exists()) {
                    SecretKey key = loadKey();
                    decryptFile(filename, key);
                    System.out.println("File Decrypted Successfully!");
                } else {
                    System.out.println("File not found!");
                }
            } else {
                System.out.println("Invalid choice. Use 'E' for encryption or 'D' for decryption.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}
