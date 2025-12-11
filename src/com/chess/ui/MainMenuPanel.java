package com.chess.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.border.EmptyBorder;
import com.chess.engine.ComputerPlayer;

public class MainMenuPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = new Color(40, 40, 40);
    private static final Color BUTTON_COLOR = new Color(70, 70, 70);
    private static final Color HOVER_COLOR = new Color(90, 90, 90);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Font TITLE_FONT = new Font("Serif", Font.BOLD, 48);
    private static final Font SUBTITLE_FONT = new Font("SansSerif", Font.PLAIN, 24);
    private static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 20);

    private ChessFrame frame;
    private JPanel contentPanel; // Panel chứa các nút để thay đổi nội dung

    public MainMenuPanel(ChessFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(BACKGROUND_COLOR);
        titlePanel.setBorder(new EmptyBorder(50, 0, 30, 0));

        JLabel titleLabel = new JLabel("CHESS");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        add(contentPanel, BorderLayout.CENTER);

        showMainMenu();
    }

    private void showMainMenu() {
        contentPanel.removeAll();

        JButton btnHuman = createStyledButton("Play vs Human");
        JButton btnComputer = createStyledButton("Play vs Computer");
        JButton btnExit = createStyledButton("Exit");

        // Action: Play vs Human
        btnHuman.addActionListener(e -> frame.showGameSetup(false, 0, 0));

        // Action: Play vs Computer -> Chuyển sang màn hình chọn Bot
        btnComputer.addActionListener(e -> showBotSelection());

        // Action: Exit
        btnExit.addActionListener(e -> System.exit(0));

        addButtonToPanel(btnHuman);
        addButtonToPanel(btnComputer);
        addButtonToPanel(btnExit);

        refreshPanel();
    }

    private void showBotSelection() {
        contentPanel.removeAll();

        JLabel lblSelect = new JLabel("Select Engine");
        lblSelect.setFont(SUBTITLE_FONT);
        lblSelect.setForeground(Color.GRAY);
        lblSelect.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(lblSelect);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton btnStockfish = createStyledButton("Stockfish (Pro)");
        JButton btnDFS = createStyledButton("DFS Bot (Java)");
        JButton btnBack = createStyledButton("Back");

        // Action: Stockfish
        btnStockfish.addActionListener(e -> {
            showDifficultyDialog(ComputerPlayer.TYPE_STOCKFISH, "Stockfish Difficulty");
        });

        // Action: DFS Bot
        btnDFS.addActionListener(e -> {
            showDifficultyDialog(ComputerPlayer.TYPE_JAVA_BOT, "DFS Bot Difficulty");
        });

        // Action: Back -> Quay lại menu chính
        btnBack.addActionListener(e -> showMainMenu());

        addButtonToPanel(btnStockfish);
        addButtonToPanel(btnDFS);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        addButtonToPanel(btnBack);

        refreshPanel();
    }

    private void showDifficultyDialog(int engineType, String title) {
        DifficultyDialog dialog = new DifficultyDialog(frame);
        dialog.setTitle(title);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            frame.showGameSetup(true, dialog.getDifficultyLevel(), engineType);
        }
    }

    private void addButtonToPanel(JButton btn) {
        contentPanel.add(btn);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
    }

    private void refreshPanel() {
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? HOVER_COLOR : BUTTON_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
                g2.dispose();
            }
        };

        button.setFont(BUTTON_FONT);
        button.setForeground(TEXT_COLOR);
        button.setBackground(BUTTON_COLOR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(300, 50));
        button.setMaximumSize(new Dimension(300, 50));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.repaint();
            }
        });

        return button;
    }
}