package net.delirium;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class App {
    private final String TITLE = "YT Utils";

    public App() {
        JFrame f = new JFrame(TITLE);

        JPanel handlesEntryPanel = new JPanel();
        JLabel handlesEntryLabel = new JLabel("Enter channel handle: ");
        JTextField handlesEntry = new JTextField(15);
        JButton handlesAddButton = new JButton("Add");
        JButton handlesRemoveButton = new JButton("Remove");
        handlesEntryPanel.add(handlesEntryLabel);
        handlesEntryPanel.add(handlesEntry);
        handlesEntryPanel.add(handlesAddButton);
        handlesEntryPanel.add(handlesRemoveButton);

        // Adding handles
        handlesAddButton.addActionListener((_) -> {
                String h = handlesEntry.getText();
                if (!h.isBlank()) {
                    try {
                        Channels.addHandle(h);
                        Channels.getAllHandles().forEach(IO::println);
                    } catch (SQLException _) {}
                }
        });

        // Removing handles
        handlesRemoveButton.addActionListener((_) -> {
                String h = handlesEntry.getText();
                if (!h.isBlank()) {
                    try {
                        Channels.removeHandle(h);
                        Channels.getAllHandles().forEach(IO::println);
                    } catch (Exception _) {}
                }
        });

        f.add(handlesEntryPanel);


        f.setSize(500,500);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}
