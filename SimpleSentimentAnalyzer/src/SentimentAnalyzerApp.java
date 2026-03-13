/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author trist
 */
package com.sentiment;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;

public class SentimentAnalyzerApp extends JFrame {
    
    private JTextField filePathField;
    private JTextArea inputArea;
    private JLabel resultLabel;
    private JLabel statusLabel;
    private Map<String, Double> wordSentiments = new HashMap<>();
    private boolean modelLoaded = false;
    
    public SentimentAnalyzerApp() {
        setTitle("Simple NLP Sentiment Analyzer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 550);
        setLocationRelativeTo(null);
        
        // Create components
        JLabel titleLabel = new JLabel("Simple NLP Sentiment Analyzer", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JLabel fileLabel = new JLabel("Select NLP File:");
        filePathField = new JTextField(30);
        filePathField.setEditable(false);
        
        JButton browseButton = new JButton("Browse...");
        JButton loadButton = new JButton("Load NLP File");
        
        statusLabel = new JLabel("Status: Ready - Please select and LOAD an NLP file");
        statusLabel.setForeground(Color.BLUE);
        
        inputArea = new JTextArea(8, 40);
        inputArea.setText("Enter text to analyze here...");
        JScrollPane scrollPane = new JScrollPane(inputArea);
        
        JButton analyzeButton = new JButton("Analyze Sentiment");
        resultLabel = new JLabel("Result: ");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        
        // Status indicator
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel modelStatusLabel = new JLabel("Model Status: ");
        JLabel modelStatusValue = new JLabel("NOT LOADED");
        modelStatusValue.setForeground(Color.RED);
        modelStatusValue.setFont(new Font("Arial", Font.BOLD, 12));
        statusPanel.add(modelStatusLabel);
        statusPanel.add(modelStatusValue);
        
        // Set layout
        setLayout(new BorderLayout(10, 10));
        
        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filePanel.add(fileLabel);
        filePanel.add(filePathField);
        filePanel.add(browseButton);
        filePanel.add(loadButton);
        topPanel.add(filePanel, BorderLayout.CENTER);
        topPanel.add(statusPanel, BorderLayout.SOUTH);
        
        // Center panel
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(statusLabel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(analyzeButton);
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(resultLabel, BorderLayout.CENTER);
        bottomPanel.add(progressBar, BorderLayout.SOUTH);
        
        // Add all panels
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Button actions
        browseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select NLP Lexicon File");
            
            // Add file filter for common lexicon files
            chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt") 
                        || f.getName().toLowerCase().endsWith(".csv");
                }
                public String getDescription() {
                    return "Text Files (*.txt, *.csv)";
                }
            });
            
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                filePathField.setText(selectedFile.getAbsolutePath());
                statusLabel.setText("File selected: " + selectedFile.getName() + " - Click 'Load NLP File' to load it");
                statusLabel.setForeground(Color.BLUE);
            }
        });
        
        loadButton.addActionListener(e -> {
            if (filePathField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please browse and select an NLP file first!", 
                    "No File Selected", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            progressBar.setVisible(true);
            progressBar.setIndeterminate(true);
            statusLabel.setText("Loading NLP file... Please wait");
            statusLabel.setForeground(Color.BLUE);
            modelStatusValue.setText("LOADING...");
            modelStatusValue.setForeground(Color.ORANGE);
            
            SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
                @Override
                protected Void doInBackground() {
                    loadNLPFile(filePathField.getText());
                    return null;
                }
                
                @Override
                protected void done() {
                    progressBar.setVisible(false);
                    if (modelLoaded) {
                        statusLabel.setText("✅ Model loaded successfully! " + wordSentiments.size() + " words loaded");
                        statusLabel.setForeground(new Color(0, 150, 0));
                        modelStatusValue.setText("LOADED (" + wordSentiments.size() + " words)");
                        modelStatusValue.setForeground(new Color(0, 150, 0));
                        JOptionPane.showMessageDialog(SentimentAnalyzerApp.this, 
                            "✅ NLP file loaded successfully!\nLoaded " + wordSentiments.size() + " words/phrases.", 
                            "Load Successful", 
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        statusLabel.setText("❌ Failed to load model - check file format");
                        statusLabel.setForeground(Color.RED);
                        modelStatusValue.setText("NOT LOADED");
                        modelStatusValue.setForeground(Color.RED);
                    }
                }
            };
            worker.execute();
        });
        
        analyzeButton.addActionListener(e -> {
            if (!modelLoaded) {
                JOptionPane.showMessageDialog(this, 
                    "❌ Please load an NLP file first!\n\nSteps:\n1. Click 'Browse...' to select a file\n2. Click 'Load NLP File' to load it", 
                    "Model Not Loaded", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String text = inputArea.getText();
            if (text.equals("Enter text to analyze here...") || text.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter some text to analyze!");
                return;
            }
            
            double score = analyzeSentiment(text);
            String sentiment;
            Color color;
            
            if (score > 0.1) {
                sentiment = "POSITIVE";
                color = new Color(0, 150, 0);
            } else if (score < -0.1) {
                sentiment = "NEGATIVE";
                color = Color.RED;
            } else {
                sentiment = "NEUTRAL";
                color = Color.GRAY;
            }
            
            resultLabel.setText(String.format("Result: %s (score: %.2f)", sentiment, score));
            resultLabel.setForeground(color);
        });
    }
    
    private void loadNLPFile(String path) {
        wordSentiments.clear();
        try {
            File file = new File(path);
            System.out.println("Loading file: " + file.getAbsolutePath());
            System.out.println("File exists: " + file.exists());
            
            List<String> lines = Files.readAllLines(Paths.get(path));
            System.out.println("Total lines in file: " + lines.size());
            
            int loaded = 0;
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                    continue;
                }
                
                // Try different delimiters
                String[] parts = line.split("[,\\t;:]");
                if (parts.length >= 2) {
                    try {
                        String word = parts[0].trim().toLowerCase();
                        // Remove any quotes
                        word = word.replaceAll("^\"|\"$", "");
                        
                        double score = Double.parseDouble(parts[1].trim());
                        wordSentiments.put(word, score);
                        loaded++;
                        
                        // Print first few loaded words for debugging
                        if (loaded <= 5) {
                            System.out.println("Loaded: " + word + " = " + score);
                        }
                    } catch (NumberFormatException ex) {
                        System.out.println("Skipping line - invalid number: " + line);
                    }
                } else {
                    System.out.println("Skipping line - wrong format: " + line);
                }
            }
            
            System.out.println("Successfully loaded " + loaded + " words");
            
            if (loaded > 0) {
                modelLoaded = true;
            } else {
                modelLoaded = false;
                JOptionPane.showMessageDialog(this, 
                    "No valid word-score pairs found in file.\n" +
                    "File should have format: word,score per line", 
                    "Invalid File Format", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading file: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            modelLoaded = false;
        }
    }
    
    private double analyzeSentiment(String text) {
        String[] words = text.toLowerCase().split("\\s+");
        double total = 0;
        int count = 0;
        
        for (String word : words) {
            word = word.replaceAll("[^a-zA-Z]", "");
            if (wordSentiments.containsKey(word)) {
                total += wordSentiments.get(word);
                count++;
            }
        }
        
        return count == 0 ? 0 : total / count;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new SentimentAnalyzerApp().setVisible(true);
        });
    }
}