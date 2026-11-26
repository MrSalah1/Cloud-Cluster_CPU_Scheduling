import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimulatorGUI extends JFrame {
    private static final String[] COLUMNS = {"Process ID", "Arrival Time", "Burst Time", "Priority"};
    private static final String ALG_SJF = "SJF";
    private static final String ALG_ACS = "ACS";
    private static final String ALG_BOTH = "Both";
    private static final int DEFAULT_QUANTUM = 3;
    private static final int DEFAULT_CONTEXT_SWITCH = 0;
    private static final Color PANEL_BACKGROUND = new Color(0xF5F5F5);

    private final JTextField quantumField = new JTextField(String.valueOf(DEFAULT_QUANTUM), 6);
    private final JTextField contextSwitchField = new JTextField(String.valueOf(DEFAULT_CONTEXT_SWITCH), 6);
    private final JComboBox<String> algorithmCombo = new JComboBox<>(new String[]{ALG_SJF, ALG_ACS, ALG_BOTH});
    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0);
    private final JTable processTable = new JTable(tableModel);
    private final JTextArea outputArea = new JTextArea();

    public SimulatorGUI() {
        super("CPU Scheduling Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(820, 600));
        setSize(1000, 760);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(PANEL_BACKGROUND);
        mainPanel.add(buildConfigurationPanel(), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                buildProcessPanel(),
                buildOutputPanel());
        splitPane.setResizeWeight(0.42);
        splitPane.setBorder(null);
        splitPane.setBackground(PANEL_BACKGROUND);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
        addProcessRow();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new SimulatorGUI().setVisible(true));
    }

    private JPanel buildConfigurationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Simulation Configuration"));
        panel.setBackground(PANEL_BACKGROUND);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 6, 4, 6);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridy = 0;

        constraints.gridx = 0;
        panel.add(new JLabel("Quantum Time:"), constraints);
        constraints.gridx = 1;
        panel.add(quantumField, constraints);

        constraints.gridx = 2;
        panel.add(new JLabel("Context Switching Time:"), constraints);
        constraints.gridx = 3;
        panel.add(contextSwitchField, constraints);

        constraints.gridx = 4;
        panel.add(new JLabel("Algorithm:"), constraints);
        constraints.gridx = 5;
        panel.add(algorithmCombo, constraints);

        constraints.gridx = 6;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        panel.add(spacer, constraints);

        return panel;
    }

    private JPanel buildProcessPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Process Input"));
        panel.setBackground(PANEL_BACKGROUND);

        processTable.setRowHeight(24);
        processTable.setFillsViewportHeight(true);
        processTable.getTableHeader().setReorderingAllowed(false);
        panel.add(new JScrollPane(processTable), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        buttons.setOpaque(false);

        JButton addButton = new JButton("Add Process");
        JButton removeButton = new JButton("Remove Selected");
        JButton loadTest1Button = new JButton("Load Test Case 1");
        JButton loadTest2Button = new JButton("Load Test Case 2");

        addButton.addActionListener(event -> addProcessRow());
        removeButton.addActionListener(event -> removeSelectedRows());
        loadTest1Button.addActionListener(event -> loadTestCase(TestCase.testCase1()));
        loadTest2Button.addActionListener(event -> loadTestCase(TestCase.testCase2()));

        buttons.add(addButton);
        buttons.add(removeButton);
        buttons.add(loadTest1Button);
        buttons.add(loadTest2Button);
        panel.add(buttons, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Simulation Output"));
        panel.setBackground(PANEL_BACKGROUND);

        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setLineWrap(false);
        outputArea.setBackground(Color.WHITE);
        panel.add(new JScrollPane(
                outputArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        buttons.setOpaque(false);

        JButton runButton = new JButton("Run Simulation");
        JButton resetButton = new JButton("Reset");

        runButton.addActionListener(event -> runSimulation());
        resetButton.addActionListener(event -> resetSimulation());

        buttons.add(runButton);
        buttons.add(resetButton);
        panel.add(buttons, BorderLayout.NORTH);

        return panel;
    }

    private void addProcessRow() {
        if (!stopTableEditing()) {
            return;
        }
        tableModel.addRow(new Object[]{"", "", "", ""});
        int lastRow = tableModel.getRowCount() - 1;
        processTable.setRowSelectionInterval(lastRow, lastRow);
        processTable.scrollRectToVisible(processTable.getCellRect(lastRow, 0, true));
    }

    private void removeSelectedRows() {
        if (!stopTableEditing()) {
            return;
        }
        int[] selectedRows = processTable.getSelectedRows();
        if (selectedRows.length == 0) {
            showError("Select at least one process row to remove.");
            return;
        }
        for (int i = selectedRows.length - 1; i >= 0; i--) {
            tableModel.removeRow(selectedRows[i]);
        }
    }

    private void loadTestCase(TestCase testCase) {
        if (!stopTableEditing()) {
            return;
        }
        tableModel.setRowCount(0);
        for (Process process : testCase.createProcesses()) {
            tableModel.addRow(new Object[]{
                    process.getId(),
                    process.getArrivalTime(),
                    process.getBurstTime(),
                    process.getPriority()
            });
        }
        quantumField.setText(String.valueOf(testCase.getQuantum()));
        contextSwitchField.setText(String.valueOf(testCase.getContextSwitch()));
        outputArea.setText("");
    }

    private void runSimulation() {
        if (!stopTableEditing()) {
            return;
        }

        int quantum;
        int contextSwitch;
        TestCase testCase;
        try {
            quantum = parseConfigValue(quantumField.getText(), "Quantum Time", 1);
            contextSwitch = parseConfigValue(contextSwitchField.getText(), "Context Switching Time", 0);
            testCase = buildTestCase(quantum, contextSwitch);
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
            return;
        }

        String algorithm = (String) algorithmCombo.getSelectedItem();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        PrintStream capture = new PrintStream(
                createTeeStream(originalOut, buffer), true, StandardCharsets.UTF_8);
        String errorMessage = null;

        try {
            System.setOut(capture);
            testCase.printHeader();
            testCase.printInputTable();

            if (ALG_SJF.equals(algorithm)) {
                Main.runSJF(testCase);
            } else if (ALG_ACS.equals(algorithm)) {
                Main.runACS(testCase);
            } else {
                List<Process> sjfProcesses = Main.runSJF(testCase);
                List<Process> acsProcesses = Main.runACS(testCase);
                Main.printSummary(sjfProcesses, acsProcesses);
            }
        } catch (RuntimeException exception) {
            errorMessage = "Simulation failed: " + exception.getMessage();
        } finally {
            System.out.flush();
            System.setOut(originalOut);
            capture.close();
        }

        outputArea.setText(buffer.toString(StandardCharsets.UTF_8));
        outputArea.setCaretPosition(0);

        if (errorMessage != null) {
            showError(errorMessage);
        }
    }

    private void resetSimulation() {
        if (!stopTableEditing()) {
            return;
        }
        outputArea.setText("");
        quantumField.setText(String.valueOf(DEFAULT_QUANTUM));
        contextSwitchField.setText(String.valueOf(DEFAULT_CONTEXT_SWITCH));
        algorithmCombo.setSelectedItem(ALG_BOTH);
    }

    private TestCase buildTestCase(int quantum, int contextSwitch) {
        List<TestCase.ProcessData> data = new ArrayList<>();
        Set<Integer> usedIds = new HashSet<>();

        for (int row = 0; row < tableModel.getRowCount(); row++) {
            int id = parseCell(row, 0, "Process ID");
            int arrival = parseCell(row, 1, "Arrival Time");
            int burst = parseCell(row, 2, "Burst Time");
            int priority = parseCell(row, 3, "Priority");

            if (arrival < 0) {
                throw new IllegalArgumentException("Row " + (row + 1) + ": Arrival Time must be 0 or greater.");
            }
            if (burst < 1) {
                throw new IllegalArgumentException("Row " + (row + 1) + ": Burst Time must be at least 1.");
            }
            if (!usedIds.add(id)) {
                throw new IllegalArgumentException("Row " + (row + 1) + ": Process ID " + id + " is duplicated.");
            }

            data.add(new TestCase.ProcessData(id, arrival, burst, priority));
        }

        if (data.isEmpty()) {
            throw new IllegalArgumentException("Add at least one process before running the simulation.");
        }

        return new TestCase("GUI Test Case", data, quantum, contextSwitch);
    }

    private int parseCell(int row, int column, String columnName) {
        Object value = tableModel.getValueAt(row, column);
        String text = value == null ? "" : value.toString().trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException("Row " + (row + 1) + ": " + columnName + " is required.");
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Row " + (row + 1) + ": " + columnName + " must be an integer.");
        }
    }

    private int parseConfigValue(String text, String fieldName, int minimum) {
        String trimmed = text == null ? "" : text.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        int value;
        try {
            value = Integer.parseInt(trimmed);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(fieldName + " must be an integer.");
        }
        if (value < minimum) {
            throw new IllegalArgumentException(fieldName + " must be at least " + minimum + ".");
        }
        return value;
    }

    private boolean stopTableEditing() {
        if (!processTable.isEditing()) {
            return true;
        }
        if (processTable.getCellEditor() != null && !processTable.getCellEditor().stopCellEditing()) {
            showError("Finish editing the current cell before continuing.");
            return false;
        }
        return true;
    }

    private OutputStream createTeeStream(PrintStream console, ByteArrayOutputStream buffer) {
        return new OutputStream() {
            @Override
            public void write(int value) throws IOException {
                console.write(value);
                buffer.write(value);
            }

            @Override
            public void write(byte[] bytes, int offset, int length) throws IOException {
                console.write(bytes, offset, length);
                buffer.write(bytes, offset, length);
            }

            @Override
            public void flush() throws IOException {
                console.flush();
                buffer.flush();
            }
        };
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Invalid Input", JOptionPane.ERROR_MESSAGE);
    }
}
