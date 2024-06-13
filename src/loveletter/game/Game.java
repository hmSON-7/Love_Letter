package loveletter.game;

import loveletter.ui.BoardGUI;

import java.util.*;

public class Game {
    private ArrayList<Player> playerList;
    private LinkedList<Card> deck;
    private List<Card> dummy;
    private BoardGUI gui;
    private int currentPlayerNum;

    public Game() {
        this.playerList = new ArrayList<>();
        this.deck = new LinkedList<>();
        this.dummy = new ArrayList<>();
    }

    public void start(BoardGUI gui) {
        this.gui = gui;
        deck = new LinkedList<>();
        dummy = new ArrayList<>();
        playerList = new ArrayList<>();
        Effect ef = new Effect(playerList, this);

        for (int i = 0; i < 4; i++) {
            playerList.add(new Player(i, new ArrayList<>()));
        }

        madeDeck(deck);
        Collections.shuffle(deck);
        dummy.add(deck.poll());
        distributeCards(deck, playerList);

        currentPlayerNum = new Random().nextInt(4);
        nextTurn();
    }

    public void nextTurn() {
        if (deck.isEmpty() || playerList.stream().filter(p -> !p.isRetired()).count() == 1) {
            endGame();
            return;
        }

        currentPlayerNum = currentPlayerNum % 4;

        Player currentPlayer = playerList.get(currentPlayerNum);

        if (currentPlayer.isGuarded()) {
            currentPlayer.setGuarded(false);
        }

        if (currentPlayer.isRetired()) {
            currentPlayerNum++;
            nextTurn();
            return;
        }

        System.out.println("Player" + currentPlayerNum + "의 턴입니다.");
        Card drewCard = deck.poll();
        currentPlayer.getHands().add(drewCard);

        System.out.println("드로우: " + drewCard);
        playerList.forEach(player -> {
            System.out.println(player.getTrash());
        });

        gui.updatePlayerInfo(playerList);
        gui.updateDeckSize(deck.size());
    }

    public void useCard(Player currentPlayer, Card card) {
        currentPlayer.getHands().remove(card);
        gui.updateDummyCard(card);

        Effect ef = new Effect(playerList, this);
        ef.solve(deck, dummy, card, currentPlayer);
    }

    public void endTurn() {
        currentPlayerNum++;
        nextTurn();
    }

    private void endGame() {
        System.out.println("덱의 모든 카드가 소진되어 게임을 종료합니다.");
        System.out.println("생존한 모든 플레이어의 손패를 확인합니다.");
        System.out.println(playerList);

        Player won = playerList.get(0);
        int max = 0;
        for (Player player : playerList) {
            if (player.getHands().isEmpty())
                continue;

            if (player.isStolen()) {
                player.setCoin(player.getCoin() + 1);
                System.out.println("첩자 상태로 살아남은 player " + player.getNumber() + " 는 추가 코인을 얻습니다!");
            }

            if (max < player.getHands().getFirst().getValue()) {
                won = player;
                max = player.getHands().getFirst().getValue();
                won.setCoin(won.getCoin() + 1);
            }
        }

        System.out.println("승자는 player" + won.getNumber() + "입니다!");
        System.out.println("승자는 코인을 가져가주세요!");
    }

    private void madeDeck(List<Card> deck) {
        deck.add(new Card(0, "첩자"));
        deck.add(new Card(0, "첩자"));

        deck.add(new Card(1, "경비병"));
        deck.add(new Card(1, "경비병"));
        deck.add(new Card(1, "경비병"));
        deck.add(new Card(1, "경비병"));
        deck.add(new Card(1, "경비병"));
        deck.add(new Card(1, "경비병"));

        deck.add(new Card(2, "사제"));
        deck.add(new Card(2, "사제"));

        deck.add(new Card(3, "남작"));
        deck.add(new Card(3, "남작"));

        deck.add(new Card(4, "시녀"));
        deck.add(new Card(4, "시녀"));

        deck.add(new Card(5, "왕자"));
        deck.add(new Card(5, "왕자"));

        deck.add(new Card(6, "수상"));
        deck.add(new Card(6, "수상"));

        deck.add(new Card(7, "왕"));

        deck.add(new Card(8, "백작 부인"));

        deck.add(new Card(9, "공주"));
    }

    private void distributeCards(LinkedList<Card> deck, ArrayList<Player> playerList) {
        playerList.forEach(player -> {
            player.getHands().add(deck.poll());
        });
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public BoardGUI getGui() {
        return gui;
    }

    public Player getCurrentPlayer() {
        return playerList.get(currentPlayerNum);
    }
}
