import javax.crypto.SecretKey;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.io.File;
import java.util.List;

@SuppressWarnings("unchecked")
public class FileEncryptorUI extends JFrame {
    private JButton encryptButton, decryptButton;
    private JTextField filePathField;
    private JFileChooser fileChooser;
    private File selectedFile;

    public FileEncryptorUI() {
        setTitle("File Encryptor");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // File Selection Panel
        JPanel filePanel = new JPanel();
        filePanel.setLayout(new BorderLayout());
        filePathField = new JTextField(20);
        JButton browseButton = new JButton("Browse");
        filePanel.add(filePathField, BorderLayout.CENTER);
        filePanel.add(browseButton, BorderLayout.EAST);

        // Drag & Drop Panel
        JPanel dropPanel = new JPanel();
        dropPanel.setBorder(BorderFactory.createTitledBorder("Drag & Drop Files Here"));
        dropPanel.setPreferredSize(new Dimension(480, 100));
        dropPanel.setBackground(Color.LIGHT_GRAY);
        dropPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 30));
        JLabel dropLabel = new JLabel("Drop your file here");
        dropPanel.add(dropLabel);
        
        new DropTarget(dropPanel, new DropTargetListener() {
            @Override
            public void dragEnter(DropTargetDragEvent dtde) {}
            @Override
            public void dragOver(DropTargetDragEvent dtde) {}
            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {}
            @Override
            public void dragExit(DropTargetEvent dte) {}
            
            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if (!droppedFiles.isEmpty()) {
                        selectedFile = droppedFiles.get(0);
                        filePathField.setText(selectedFile.getAbsolutePath());
                        dropLabel.setText("File Selected: " + selectedFile.getName());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        encryptButton = new JButton("Encrypt");
        decryptButton = new JButton("Decrypt");
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);

        add(filePanel, BorderLayout.NORTH);
        add(dropPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        fileChooser = new JFileChooser();

        // Browse File
        browseButton.addActionListener(e -> {
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                filePathField.setText(selectedFile.getAbsolutePath());
                dropLabel.setText("File Selected: " + selectedFile.getName());
            }
        });

        // Encrypt Button Action
        encryptButton.addActionListener(e -> processFile(true));

        // Decrypt Button Action
        decryptButton.addActionListener(e -> processFile(false));

        setVisible(true);
    }

    private void processFile(boolean isEncrypt) {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Please select a file first!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            SecretKey key;
            if (isEncrypt) {
                FileEncryptor.generateKey();
            }
            key = FileEncryptor.loadKey();
            if (isEncrypt) {
                FileEncryptor.encryptFile(selectedFile.getAbsolutePath(), key);
                JOptionPane.showMessageDialog(this, "File Encrypted Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                FileEncryptor.decryptFile(selectedFile.getAbsolutePath(), key);
                JOptionPane.showMessageDialog(this, "File Decrypted Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FileEncryptorUI::new);
    }
}
