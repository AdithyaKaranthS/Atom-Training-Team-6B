import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/** A refined, minimal Swing interface for the Bank Account transaction ledger. */
public class MainGUI extends JFrame {
    private static final Color NAVY = new Color(25, 47, 89);
    private static final Color BLUE = new Color(47, 101, 184);
    private static final Color LIGHT_BLUE = new Color(232, 240, 252);
    private static final Color BACKGROUND = new Color(246, 248, 252);
    private static final Color TEXT = new Color(35, 44, 61);
    private static final Color SUCCESS = new Color(24, 124, 75);
    private static final Color ERROR = new Color(190, 49, 49);
    private static final String DEPOSIT = "Deposit";
    private static final String WITHDRAW = "Withdraw";
    private static final String BALANCE = "Check Balance";
    private static final String STATEMENT = "Mini-Statement";
    private static final String UNDO = "Undo Last Transaction";

    private final BankAccount account = new BankAccount();
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final JLabel persistentBalanceLabel = new JLabel();
    private final JLabel balanceScreenLabel = new JLabel("", SwingConstants.CENTER);
    private final DefaultTableModel statementModel = new DefaultTableModel(
            new String[] {"Transaction ID", "Type", "Amount (Rs.)", "Balance After (Rs.)"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    public MainGUI() {
        super("Bank Account & Transaction Ledger");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(850, 540));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BACKGROUND);
        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createNavigation(), BorderLayout.WEST);
        root.add(createContentPanels(), BorderLayout.CENTER);
        setContentPane(root);

        updateBalanceDisplays();
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(NAVY);
        header.setBorder(new EmptyBorder(16, 26, 16, 26));

        JPanel titleArea = new JPanel();
        titleArea.setOpaque(false);
        titleArea.setLayout(new BoxLayout(titleArea, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("BANK ACCOUNT");
        title.setForeground(Color.WHITE);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        JLabel subtitle = new JLabel("Transaction Ledger");
        subtitle.setForeground(new Color(206, 220, 245));
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        titleArea.add(title); titleArea.add(Box.createVerticalStrut(3)); titleArea.add(subtitle);

        JPanel balanceArea = new JPanel();
        balanceArea.setOpaque(false);
        balanceArea.setLayout(new BoxLayout(balanceArea, BoxLayout.Y_AXIS));
        JLabel caption = new JLabel("AVAILABLE BALANCE", SwingConstants.RIGHT);
        caption.setAlignmentX(Component.RIGHT_ALIGNMENT);
        caption.setForeground(new Color(206, 220, 245));
        caption.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        persistentBalanceLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        persistentBalanceLabel.setForeground(Color.WHITE);
        persistentBalanceLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 23));
        balanceArea.add(caption); balanceArea.add(Box.createVerticalStrut(2)); balanceArea.add(persistentBalanceLabel);

