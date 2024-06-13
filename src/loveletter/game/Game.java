package loveletter.game;

import loveletter.ui.BoardGUI;

import java.io.*;
import java.util.*;

public class Game {
    private ArrayList<Player> playerList;
    private LinkedList<Card> deck;
    private List<Card> dummy;
    private BoardGUI gui;

    public void start(BoardGUI gui) {
        this.gui = gui;
        deck = new LinkedList<>();
        dummy = new ArrayList<>();
        playerList = new ArrayList<>();
        BufferedReader br = null;
        Effect ef = new Effect(playerList, gui);

        for (int i = 0; i < 4; i++) {
            playerList.add(new Player(i, new ArrayList<>()));
        }

        madeDeck(deck);
        Collections.shuffle(deck);
        dummy.add(deck.poll());
        distributeCards(deck, playerList);

        int currentPlayerNum = new Random().nextInt(4);

        while (deck.iterator().hasNext()) {
            int count = 0;
            for (Player p : playerList) {
                if (p.isRetired())
                    count++;
            }
            if (count == 3) {
                break;
            }

            currentPlayerNum = currentPlayerNum % 4;

            Player currentPlayer = playerList.get(currentPlayerNum);

            if (currentPlayer.isGuarded()) {
                currentPlayer.setGuarded(false);
            }

            if (currentPlayer.isRetired()) {
                currentPlayerNum++;
                continue;
            }
            System.out.println("Player" + currentPlayerNum + "의 턴입니다.");

            Card drewCard = deck.poll();

            System.out.println("드로우: " + drewCard);
            playerList.forEach(player -> {
                System.out.println(player.getTrash());
            });

            currentPlayer.getHands().add(drewCard);

            gui.updatePlayerInfo(playerList);
            gui.updateDeckSize(deck.size());

            int handNum;
            while (true) {
                System.out.println("제출할 카드를 선택해주세요.");

                System.out.println("0: " + currentPlayer.getHands().get(0));
                System.out.println("1: " + currentPlayer.getHands().get(1));

                // 현재 패가 < 8: 백작 부인, 5: 왕자 > 또는 < 8: 백작 부인, 7: 왕 >인 경우 -> 즉시 백작 부인을 버림
                if (currentPlayer.getHands().get(0).getValue() == 8) {
                    if (currentPlayer.getHands().get(1).getValue() == 5 ||
                            currentPlayer.getHands().get(1).getValue() == 7) {

                        handNum = 0;
                        break;

                    }
                } else if (currentPlayer.getHands().get(1).getValue() == 8) {
                    if (currentPlayer.getHands().get(0).getValue() == 5 ||
                            currentPlayer.getHands().get(0).getValue() == 7) {

                        handNum = 1;
                        break;

                    }
                }

                String line = null;  // 입력값을 받음
                try {
                    line = br.readLine().trim();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (line.isEmpty()) {
                    System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
                    continue;  // 반복문 처음으로 돌아감
                }

                try {
                    handNum = Integer.parseInt(line);

                } catch (NumberFormatException e) {
                    System.out.println("숫자를 입력해주세요.");
                    continue;  // 반복문 처음으로 돌아감
                }

                // 선택한 카드 처리 로직
                if (handNum < 0 || handNum >= currentPlayer.getHands().size()) {
                    System.out.println("잘못된 선택입니다. 다시 입력해주세요.");
                    continue;  // 반복문 처음으로 돌아감
                }

                break;
            }
            Card trashCard = currentPlayer.getHands().get(handNum);
            currentPlayer.getHands().remove(handNum);
            gui.updateDummyCard(trashCard);

            Player deadPlayer = ef.solve(deck, dummy, trashCard, currentPlayer, handNum);
            currentPlayer.getTrash().add(trashCard);
            if(deadPlayer != null) {
                Card card = deadPlayer.getHands().removeFirst();
                deadPlayer.getTrash().add(card);
                dummy.add(card);

                gui.updateDummyCard(card);
            }

            gui.updatePlayerInfo(playerList);
            gui.updateDeckSize(deck.size());

            currentPlayerNum++;
        }

        if (deck.isEmpty()) {
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
        } else {
            System.out.println("3명이 탈락하였습니다.");
            System.out.println("남은 카드와 관계 없이 최후의 생존자가 승리합니다!");
            System.out.println(playerList);

            playerList.forEach(player -> {
                if (!player.isRetired()) {
                    Player won = player;
                    System.out.println("승자는 player" + won.getNumber() + "입니다!");

                    if (won.isStolen()) {
                        won.setCoin(won.getCoin() + 1);
                        System.out.println("첩자 상태로 살아남은 player " + won.getNumber() + " 는 추가 코인을 얻습니다!");
                    }
                }
            });

            System.out.println("승자는 코인을 가져가주세요!");
        }
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
}
