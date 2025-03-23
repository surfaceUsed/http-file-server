package org.example.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

/**
 * GUI class for handling the server's command interface. Provides an interface for the administrator to enter commands '
 * and view results.
 */
public class CommandWindow {

    private JFrame frame;
    private JTextArea textArea;
    private JPanel inputPanel;
    private JTextField textField;
    private JButton sendButton;
    private final Consumer<String> administrator; // Access object that handles the specific commands made by the administrator.

    /**
     * Constructs a new CommandWindow with the specified administrator.
     *
     * @param administrator A consumer that processes the commands entered by the administrator.
     */
    public CommandWindow(Consumer<String> administrator) {
        this.administrator = administrator;
    }

    /**
     * Initializes and displays the command window with all necessary components.
     */
    public void start() {
        initFrame();
        initTextArea();
        initPanel();
        initTextField();
        initButton();
        this.frame.add(this.inputPanel, BorderLayout.SOUTH);
        addListeners();
        this.frame.setVisible(true);
    }

    /**
     * Initializes the main frame for the command window.
     */
    private void initFrame() {
        this.frame = new JFrame("Admin window");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Should not exit on close, only when server is shut down.
        this.frame.setSize(850, 650);
        this.frame.setLayout(new BorderLayout());
        this.frame.setResizable(false);
    }

    /**
     * Initializes the text area for displaying messages.
     */
    private void initTextArea() {
        this.textArea = new JTextArea();
        this.textArea.setEditable(false);
        this.textArea.setLineWrap(true);
        this.textArea.setWrapStyleWord(true);
        this.textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        this.textArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.textArea.setText("Press '.help' for command menu.\n\n");
        this.frame.add(this.textArea, BorderLayout.CENTER);
        initScrollPane();
    }

    /**
     * Initializes the scroll pane for the text area.
     */
    private void initScrollPane() {
        JScrollPane scrollPane = new JScrollPane(this.textArea);
        this.frame.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Initializes the panel that holds the input field and send button.
     */
    private void initPanel() {
        this.inputPanel = new JPanel();
        this.inputPanel.setLayout(new BorderLayout());
        this.inputPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
    }

    /**
     * Initializes the text field for entering commands.
     */
    private void initTextField() {
        this.textField = new JTextField();
        this.textField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        this.inputPanel.add(textField, BorderLayout.CENTER);
    }

    /**
     * Initializes the send button that submits the command entered in the text field.
     */
    private void initButton() {
        this.sendButton = new JButton("Send");
        this.sendButton.setFont(new Font("Monospaced", Font.PLAIN, 14));
        this.sendButton.setFocusable(false);
        this.inputPanel.add(sendButton, BorderLayout.EAST);
    }

    /**
     * Adds action listeners to the send button and the text field.
     */
    private void addListeners() {
        this.sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textField.getText();
                if (!text.trim().isEmpty()) {
                    administrator.accept(text); // The text from the "textField" is sent to the accept()-method of the "administrator", where it's handled accordingly.
                    textField.setText("");
                }
            }
        });

        this.textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendButton.doClick();
            }
        });
    }

    /**
     * Appends a message to the text area, and scrolls to the bottom.
     *
     * @param message The message to append to the text area.
     */
    public void appendMessage(String message) {
        this.textArea.append(message);
        this.textArea.append("\n");
        this.textArea.setCaretPosition(this.textArea.getDocument().getLength()); // Sets the cursor to the bottom of the text area.
    }

    /**
     * Clears the content of the command window.
     */
    public void clearWindow() {
        this.textArea.setText("");
    }
}
