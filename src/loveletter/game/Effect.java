package loveletter.game;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Effect {
    ArrayList<Player> playerList;
    Game game;

    public Effect(ArrayList<Player> playerList, Game game) {
        super();
        this.playerList = playerList;
        this.game = game;
    }

    Player solve(LinkedList<Card> deck, List<Card> dummy, Card card, Player currentPlayer) {
        switch (card.getValue()) {
            case 0: // 첩자
                handleSpyEffect(currentPlayer);
                break;

            case 1: // 경비병
                handleGuardEffect(currentPlayer, deck, dummy);
                break;

            case 2: // 사제
                handlePriestEffect(currentPlayer);
                break;

            case 3: // 남작
                handleBaronEffect(currentPlayer, deck, dummy);
                break;

            case 4: // 시녀
                handleHandmaidEffect(currentPlayer);
                break;

            case 5: // 왕자
                handlePrinceEffect(currentPlayer, deck, dummy);
                break;

            case 6: // 수상
                handleChancellorEffect(currentPlayer, card, deck, dummy);
                break;

            case 7: // 왕
                handleKingEffect(currentPlayer);
                break;

            case 8: // 백작 부인
                handleCountessEffect();
                break;

            case 9: // 공주
                handlePrincessEffect(currentPlayer, card, dummy);
                break;

            default:
                return null;
        }
        return null;
    }

    private void handleSpyEffect(Player currentPlayer) {
        boolean check = false;
        for (Player p : playerList) {
            if (p.isStolen()) {
                check = true;
                System.out.println("player" + p.getNumber() + "가 검거되었습니다!");
                System.out.println("첩자 효과가 제거됩니다.");
                p.setStolen(false);
                break;
            }
        }

        if (!check) {
            System.out.println("player" + currentPlayer.getNumber() + "가 첩자 효과를 발동합니다.");
            System.out.println("마지막까지 살아남는다면 게임 결과와 관계 없이 코인을 획득합니다!");
            currentPlayer.setStolen(true);
        }
        game.endTurn();
    }

    private void handleGuardEffect(Player currentPlayer, LinkedList<Card> deck, List<Card> dummy) {
        game.getGui().showPlayerSelectionDialog(currentPlayer, playerList, pe -> {
            JButton button = (JButton) pe.getSource();
            int targetPlayerNumber = Integer.parseInt(button.getActionCommand());
            Player targetPlayer = playerList.get(targetPlayerNumber);

            if (targetPlayer.isGuarded() || targetPlayer.isRetired()) {
                JOptionPane.showMessageDialog(null, "선택할 수 없는 플레이어입니다.");
                return;
            }

            game.getGui().showCardGuessDialog(e -> {
                int guessedCard = Integer.parseInt(e.getActionCommand());
                if (targetPlayer.getHands().getFirst().getValue() == guessedCard) {
                    System.out.println("Player " + targetPlayer.getNumber() + "의 카드가 맞았습니다!");
                    targetPlayer.setRetired(true);
                    dummy.addAll(targetPlayer.getHands());
                    targetPlayer.getHands().clear();
                } else {
                    System.out.println("Player " + targetPlayer.getNumber() + "의 카드는 " + targetPlayer.getHands().getFirst().getName() + " 입니다.");
                }
                game.getGui().updatePlayerInfo(playerList);
                game.endTurn();
            });
        });
    }

    private void handlePriestEffect(Player currentPlayer) {
        game.getGui().showPlayerSelectionDialog(currentPlayer, playerList, pe -> {
            JButton button = (JButton) pe.getSource();
            int targetPlayerNumber = Integer.parseInt(button.getActionCommand());
            Player targetPlayer = playerList.get(targetPlayerNumber);

            System.out.println("Player " + targetPlayer.getNumber() + "의 카드는 " + targetPlayer.getHands().getFirst().getName() + " 입니다.");
            game.getGui().updatePlayerInfo(playerList);
            game.endTurn();
        });
    }

    private void handleBaronEffect(Player currentPlayer, LinkedList<Card> deck, List<Card> dummy) {
        game.getGui().showPlayerSelectionDialog(currentPlayer, playerList, pe -> {
            JButton button = (JButton) pe.getSource();
            int opponentPlayerNumber = Integer.parseInt(button.getActionCommand());
            Player opponentPlayer = playerList.get(opponentPlayerNumber);

            if (opponentPlayer.isGuarded() || opponentPlayer.isRetired()) {
                JOptionPane.showMessageDialog(null, "선택할 수 없는 플레이어입니다.");
                return;
            }

            Card opponentHand = opponentPlayer.getHands().getFirst();
            Card currentHand = currentPlayer.getHands().getFirst();

            if (currentHand.getValue() > opponentHand.getValue()) {
                System.out.println("플레이어 " + opponentPlayer.getNumber() + "이 결투에서 패배했습니다");
                opponentPlayer.setRetired(true);
                dummy.addAll(opponentPlayer.getHands());
                opponentPlayer.getHands().clear();
            } else if (currentHand.getValue() < opponentHand.getValue()) {
                System.out.println("플레이어 " + currentPlayer.getNumber() + "이 결투에서 패배했습니다");
                currentPlayer.setRetired(true);
                dummy.addAll(currentPlayer.getHands());
                currentPlayer.getHands().clear();
            } else {
                System.out.println("무승부입니다!");
            }
            game.getGui().updatePlayerInfo(playerList);
            game.endTurn();
        });
    }

    private void handleHandmaidEffect(Player currentPlayer) {
        System.out.println("player" + currentPlayer.getNumber() + "는 1턴동안 상대에게 지목되지 않습니다.");
        currentPlayer.setGuarded(true);
        game.getGui().updatePlayerInfo(playerList);
        game.endTurn();
    }

    private void handlePrinceEffect(Player currentPlayer, LinkedList<Card> deck, List<Card> dummy) {
        game.getGui().showPlayerSelectionDialog(currentPlayer, playerList, pe -> {
            JButton button = (JButton) pe.getSource();
            int opponentPlayerNumber = Integer.parseInt(button.getActionCommand());
            Player opponentPlayer = playerList.get(opponentPlayerNumber);

            if (opponentPlayer.isGuarded() || opponentPlayer.isRetired()) {
                JOptionPane.showMessageDialog(null, "선택할 수 없는 플레이어입니다.");
                return;
            }

            if (opponentPlayer.getHands().isEmpty()) {
                System.out.println("상대 플레이어의 손에 카드가 없습니다!");
                game.endTurn();
                return;
            }

            Card trashed = opponentPlayer.getHands().removeFirst();
            System.out.println(trashed + "를 버립니다.");

            if (trashed.getValue() == 9) {
                System.out.println("공주 카드가 버려집니다.");
                System.out.println("player " + opponentPlayer.getNumber() + "는 공주 카드의 효과로 즉시 탈락합니다!");
                opponentPlayer.setRetired(true);
                dummy.add(trashed);
                game.getGui().updatePlayerInfo(playerList);
                game.endTurn();
                return;
            }

            opponentPlayer.getTrash().add(trashed);
            game.getGui().updateDummyCard(trashed);
            dummy.add(trashed);

            opponentPlayer.getHands().add(deck.poll());
            game.getGui().updatePlayerInfo(playerList);
            game.endTurn();
        });
    }

    private void handleChancellorEffect(Player currentPlayer, Card usedCard, LinkedList<Card> deck, List<Card> dummy) {
        currentPlayer.getHands().remove(usedCard);
        dummy.add(usedCard);
        game.getGui().updateDummyCard(usedCard);

        for (int i = 0; i < 2; i++) {
            Card drewCard = deck.poll();
            currentPlayer.getHands().add(drewCard);
        }

        game.getGui().updatePlayerInfo(playerList);

        game.getGui().showCardSelectionDialog(e -> {
            int selectedIndex = Integer.parseInt(e.getActionCommand());
            Card selectedCard = currentPlayer.getHands().get(selectedIndex);
            List<Card> cardsToReturn = new ArrayList<>(currentPlayer.getHands());
            cardsToReturn.remove(selectedCard);
            currentPlayer.getHands().clear();
            currentPlayer.getHands().add(selectedCard);
            deck.addAll(cardsToReturn);

            System.out.println("수상의 효과로 두 장의 카드가 덱의 제일 아래에 묻힙니다!");
            game.getGui().updatePlayerInfo(playerList);
            game.getGui().updateDeckSize(deck.size());
            game.endTurn();
        });
    }

    private void handleKingEffect(Player currentPlayer) {
        game.getGui().showPlayerSelectionDialog(currentPlayer, playerList, pe -> {
            JButton button = (JButton) pe.getSource();
            int opponentPlayerNumber = Integer.parseInt(button.getActionCommand());
            Player opponentPlayer = playerList.get(opponentPlayerNumber);

            if (opponentPlayer.isGuarded() || opponentPlayer.isRetired()) {
                JOptionPane.showMessageDialog(null, "선택할 수 없는 플레이어입니다.");
                return;
            }

            Card opponentHand = opponentPlayer.getHands().getFirst();
            Card currentHand = currentPlayer.getHands().getFirst();

            opponentPlayer.getHands().removeFirst();
            currentPlayer.getHands().removeFirst();

            opponentPlayer.getHands().add(currentHand);
            currentPlayer.getHands().add(opponentHand);

            System.out.println("플레이어 " + currentPlayer + ", 플레이어 " + opponentPlayer + "의 카드가 교환됩니다");
            game.getGui().updatePlayerInfo(playerList);
            game.endTurn();
        });
    }

    private void handleCountessEffect() {
        System.out.println("백작 부인 카드가 버려집니다.");
        System.out.println("자의로 버렸는지 아닌지는... 본인만이 알고 있을 겁니다.");
        game.getGui().updatePlayerInfo(playerList);
        game.endTurn();
    }

    private void handlePrincessEffect(Player currentPlayer, Card card, List<Card> dummy) {
        System.out.println("공주 카드가 버려집니다.");
        System.out.println("player " + currentPlayer.getNumber() + "는 공주 카드의 효과로 즉시 탈락합니다!");
        currentPlayer.setRetired(true);
        dummy.add(card);
        game.getGui().updatePlayerInfo(playerList);
        game.endTurn();
    }
}
