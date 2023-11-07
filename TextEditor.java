package MyTextEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEditor extends JFrame {
    private JPanel topPanel;
    private JPanel bottomPanel;
    private JTextArea textArea;
    private JTextField searchField;
    private JCheckBox useRegex;
    private JFileChooser jFileChooser;
    private JCheckBoxMenuItem searchRegex;
    private List<SearchMatchIndex> startIndices;
    private int currentSearchIndex;

    private final ActionListener exitEditor = actionEvent -> System.exit(0);

    private final ActionListener searchMatch = actionEvent -> new SearchWorker().execute();

    private final ActionListener findNextMatch = actionEvent -> getMatch(Match.NEXT);

    private final ActionListener findPrevMatch = actionEvent -> getMatch(Match.PREV);

    private final ActionListener setRegex = actionEvent -> this.searchRegex.setState(this.useRegex.isSelected());

    private final ActionListener setRegexMenu = actionEvent -> this.useRegex.setSelected(this.searchRegex.isSelected());

    private final ActionListener saveFileAction = actionEvent -> {
        if (this.jFileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                String selectedFileAbsPath = this.jFileChooser.getSelectedFile().getAbsolutePath();
                FileWriter fw = new FileWriter(selectedFileAbsPath);
                BufferedWriter bw = new BufferedWriter(fw);
                String txt = this.textArea.getText();
                System.out.println(txt);
                bw.write(txt);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    };

    private final ActionListener loadFileAction = actionEvent -> {
        if (this.jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                Path selectedFileAbsPath = Path.of(this.jFileChooser.getSelectedFile().getAbsolutePath());
                this.textArea.setText(Files.readString(selectedFileAbsPath));
            } catch (IOException e) {
                this.textArea.setText("");
                System.out.println(e.getMessage());
            }
        }
    };

    public TextEditor() {
        initPanels();
        initFileChooser();
        initTextField();
        initButtons();
        initMenu();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 1000);
        setVisible(true);
        setTitle("My Text Editor");
    }

    private void initPanels() {
        this.topPanel = new JPanel();
        this.bottomPanel = new JPanel();
    }

    private void initMenu() {
        JMenuItem loadFile = new JMenuItem("Open");
        loadFile.setName("Load File");
        loadFile.addActionListener(this.loadFileAction);

        JMenuItem saveFile = new JMenuItem("Save");
        saveFile.setName("Save File");
        saveFile.addActionListener(this.saveFileAction);

        JMenuItem exitMenu = new JMenuItem("Exit");
        exitMenu.setName("Exit Menu");
        exitMenu.addActionListener(this.exitEditor);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setName("File Menu");
        fileMenu.add(loadFile);
        fileMenu.add(saveFile);
        fileMenu.add(exitMenu);
        fileMenu.addSeparator();

        JMenuItem searchMatch = new JMenuItem("Search Match");
        searchMatch.setName("Search Match");
        searchMatch.addActionListener(this.searchMatch);

        JMenuItem nextMatch = new JMenuItem("Next Match");
        nextMatch.setName("Search Next Match");
        nextMatch.addActionListener(this.findNextMatch);

        JMenuItem previousMatch = new JMenuItem("Previous Match");
        previousMatch.setName("Search Previous Match");
        previousMatch.addActionListener(this.findPrevMatch);

        this.searchRegex = new JCheckBoxMenuItem("Use Regex in Search");
        this.searchRegex.setName("Regex Search");
        this.searchRegex.addActionListener(this.setRegexMenu);

        JMenu searchMenu = new JMenu("Search");
        searchMenu.setName("Search Menu");
        searchMenu.add(searchMatch);
        searchMenu.add(previousMatch);
        searchMenu.add(nextMatch);
        searchMenu.add(this.searchRegex);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(searchMenu);

        this.setJMenuBar(menuBar);
    }

    private void initButtons() {
        initSearchButtons();
        initFileButtons();
    }

    private void initFileButtons() {
        Image saveImg = new ImageIcon("MyTextEditor\\icons\\save.png")
                .getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        JButton saveButton = new JButton(new ImageIcon(saveImg));
        saveButton.setName("Save");
        saveButton.addActionListener(this.saveFileAction);

        Image loadImg = new ImageIcon("MyTextEditor\\icons\\load.png")
                .getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        JButton loadButton = new JButton(new ImageIcon(loadImg));
        loadButton.setName("Load");
        loadButton.addActionListener(this.loadFileAction);

        this.topPanel.add(saveButton);
        this.topPanel.add(loadButton);

        this.add(this.topPanel, BorderLayout.NORTH);
    }

    private void initSearchButtons() {
        Image startSearchImg = new ImageIcon("MyTextEditor\\icons\\search.png")
                .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        JButton startSearchButton = new JButton(new ImageIcon(startSearchImg));
        startSearchButton.setName("Start Search Button");
        startSearchButton.addActionListener(this.searchMatch);

        Image nextMatchImg = new ImageIcon("MyTextEditor\\icons\\next.png")
                .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        JButton nextMatchButton = new JButton(new ImageIcon(nextMatchImg));
        nextMatchButton.setName("Next Match Button");
        nextMatchButton.addActionListener(this.findNextMatch);

        Image prevMatchImg = new ImageIcon("MyTextEditor\\icons\\previous.png")
                .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        JButton prevMatchButton = new JButton(new ImageIcon(prevMatchImg));
        prevMatchButton.setName("Previous Match Button");
        prevMatchButton.addActionListener(this.findPrevMatch);

        this.searchField = new JTextField(30);
        this.searchField.setName("SearchField");

        this.useRegex = new JCheckBox("Use Regex");
        this.useRegex.setName("Use Regex Check Box");
        this.useRegex.addActionListener(this.setRegex);

        this.bottomPanel.add(this.searchField);
        this.bottomPanel.add(startSearchButton);
        this.bottomPanel.add(this.useRegex);
        this.bottomPanel.add(prevMatchButton);
        this.bottomPanel.add(nextMatchButton);

        this.add(this.bottomPanel, BorderLayout.SOUTH);
    }

    private void initTextField() {
        this.textArea = new JTextArea();
        this.textArea.setName("Text Area");
        this.textArea.setSize(1000, 1000);

        JScrollPane scrollPane = new JScrollPane(this.textArea);
        scrollPane.setName("Scroll Pane");
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        this.add(scrollPane, BorderLayout.CENTER);
    }

    private void initFileChooser() {
        this.jFileChooser = new JFileChooser();
        this.jFileChooser.setName("FileChooser");
        this.add(this.jFileChooser);
    }

    private synchronized List<SearchMatchIndex> getStartIndices() {
        return this.startIndices;
    }

    private synchronized void setStartIndices(List<SearchMatchIndex> startIndices) {
        this.startIndices = startIndices;
        this.currentSearchIndex = 0;
        highlightCurrentMatch();
    }

    private synchronized void highlightCurrentMatch() {
        if (getStartIndices() != null && !getStartIndices().isEmpty()) {
            SearchMatchIndex indices = getStartIndices().get(this.currentSearchIndex);
            this.textArea.setCaretPosition(indices.end());
            this.textArea.select(indices.start(), indices.end());
            this.textArea.grabFocus();
        }
    }

    private synchronized void getMatch(Match match) {
        if (this.startIndices != null && !this.startIndices.isEmpty()) {
            switch (match) {
                case NEXT -> this.currentSearchIndex =
                        Math.floorMod(++this.currentSearchIndex, this.startIndices.size());
                case PREV -> this.currentSearchIndex =
                        Math.floorMod(--this.currentSearchIndex, this.startIndices.size());
            }
            highlightCurrentMatch();
        }
    }

    private class SearchWorker extends SwingWorker<List<SearchMatchIndex>, Object> {
        @Override
        protected void done() {
            try {
                TextEditor.this.setStartIndices(get());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        @Override
        protected List<SearchMatchIndex> doInBackground() {
            boolean useRegex = TextEditor.this.useRegex.isSelected();
            Matcher matcher;

            String searchFieldText = TextEditor.this.searchField.getText();
            if (useRegex)
                matcher = Pattern.compile(searchFieldText).matcher(TextEditor.this.textArea.getText());
            else
                matcher = Pattern.compile(Pattern.quote(searchFieldText)).matcher(TextEditor.this.textArea.getText());

            ArrayList<SearchMatchIndex> indexList = new ArrayList<>();
            while (matcher.find())
                indexList.add(new SearchMatchIndex(matcher.start(), matcher.end()));

            return indexList;
        }
    }

    private enum Match {
        NEXT, PREV
    }

    private record SearchMatchIndex(int start, int end) {}
}