        header.add(titleArea, BorderLayout.WEST);
        header.add(balanceArea, BorderLayout.EAST);
        return header;
    }

    private JPanel createNavigation() {
        JPanel navigation = new JPanel();
        navigation.setBackground(Color.WHITE);
        navigation.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 225, 235)));
        navigation.setPreferredSize(new Dimension(205, 0));
        navigation.setLayout(new BoxLayout(navigation, BoxLayout.Y_AXIS));

        JLabel menu = new JLabel("  ACTIONS");
        menu.setForeground(new Color(108, 119, 138));
        menu.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        menu.setBorder(new EmptyBorder(22, 14, 10, 0));
        navigation.add(menu);
        addNavigationButton(navigation, DEPOSIT);
        addNavigationButton(navigation, WITHDRAW);
        addNavigationButton(navigation, BALANCE);
        addNavigationButton(navigation, STATEMENT);
        addNavigationButton(navigation, UNDO);
        navigation.add(Box.createVerticalGlue());

        JLabel hint = new JLabel("Transactions are saved in order.");
        hint.setForeground(new Color(130, 139, 155));
        hint.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        hint.setBorder(new EmptyBorder(8, 15, 20, 8));
        navigation.add(hint);
        return navigation;
    }

    private void addNavigationButton(JPanel navigation, String cardName) {
        JButton button = new JButton(cardName);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        button.setBorder(new EmptyBorder(0, 18, 0, 8));
        button.setFocusPainted(false);
        button.setForeground(TEXT);
        button.setBackground(Color.WHITE);
        button.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        button.addActionListener(event -> showCard(cardName));
        navigation.add(button);
        navigation.add(Box.createVerticalStrut(3));
    }

    private JPanel createContentPanels() {
        contentPanel.setBackground(BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(26, 32, 28, 32));
        contentPanel.add(createTransactionPanel(DEPOSIT), DEPOSIT);
        contentPanel.add(createTransactionPanel(WITHDRAW), WITHDRAW);
        contentPanel.add(createBalancePanel(), BALANCE);
        contentPanel.add(createStatementPanel(), STATEMENT);
        contentPanel.add(createUndoPanel(), UNDO);
        return contentPanel;
    }

    private JPanel createTransactionPanel(String operation) {
        boolean isDeposit = DEPOSIT.equals(operation);
        JPanel card = cardPanel();
        card.setLayout(new GridBagLayout());
        GridBagConstraints c = constraints();
        JTextField amountField = styledTextField(16);
        JLabel message = messageLabel();

        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        card.add(titleLabel(operation + " Money"), c);
        c.gridy++; card.add(descriptionLabel(isDeposit
                ? "Add funds to your bank account." : "Enter the amount you would like to withdraw."), c);
        c.gridwidth = 1; c.gridy++; c.insets = new Insets(22, 18, 6, 8);
        card.add(new JLabel("Amount (Rs.)"), c);
        c.gridx = 1; c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1;
        card.add(amountField, c);
        c.gridx = 1; c.gridy++; c.insets = new Insets(14, 8, 8, 18); c.fill = GridBagConstraints.NONE; c.weightx = 0;
        JButton submit = primaryButton(operation);
        submit.addActionListener(event -> {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                if (isDeposit) account.deposit(amount); else account.withdraw(amount);
                showSuccess(message, operation + " successful: Rs. " + money(amount));
                amountField.setText(""); updateBalanceDisplays(); refreshStatement();
            } catch (NumberFormatException exception) {
                showError(message, "Enter a valid numeric amount.");
            } catch (IllegalArgumentException exception) {
                showError(message, exception.getMessage());
            }
        });
        card.add(submit, c);
        c.gridx = 0; c.gridy++; c.gridwidth = 2; c.insets = new Insets(4, 18, 18, 18);
        card.add(message, c);
        return card;
    }

    private JPanel createBalancePanel() {
        JPanel card = cardPanel();
        card.setLayout(new BorderLayout(0, 26));
        card.add(titleLabel("Account Balance"), BorderLayout.NORTH);
        balanceScreenLabel.setForeground(NAVY);
        balanceScreenLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 36));
        card.add(balanceScreenLabel, BorderLayout.CENTER);
        JLabel detail = new JLabel("This amount updates after every completed transaction.", SwingConstants.CENTER);
        detail.setForeground(new Color(101, 112, 130));
        card.add(detail, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createStatementPanel() {
        JPanel card = cardPanel();
        card.setLayout(new BorderLayout(0, 16));
        JPanel header = new JPanel();
        header.setOpaque(false); header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(titleLabel("Mini-Statement"));
        header.add(Box.createVerticalStrut(5));
        header.add(descriptionLabel("Review your most recent transactions, newest first."));
        card.add(header, BorderLayout.NORTH);

        JPanel centre = new JPanel(new BorderLayout(0, 12));
        centre.setOpaque(false);
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        controls.setOpaque(false);
        JTextField countField = styledTextField(5);
        JLabel message = messageLabel();
        JButton show = primaryButton("Show");
        show.addActionListener(event -> {
            try {
                int count = Integer.parseInt(countField.getText().trim());
                if (count <= 0) throw new NumberFormatException();
                refreshStatement(count);
                showSuccess(message, "Showing up to " + count + " most recent transaction(s).");
            } catch (NumberFormatException exception) {
                showError(message, "Enter a positive whole number.");
            }
        });
        controls.add(new JLabel("Number of transactions:")); controls.add(countField); controls.add(show); controls.add(message);
        centre.add(controls, BorderLayout.NORTH);
        JTable table = new JTable(statementModel);
        table.setRowHeight(28); table.setShowVerticalLines(false); table.setGridColor(new Color(230, 234, 241));
        table.getTableHeader().setBackground(LIGHT_BLUE); table.getTableHeader().setForeground(NAVY);
        table.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        DefaultTableCellRenderer right = new DefaultTableCellRenderer(); right.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(2).setCellRenderer(right);
        table.getColumnModel().getColumn(3).setCellRenderer(right);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(214, 221, 232)));
        centre.add(scrollPane, BorderLayout.CENTER);
        card.add(centre, BorderLayout.CENTER);
        return card;
    }

    private JPanel createUndoPanel() {
        JPanel card = cardPanel();
        card.setLayout(new GridBagLayout());
        GridBagConstraints c = constraints();
        JLabel message = messageLabel();
        c.gridx = 0; c.gridy = 0; card.add(titleLabel("Undo Last Transaction"), c);
        c.gridy++; card.add(descriptionLabel("Reverse only the most recently completed transaction."), c);
        c.gridy++; c.insets = new Insets(24, 18, 10, 18);
        JButton undoButton = primaryButton("Undo Transaction");
        undoButton.addActionListener(event -> {
            Transaction undone = account.undoLastTransaction();
            if (undone == null) showError(message, "No transactions to undo.");
            else {
                showSuccess(message, "Undid " + undone.getType() + " of Rs. " + money(undone.getAmount()) + ".");
                updateBalanceDisplays(); refreshStatement();
            }
        });
        card.add(undoButton, c);
        c.gridy++; c.insets = new Insets(4, 18, 18, 18); card.add(message, c);
        return card;
    }

    private JPanel cardPanel() {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 226, 236)), new EmptyBorder(22, 24, 22, 24)));
        return card;
    }

    private void showCard(String cardName) {
        if (STATEMENT.equals(cardName)) refreshStatement();
        cardLayout.show(contentPanel, cardName);
    }

    private void updateBalanceDisplays() {
        String text = "Rs. " + money(account.getBalance());
        persistentBalanceLabel.setText(text);
        balanceScreenLabel.setText(text);
    }

    private void refreshStatement() { refreshStatement(Integer.MAX_VALUE); }

    private void refreshStatement(int limit) {
        statementModel.setRowCount(0);
        List<Transaction> entries = new ArrayList<>(account.getLedger().values());
        for (int i = entries.size() - 1, shown = 0; i >= 0 && shown < limit; i--, shown++) {
            Transaction transaction = entries.get(i);
            statementModel.addRow(new Object[] {transaction.getId(), transaction.getType(),
                    "Rs. " + money(transaction.getAmount()),
                    "Rs. " + money(transaction.getBalanceAfterTransaction())});
        }
    }

    private static GridBagConstraints constraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(8, 18, 8, 8);
        return c;
    }

    private static JTextField styledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(178, 190, 210)),
                new EmptyBorder(6, 8, 6, 8)));
        return field;
    }

    private static JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE); button.setBackground(BLUE);
        button.setFocusPainted(false); button.setBorder(new EmptyBorder(8, 16, 8, 16));
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        return button;
    }

    private static JLabel titleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT); label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        return label;
    }

    private static JLabel descriptionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(101, 112, 130)); label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        return label;
    }

    private static JLabel messageLabel() {
        JLabel label = new JLabel(" ");
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        label.setForeground(ERROR);
        return label;
    }

    private static void showError(JLabel label, String message) { label.setForeground(ERROR); label.setText(message); }
    private static void showSuccess(JLabel label, String message) { label.setForeground(SUCCESS); label.setText(message); }
    private static String money(double amount) { return String.format("%.2f", amount); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainGUI().setVisible(true));
    }
}
