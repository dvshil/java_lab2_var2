import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class FenwickTreeGUI {
    private FenwickTree fenwick;
    private int[] originalArray;
    private int[] initialArray;
    private JTextArea outputArea;
    private JTextField inputField;
    private JFrame frame;

    private final Color BACKGROUND_COLOR = new Color(214, 209, 207, 253);
    private final Color BORDER_COLOR = new Color(113, 99, 97);
    private final Color TEXT_COLOR = new Color(54, 47, 50);
    private final Color BUILD_BUTTON_COLOR = new Color(88, 97, 68);
    private final Color OTHER_BUTTONS_COLOR = new Color(93, 68, 89);

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        SwingUtilities.invokeLater(() -> new FenwickTreeGUI().createAndShowGUI());
    }

    private Color brighter(Color color, float factor) {
        int r = Math.min(255, (int)(color.getRed() + (255 - color.getRed()) * factor));
        int g = Math.min(255, (int)(color.getGreen() + (255 - color.getGreen()) * factor));
        int b = Math.min(255, (int)(color.getBlue() + (255 - color.getBlue()) * factor));
        return new Color(r, g, b);
    }

    private Color darker(Color color, float factor) {
        int r = (int)(color.getRed() * (1 - factor));
        int g = (int)(color.getGreen() * (1 - factor));
        int b = (int)(color.getBlue() * (1 - factor));
        return new Color(r, g, b);
    }

    private Color getContrastColor(Color color) {
        double luminance = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;
        return luminance > 0.6 ? Color.BLACK : Color.WHITE;
    }

    //сортировки
    private void bubbleSortAscending(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                }
            }
        }
    }

    private void bubbleSortDescending(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] < arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2d.setColor(getForeground());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), x, y);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(BORDER_COLOR);
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            }
        };

        button.setFont(new Font("Georgia", Font.BOLD, 14));
        button.setForeground(getContrastColor(color));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBackground(color);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(brighter(color, 0.15f));
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                button.repaint();
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
                button.repaint();
            }

            public void mousePressed(MouseEvent e) {
                button.setBackground(darker(color, 0.15f));
                button.repaint();
            }

            public void mouseReleased(MouseEvent e) {
                button.setBackground(brighter(color, 0.08f));
                button.repaint();
            }
        });

        button.setPreferredSize(new Dimension(230, 50));
        return button;
    }

    private void createAndShowGUI() {
        frame = new JFrame("Дерево Фенвика");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(15, 15));
        frame.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel inputPanel = createInputPanel();
        frame.add(inputPanel, BorderLayout.NORTH);

        outputArea = new JTextArea(20, 60);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Georgia", Font.PLAIN, 15));
        outputArea.setBackground(BACKGROUND_COLOR);
        outputArea.setForeground(TEXT_COLOR);
        outputArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 2, true),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(new TitledBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 2, true),
                "Результаты операций",
                TitledBorder.LEADING,
                TitledBorder.TOP,
                new Font("Georgia", Font.BOLD, 14),
                TEXT_COLOR
        ));
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);

        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setMinimumSize(new Dimension(850, 650));
        frame.setVisible(true);

        outputArea.append("ДЕРЕВО ФЕНВИКА\n\n");
        outputArea.append("Введите массив чисел и нажмите 'Построить дерево' чтобы начать работу.\n");
        outputArea.append("Пример: 1 2 3 4 5\n\n");
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 2, true),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JLabel titleLabel = new JLabel("Дерево Фенвика");
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_COLOR);
        panel.add(titleLabel);

        panel.add(Box.createHorizontalStrut(25));

        JLabel arrayLabel = new JLabel("Массив:");
        arrayLabel.setFont(new Font("Georgia", Font.BOLD, 14));
        arrayLabel.setForeground(TEXT_COLOR);
        panel.add(arrayLabel);

        inputField = new JTextField(25);
        inputField.setFont(new Font("Georgia", Font.PLAIN, 14));
        inputField.setForeground(TEXT_COLOR);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        inputField.setBackground(Color.WHITE);
        panel.add(inputField);

        JButton buildButton = createButton("Построить дерево", BUILD_BUTTON_COLOR);
        buildButton.addActionListener(_ -> buildTree());
        panel.add(buildButton);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 2, true),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        topPanel.setBackground(BACKGROUND_COLOR);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBackground(BACKGROUND_COLOR);

        JButton[] topButtons = new JButton[4];
        topButtons[0] = createButton("Сумма на отрезке", OTHER_BUTTONS_COLOR);
        topButtons[1] = createButton("Префиксная сумма", OTHER_BUTTONS_COLOR);
        topButtons[2] = createButton("Обновить значение", OTHER_BUTTONS_COLOR);
        topButtons[3] = createButton("Заменить значение", OTHER_BUTTONS_COLOR);

        JButton[] bottomButtons = new JButton[4];
        bottomButtons[0] = createButton("Сортировка по возрастанию", OTHER_BUTTONS_COLOR);
        bottomButtons[1] = createButton("Сортировка по убыванию", OTHER_BUTTONS_COLOR);
        bottomButtons[2] = createButton("Статистики", OTHER_BUTTONS_COLOR);
        bottomButtons[3] = createButton("Текущее состояние", OTHER_BUTTONS_COLOR);

        for (JButton button : topButtons) {
            topPanel.add(button);
        }

        for (JButton button : bottomButtons) {
            bottomPanel.add(button);
        }

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(bottomPanel, BorderLayout.CENTER);

        topButtons[0].addActionListener(_ -> showRangeSumDialog());
        topButtons[1].addActionListener(_ -> showPrefixSumDialog());
        topButtons[2].addActionListener(_ -> showUpdateDialog());
        topButtons[3].addActionListener(_ -> showReplaceDialog());

        bottomButtons[0].addActionListener(_ -> sortAscending());
        bottomButtons[1].addActionListener(_ -> sortDescending());
        bottomButtons[2].addActionListener(_ -> showStatistics());
        bottomButtons[3].addActionListener(_ -> showState());

        return mainPanel;
    }

    private String arrayToString(int[] array) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < array.length - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    private String[] splitString(String str, char delimiter) {
        int count = 1;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == delimiter) count++;
        }

        String[] result = new String[count];
        int start = 0;
        int index = 0;

        for (int i = 0; i <= str.length(); i++) {
            if (i == str.length() || str.charAt(i) == delimiter) {
                result[index] = str.substring(start, i);
                start = i + 1;
                index++;
            }
        }
        return result;
    }

    private int stringToInt(String str) {
        int result = 0;
        int sign = 1;
        int start = 0;

        if (str.charAt(0) == '-') {
            sign = -1;
            start = 1;
        }

        for (int i = start; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                throw new NumberFormatException("Неверное значение: " + str);
            }
            result = result * 10 + (c - '0');
        }
        return result * sign;
    }

    private int[] copyArray(int[] source) {
        int[] copy = new int[source.length];
        for (int i = 0; i < source.length; i++) copy[i] = source[i];
        return copy;
    }

    private int findMax() {
        int max = originalArray[0];
        for (int i = 1; i < originalArray.length; i++) {
            if (originalArray[i] > max) max = originalArray[i];
        }
        return max;
    }

    private int findMin() {
        int min = originalArray[0];
        for (int i = 1; i < originalArray.length; i++) {
            if (originalArray[i] < min) min = originalArray[i];
        }
        return min;
    }

    private String formatNumber(int number) {
        return String.format("%,d", number).replace(',', ' ');
    }

    private void showError(String message, String title) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private void buildTree() {
        try {
            String input = inputField.getText().trim();
            if (input.isEmpty()) {
                showError("Введите массив!", "Пустой ввод");
                return;
            }

            String[] parts = splitString(input, ' ');
            int[] array = new int[parts.length];

            for (int i = 0; i < parts.length; i++) {
                array[i] = stringToInt(parts[i]);
            }

            originalArray = copyArray(array);
            initialArray = copyArray(array);

            fenwick = new FenwickTree(array.length);
            fenwick.build(array);

            outputArea.append("Дерево успешно построено!\n");
            outputArea.append("Размер: " + originalArray.length + " элементов\n");
            outputArea.append("Массив: " + arrayToString(originalArray) + "\n\n");

        } catch (Exception e) {
            showError("Ошибка при построении дерева: " + e.getMessage(), "Ошибка ввода");
        }
    }

    private void showRangeSumDialog() {
        if (fenwick == null) {
            showError("Сначала постройте дерево!", "Дерево не построено");
            return;
        }

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel leftLabel = new JLabel("Левая граница:");
        leftLabel.setFont(new Font("Georgia", Font.PLAIN, 14));
        leftLabel.setForeground(TEXT_COLOR);
        JTextField leftField = new JTextField("0");
        leftField.setFont(new Font("Georgia", Font.PLAIN, 14));
        leftField.setForeground(TEXT_COLOR);
        JLabel rightLabel = new JLabel("Правая граница:");
        rightLabel.setFont(new Font("Georgia", Font.PLAIN, 14));
        rightLabel.setForeground(TEXT_COLOR);
        JTextField rightField = new JTextField(String.valueOf(originalArray.length - 1));
        rightField.setFont(new Font("Georgia", Font.PLAIN, 14));
        rightField.setForeground(TEXT_COLOR);

        panel.add(leftLabel);
        panel.add(leftField);
        panel.add(rightLabel);
        panel.add(rightField);

        int result = JOptionPane.showConfirmDialog(frame, panel,
                "Сумма на отрезке", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int left = Integer.parseInt(leftField.getText());
                int right = Integer.parseInt(rightField.getText());
                int sum = fenwick.rangeSum(left, right);

                outputArea.append("Сумма на отрезке [" + left + ", " + right + "] = " +
                        formatNumber(sum) + "\n\n");

            } catch (Exception e) {
                showError("Ошибка: " + e.getMessage(), "Неверные параметры");
            }
        }
    }

    private void showPrefixSumDialog() {
        if (fenwick == null) {
            showError("Сначала постройте дерево!", "Дерево не построено");
            return;
        }

        String indexStr = JOptionPane.showInputDialog(frame,
                "Введите индекс (0-" + (originalArray.length-1) + "):", "0");

        if (indexStr != null) {
            try {
                int index = Integer.parseInt(indexStr);
                int sum = fenwick.prefixSum(index);

                outputArea.append("Префиксная сумма до " + index + " элемента = " +
                        formatNumber(sum) + "\n\n");

            } catch (Exception e) {
                showError("Ошибка: " + e.getMessage(), "Неверный индекс");
            }
        }
    }

    private void showUpdateDialog() {
        if (fenwick == null) {
            showError("Сначала постройте дерево!", "Дерево не построено");
            return;
        }

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel indexLabel = new JLabel("Индекс:");
        indexLabel.setFont(new Font("Georgia", Font.PLAIN, 14));
        indexLabel.setForeground(TEXT_COLOR);
        JTextField indexField = new JTextField("0");
        indexField.setFont(new Font("Georgia", Font.PLAIN, 14));
        indexField.setForeground(TEXT_COLOR);
        JLabel deltaLabel = new JLabel("Дельта:");
        deltaLabel.setFont(new Font("Georgia", Font.PLAIN, 14));
        deltaLabel.setForeground(TEXT_COLOR);
        JTextField deltaField = new JTextField("1");
        deltaField.setFont(new Font("Georgia", Font.PLAIN, 14));
        deltaField.setForeground(TEXT_COLOR);

        panel.add(indexLabel);
        panel.add(indexField);
        panel.add(deltaLabel);
        panel.add(deltaField);

        int result = JOptionPane.showConfirmDialog(frame, panel,
                "Обновление элемента", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int index = Integer.parseInt(indexField.getText());
                int delta = Integer.parseInt(deltaField.getText());
                int oldVal = originalArray[index];
                fenwick.update(index, delta);
                originalArray[index] += delta;
                int newVal = originalArray[index];

                outputArea.append("Обновление элемента [" + index + "]: " +
                        oldVal + " -> " + newVal + " (дельта = " + delta + ")\n\n");

            } catch (Exception e) {
                showError("Ошибка: " + e.getMessage(), "Неверные параметры");
            }
        }
    }

    private void showReplaceDialog() {
        if (fenwick == null) {
            showError("Сначала постройте дерево!", "Дерево не построено");
            return;
        }

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel indexLabel = new JLabel("Индекс:");
        indexLabel.setFont(new Font("Georgia", Font.PLAIN, 14));
        indexLabel.setForeground(TEXT_COLOR);
        JTextField indexField = new JTextField("0");
        indexField.setFont(new Font("Georgia", Font.PLAIN, 14));
        indexField.setForeground(TEXT_COLOR);
        JLabel valueLabel = new JLabel("Новое значение:");
        valueLabel.setFont(new Font("Georgia", Font.PLAIN, 14));
        valueLabel.setForeground(TEXT_COLOR);
        JTextField valueField = new JTextField("0");
        valueField.setFont(new Font("Georgia", Font.PLAIN, 14));
        valueField.setForeground(TEXT_COLOR);

        panel.add(indexLabel);
        panel.add(indexField);
        panel.add(valueLabel);
        panel.add(valueField);

        int result = JOptionPane.showConfirmDialog(frame, panel,
                "Замена элемента", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int index = Integer.parseInt(indexField.getText());
                int value = Integer.parseInt(valueField.getText());
                int oldVal = originalArray[index];
                fenwick.set(index, value);
                originalArray[index] = value;

                outputArea.append("Замена элемента [" + index + "]: " +
                        oldVal + " -> " + value + "\n\n");

            } catch (Exception e) {
                showError("Ошибка: " + e.getMessage(), "Неверные параметры");
            }
        }
    }

    private void sortAscending() {
        if (fenwick == null) {
            showError("Сначала постройте дерево!", "Дерево не построено");
            return;
        }

        int[] sortedArr = originalArray.clone();
        bubbleSortAscending(sortedArr);

        // Перестраиваем дерево с отсортированным массивом
        fenwick = new FenwickTree(sortedArr.length);
        fenwick.build(sortedArr);
        originalArray = sortedArr;

        outputArea.append("Массив отсортирован по возрастанию\n");
        outputArea.append("Текущий массив: " + arrayToString(originalArray) + "\n\n");
    }

    private void sortDescending() {
        if (fenwick == null) {
            showError("Сначала постройте дерево!", "Дерево не построено");
            return;
        }

        int[] sortedArr = originalArray.clone();
        bubbleSortDescending(sortedArr);

        // Перестраиваем дерево с отсортированным массивом
        fenwick = new FenwickTree(sortedArr.length);
        fenwick.build(sortedArr);
        originalArray = sortedArr;

        outputArea.append("Массив отсортирован по убыванию\n");
        outputArea.append("Текущий массив: " + arrayToString(originalArray) + "\n\n");
    }

    private void showStatistics() {
        if (fenwick == null) {
            showError("Сначала постройте дерево!", "Дерево не построено");
            return;
        }

        if (originalArray.length == 0) {
            outputArea.append("Массив пуст\n\n");
            return;
        }

        int max = findMax();
        int min = findMin();
        int sum = fenwick.totalSum();
        double avg = (double) sum / originalArray.length;

        outputArea.append("СТАТИСТИКА МАССИВА:\n");
        outputArea.append("   Первоначальный массив: " + arrayToString(initialArray) + "\n");
        outputArea.append("   Текущий массив: " + arrayToString(originalArray) + "\n");
        outputArea.append("   Сумма: " + formatNumber(sum) + "\n");
        outputArea.append("   Среднее: " + String.format("%.2f", avg) + "\n");
        outputArea.append("   Максимум: " + max + "\n");
        outputArea.append("   Минимум: " + min + "\n\n");
    }

    private void showState() {
        if (fenwick == null) {
            showError("Сначала постройте дерево!", "Дерево не построено");
            return;
        }

        outputArea.append("ТЕКУЩЕЕ СОСТОЯНИЕ:\n");
        outputArea.append("   Массив: " + arrayToString(originalArray) + "\n");
        outputArea.append("   Дерево Фенвика:  " + fenwick.treeToString() + "\n");
        outputArea.append("   Размер дерева: " + fenwick.size() + " элементов\n\n");
    }
}