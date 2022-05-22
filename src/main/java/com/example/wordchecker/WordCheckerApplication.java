package com.example.wordchecker;

import com.example.wordchecker.service.FetchData;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordCheckerApplication {

    static List<JTextField> letters = new ArrayList<>();

    static JTextField jKnownLetters;

    static JTextField jUnknownLetters;

    static JTable table;

    static DefaultTableModel dtm;

    private static JPanel addTextFields() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        for (int i = 0; i < 5; i++) {
            JFormattedTextField textField = new JFormattedTextField();
            textField.setColumns(2);
            textField.setDocument(new LimitDocument(1));
            controlPanel.add(textField);
            letters.add(textField);
        }
        return controlPanel;
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Угадать букву");
        frame.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx     = 0;
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        constraints.gridy      = 0;
        constraints.weighty    = 0.0;
        constraints.weightx    = 1.0;
        constraints.gridwidth  = GridBagConstraints.RELATIVE;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.PAGE_START;
        frame.add(addTextFields(), constraints);
        JLabel lKnownLetters = new JLabel("Известные буквы");
        constraints.gridy     = 1;
        frame.add(lKnownLetters, constraints);
        jKnownLetters = new JTextField();
        constraints.gridy     = 2;
        frame.add(jKnownLetters, constraints);
        JLabel lUnknownLetters = new JLabel("Неизвестные буквы");
        constraints.gridy     = 3;
        frame.add(lUnknownLetters, constraints);
        jUnknownLetters = new JTextField();
        constraints.gridy     = 4;
        frame.add(jUnknownLetters, constraints);
        JButton button = new JButton();
        button.setText("send");
        button.addActionListener(e -> {
            dtm.setRowCount(0);
            Map<Integer, String> letterPos = new HashMap<>();
            for (int i = 0; i < letters.size(); i++) {
                JTextField textField = letters.get(i);
                if (textField.getText().length() != 0) {
                    letterPos.put(i, textField.getText());
                }
            }
            List<String> strings = FetchData.fetchData(letterPos, jKnownLetters.getText(), jUnknownLetters.getText());
            for (String s : strings) {
                dtm.addRow(new Object[]{s});
            }
            table.setVisible(true);
        });
        constraints.gridy = 5;
        frame.add(button, constraints);

        dtm =  new DefaultTableModel(null, new String[]{"word"}) {
            @Override
            public Class<?> getColumnClass(int col) {
                return getValueAt(0, col).getClass();
            }
        };
        table = new JTable(dtm);
        JScrollPane sp = new JScrollPane(table);
        constraints.fill       = GridBagConstraints.BOTH;
        constraints.weighty    = 1;
        constraints.gridy      = 6;
        constraints.anchor     = GridBagConstraints.PAGE_END;
        constraints.gridheight = GridBagConstraints.REMAINDER;
        frame.add(sp, constraints);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(WordCheckerApplication::createAndShowGUI);


/*        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))){
            boolean inputKnownLetterPositions = true;
            Map<Integer, String> knownLetterPositions = new HashMap<>();
            while (inputKnownLetterPositions) {
                System.out.println("Input known letter (with position) or ENTER if unknown:");
                String s = reader.readLine();
                if (s.length() != 0) {
                    if (s.length() != 1) {
                        System.out.println("You must enter only one letter!");
                    } else {
                        System.out.println("Enter letter position:");
                        String positionString = reader.readLine();
                        try {
                            Integer position = Integer.parseInt(positionString);
                            knownLetterPositions.put(position, s);
                        } catch (NumberFormatException e) {
                            System.out.println("You must enter digit!");
                        }
                    }
                } else {
                    inputKnownLetterPositions = false;
                }
            }
            System.out.println("Input known letters:");
            String knownLetters = reader.readLine();
            System.out.println("Input unknown letters:");
            String unknownLetters = reader.readLine();
            List<String> words = FetchData.fetchData(knownLetterPositions, knownLetters, unknownLetters);
            words.forEach(System.out::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }
}
