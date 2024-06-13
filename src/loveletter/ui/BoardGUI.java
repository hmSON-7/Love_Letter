package loveletter.ui;

import loveletter.game.Card;
import loveletter.game.Game;
import loveletter.game.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class BoardGUI extends JFrame {

    private JTextArea chatArea;
    private JTextField chatInput;
    private JLabel[] playerLabels;
    private JPanel[] playerPanels;
    private JPanel deckPanel, dummyPanel;

    private Game game;

    public BoardGUI(Game game) {
        this.game = game;
        setTitle("Love Letter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(false);
        setResizable(false);

        // 배경 색상
        Color lightBrown = new Color(210, 180, 140);
        Color darkBrown = new Color(139, 69, 19);
        Dimension cardSize = new Dimension(100, 140); // Increased card size for fullscreen

        // 게임 보드
        JPanel gameBoard = new JPanel(new GridLayout(2, 2));
        gameBoard.setPreferredSize(new Dimension(800, 800));
        playerPanels = new JPanel[4];
        for (int i = 0; i < 4; i++) {
            JPanel playerPanel = new JPanel(new BorderLayout());
            playerPanel.setBackground(lightBrown);
            playerPanel.setBorder(BorderFactory.createLineBorder(darkBrown, 10));

            JLabel playerName = new JLabel("Player " + i, SwingConstants.CENTER);
            playerName.setPreferredSize(new Dimension(200, 30));
            playerPanel.add(playerName, BorderLayout.NORTH);

            JPanel cardContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
            cardContainer.setBackground(lightBrown);
            for (int j = 0; j < 3; j++) {
                JPanel cardSlot = new JPanel();
                cardSlot.setPreferredSize(cardSize);
                cardSlot.setBackground(lightBrown);
                cardSlot.setBorder(BorderFactory.createLineBorder(darkBrown, 5));
                cardContainer.add(cardSlot);
            }
            playerPanel.add(cardContainer, BorderLayout.CENTER);

            playerPanels[i] = playerPanel;
            gameBoard.add(playerPanel);
        }

        // 좌측 패널 : 덱과 더미
        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        leftPanel.setPreferredSize(new Dimension(300, 400));
        leftPanel.setBackground(lightBrown);
        leftPanel.setBorder(BorderFactory.createLineBorder(darkBrown, 10));

        deckPanel = new JPanel(new BorderLayout());
        deckPanel.setBackground(lightBrown);
        deckPanel.setBorder(BorderFactory.createLineBorder(darkBrown, 5));
        deckPanel.setPreferredSize(cardSize);
        deckPanel.add(new JLabel("Deck ( 남은 카드 :  )", SwingConstants.CENTER), BorderLayout.NORTH);

        JPanel deckCardSlot = new JPanel();
        deckCardSlot.setPreferredSize(cardSize);
        deckCardSlot.setBackground(lightBrown);
        deckCardSlot.setBorder(BorderFactory.createLineBorder(darkBrown, 5));
        deckPanel.add(deckCardSlot, BorderLayout.CENTER);

        dummyPanel = new JPanel(new BorderLayout());
        dummyPanel.setBackground(lightBrown);
        dummyPanel.setBorder(BorderFactory.createLineBorder(darkBrown, 5));
        dummyPanel.setPreferredSize(cardSize);
        dummyPanel.add(new JLabel("Dummy ( 카드 수 :  )", SwingConstants.CENTER), BorderLayout.NORTH);

        JPanel dummyCardSlot = new JPanel();
        dummyCardSlot.setPreferredSize(cardSize);
        dummyCardSlot.setBackground(lightBrown);
        dummyCardSlot.setBorder(BorderFactory.createLineBorder(darkBrown, 5));
        dummyPanel.add(dummyCardSlot, BorderLayout.CENTER);

        leftPanel.add(deckPanel);
        leftPanel.add(dummyPanel);

        // 우측 패널 : 플레이어 프로필과 채팅
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(400, 800));

        // 플레이어 프로필
        JPanel playerProfile = new JPanel(new GridLayout(4, 1, 0, 10));
        playerProfile.setPreferredSize(new Dimension(300, 200));
        playerProfile.setBackground(lightBrown);
        playerProfile.setBorder(BorderFactory.createLineBorder(darkBrown, 10));
        playerLabels = new JLabel[4];
        for (int i = 0; i < 4; i++) {
            playerLabels[i] = new JLabel("Player " + i + ": 0 Coins", SwingConstants.CENTER);
            playerLabels[i].setPreferredSize(new Dimension(300, 50)); // Increased height
            playerProfile.add(playerLabels[i]);
        }

        // 채팅 공간
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatInput = new JTextField();
        chatInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = chatInput.getText();
                if (!text.isEmpty()) {
                    chatArea.append("Me: " + text + "\n");
                    chatInput.setText("");
                }
            }
        });
        chatPanel.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        chatPanel.add(chatInput, BorderLayout.SOUTH);

        rightPanel.add(playerProfile, BorderLayout.NORTH);
        rightPanel.add(chatPanel, BorderLayout.CENTER);
        rightPanel.setBackground(lightBrown);
        rightPanel.setBorder(BorderFactory.createLineBorder(darkBrown, 10));

        add(gameBoard, BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);

        // 게임 시작 버튼
        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        add(startButton, BorderLayout.SOUTH);
    }

    private void startGame() {
        new Thread(() -> game.start(this)).start();
    }

    public void updatePlayerInfo(List<Player> players) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                playerLabels[i].setText("Player " + player.getNumber() + ": " + player.getCoin() + " Coins");
                updatePlayerCards(playerPanels[i], player.getHands());
            }
        });
    }

    private void updatePlayerCards(JPanel playerPanel, List<Card> cards) {
        SwingUtilities.invokeLater(() -> {
            JPanel cardContainer = (JPanel) ((BorderLayout) playerPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
            int cardIndex = 0;
            for (Component comp : cardContainer.getComponents()) {
                if (comp instanceof JPanel) {
                    JPanel cardSlot = (JPanel) comp;
                    if (cardIndex < cards.size()) {
                        JLabel cardLabel = new JLabel(cards.get(cardIndex).getName(), SwingConstants.CENTER);
                        cardSlot.removeAll();
                        cardSlot.add(cardLabel);
                        cardIndex++;
                    } else {
                        cardSlot.removeAll();
                    }
                    cardSlot.revalidate();
                    cardSlot.repaint();
                }
            }
        });
    }

    public void updateDeckSize(int size) {
        SwingUtilities.invokeLater(() -> {
            deckPanel.removeAll();
            deckPanel.add(new JLabel("Deck ( 남은 카드 : " + size + " )", SwingConstants.CENTER), BorderLayout.NORTH);
            JPanel deckCardSlot = new JPanel();
            deckCardSlot.setPreferredSize(deckPanel.getSize());
            deckCardSlot.setBackground(deckPanel.getBackground());
            deckCardSlot.setBorder(deckPanel.getBorder());
            deckPanel.add(deckCardSlot, BorderLayout.CENTER);
            deckPanel.revalidate();
            deckPanel.repaint();
        });
    }

    public void updateDummyCard(Card card) {
        SwingUtilities.invokeLater(() -> {
            dummyPanel.removeAll();
            if (card != null) {
                dummyPanel.add(new JLabel("Dummy ( 마지막 카드 : " + card.getName() + " )", SwingConstants.CENTER), BorderLayout.NORTH);
            } else {
                dummyPanel.add(new JLabel("Dummy ( 카드 없음 )", SwingConstants.CENTER), BorderLayout.NORTH);
            }
            JPanel dummyCardSlot = new JPanel();
            dummyCardSlot.setPreferredSize(dummyPanel.getSize());
            dummyCardSlot.setBackground(dummyPanel.getBackground());
            dummyCardSlot.setBorder(dummyPanel.getBorder());
            dummyPanel.add(dummyCardSlot, BorderLayout.CENTER);
            dummyPanel.revalidate();
            dummyPanel.repaint();
        });
    }

    public void showCardSelectionDialog(ActionListener callback) {
        JDialog dialog = new JDialog(this, "카드 선택", true);
        dialog.setLayout(new GridLayout(0, 3));
        dialog.setSize(400, 300);

        List<Card> cards = game.getCurrentPlayer().getHands();
        for (int i = 0; i < cards.size(); i++) {
            JButton button = new JButton(cards.get(i).getName());
            button.setActionCommand(String.valueOf(i));
            button.addActionListener(callback);
            button.addActionListener(e -> dialog.dispose());
            dialog.add(button);
        }

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public void showPlayerSelectionDialog(Player currentPlayer, List<Player> players, ActionListener callback) {
        JDialog dialog = new JDialog(this, "플레이어 선택", true);
        dialog.setLayout(new GridLayout(0, 2));
        dialog.setSize(400, 300);

        for (Player player : players) {
            if (player != currentPlayer && !player.isRetired() && !player.isGuarded()) {
                JButton button = new JButton("Player " + player.getNumber());
                button.setActionCommand(String.valueOf(player.getNumber()));
                button.addActionListener(callback);
                button.addActionListener(e -> dialog.dispose());
                dialog.add(button);
            }
        }

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Game game = new Game();
            new BoardGUI(game).setVisible(true);
        });
    }
}
