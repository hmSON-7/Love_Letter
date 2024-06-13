package loveletter.game;

import loveletter.ui.BoardGUI;

import java.util.*;

public class Effect {
    ArrayList<Player> playerList;
    BoardGUI gui;

    public Effect(ArrayList<Player> playerList, BoardGUI gui) {
        super();
        this.playerList = playerList;
        this.gui = gui; // Initialize the gui field
    }

    Player solve(LinkedList<Card> deck, List<Card> dummy, Card card, Player currentPlayer, int handNum) {
        Scanner sc = new Scanner(System.in);
        String line;
        Player opponentPlayer; // 지목한 상대 플레이어
        Card opponentHand; // 상대 플레이어의 패
        Card currentHand; // 플레이어 본인의 패
        Card trashing; // 버려야 할 패
        int objectCard; // 경비병 효과로 선택된 목표 카드
        boolean checkGuard; // 모든 상대 플레이어가 보호받는 중인지 확인

        switch (card.getValue()) {
            /*
             * 0 : 첩자
             * 카드 사용 후 마지막까지 살아남으면 게임 결과와 관계 없이 코인을 얻는다.
             * 단, 다른 플레이어가 다시 <0: 첩자> 카드를 사용할 경우 상기한 효과를 제거한다.
             */
            case 0:
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

                if(!check) {
                    System.out.println("player" + currentPlayer.getNumber() + "가 첩자 효과를 발동합니다.");
                    System.out.println("마지막까지 살아남는다면 게임 결과와 관계 없이 코인을 획득합니다!");
                    currentPlayer.setStolen(true);
                }
                break;

            /*
             * 1 : 경비병
             * 다른 플레이어 한 명을 지목해서 어떤 카드를 들고 있을 지 추측한다.
             * 추측에 성공하면 지목된 플레이어는 탈락하며, 실패하면 아무 일도 일어나지 않는다.
             * 단, 상대 플레이어의 카드를 <1: 경비병>으로 추측할 수는 없다.
             */
            case 1:
                opponentPlayer = selectPlayer(currentPlayer);
                opponentHand = opponentPlayer.getHands().getFirst();

                // 현재 생존한 모든 플레이어가 시녀의 보호를 받는지 확인
                checkGuard = true;
                for (Player player : playerList) {
                    if(player.isRetired() || player.equals(currentPlayer))
                        continue;

                    if(!player.isGuarded()) {
                        checkGuard = false;
                        break;
                    }
                }

                // 만약 생존한 모든 플레이어가 시녀의 보호를 받는 경우 선택한 카드를 버림
                if(checkGuard) {
                    System.out.println("생존한 모든 플레이어가 보호받고 있습니다... 해당 카드를 버립니다...");
                    return null;
                }

                // 선택한 대상이 시녀의 효과를 받는 경우
                while (opponentPlayer.isGuarded()) {
                    System.out.println("선택할 수 없는 대상입니다! 다른 플레이어를 선택해주세요.");
                    opponentPlayer = selectPlayer(currentPlayer);
                    opponentHand = opponentPlayer.getHands().getFirst();
                }

                objectCard = selectCard();

                if (opponentHand.getValue() == objectCard) {
                    System.out.println("플레이어 " + opponentPlayer + "의 카드를 맞혔습니다!");
                    System.out.println("플레이어 " + opponentPlayer + "가 탈락합니다!");
                    opponentPlayer.setRetired(true);
                    gui.updatePlayerInfo(playerList);
                    return opponentPlayer;

                } else {
                    System.out.println("잘못 선택하셨습니다.");
                    return null;
                }
            /*
             * 2 : 사제
             * 상대 플레이어 한 명을 지목해서 어떤 카드를 갖고 있는지 확인할 수 있다.
             * GUI 개발 후 최종 릴리즈 시 상대 플레이어의 카드는 지목한 플레이어 본인만 확인할 수 있도록 할 예정.
             */
            case 2:
                opponentPlayer = selectPlayer(currentPlayer);

                // 현재 생존한 모든 플레이어가 시녀의 보호를 받는지 확인
                checkGuard = true;
                for (Player player : playerList) {
                    if(player.isRetired())
                        continue;

                    if(!player.isGuarded()) {
                        checkGuard = false;
                        break;
                    }
                }

                // 만약 생존한 모든 플레이어가 시녀의 보호를 받는 경우 선택한 카드를 버림
                if(checkGuard) {
                    System.out.println("생존한 모든 플레이어가 보호받고 있습니다... 해당 카드를 버립니다...");
                    break;
                }

                // 선택한 대상이 시녀의 효과를 받는 경우
                while (opponentPlayer.isGuarded()) {
                    System.out.println("선택할 수 없는 대상입니다! 다른 플레이어를 선택해주세요.");
                    opponentPlayer = selectPlayer(currentPlayer);
                }

                System.out.println("플레이어 " + opponentPlayer + "의 카드는 " + opponentPlayer.getHands().getFirst() + "입니다!");
                break;

            /*
             * 3 : 남작
             * 상대 플레이어 한 명을 지목하여 1대1 결투를 진행한다. 서로의 카드를 비교하여 더 높은 카드를 가진 플레이어가 승리한다.
             * 패배한 플레이어는 이 게임에서 탈락한다. 무승부인 경우 아무 일도 일어나지 않는다.
             */
            case 3:
                opponentPlayer = selectPlayer(currentPlayer);

                // 현재 생존한 모든 플레이어가 시녀의 보호를 받는지 확인
                checkGuard = true;
                for (Player player : playerList) {
                    if(player.isRetired())
                        continue;

                    if(!player.isGuarded()) {
                        checkGuard = false;
                        break;
                    }
                }

                // 만약 생존한 모든 플레이어가 시녀의 보호를 받는 경우 선택한 카드를 버림
                if(checkGuard) {
                    System.out.println("생존한 모든 플레이어가 보호받고 있습니다... 해당 카드를 버립니다...");
                    break;
                }

                // 선택한 대상이 시녀의 효과를 받는 경우
                while (opponentPlayer.isGuarded()) {
                    System.out.println("선택할 수 없는 대상입니다! 다른 플레이어를 선택해주세요.");
                    opponentPlayer = selectPlayer(currentPlayer);
                }

                opponentHand = opponentPlayer.getHands().getFirst();
                currentHand = currentPlayer.getHands().getFirst();

                if (currentHand.getValue() > opponentHand.getValue()) {
                    System.out.println("플레이어 " + opponentPlayer.getNumber() + "이 결투에서 패배했습니다");
                    System.out.println("플레이어 " + currentPlayer + "의 카드: " + currentHand);
                    System.out.println("플레이어 " + opponentPlayer + "의 카드: " + opponentHand);
                    opponentPlayer.setRetired(true);
                    return opponentPlayer;

                } else if (currentHand.getValue() < opponentHand.getValue()) {
                    System.out.println("플레이어 " + currentPlayer.getNumber() + "이 결투에서 패배했습니다");
                    System.out.println("플레이어 " + currentPlayer + "의 카드: " + currentHand);
                    System.out.println("플레이어 " + opponentPlayer + "의 카드: " + opponentHand);
                    currentPlayer.setRetired(true);
                    return currentPlayer;

                } else {
                    System.out.println("무승부입니다!");
                    System.out.println("플레이어 " + currentPlayer + "의 카드: " + currentHand);
                    System.out.println("플레이어 " + opponentPlayer + "의 카드: " + opponentHand);
                    return null;
                }

            /*
             * 4 : 시녀
             * 카드를 사용하면 1턴간 보호(Guard) 상태가 된다.
             * 이 상태가 지속되는 동안 어떤 형태로든 상대 플레이어에게 지목당하지 않는다.
             */
            case 4:
                System.out.println("player" + currentPlayer.getNumber() + "는 1턴동안 상대에게 지목되지 않습니다.");
                currentPlayer.setGuarded(true);
                return null;

            /*
             * 5 : 왕자
             * 상대 플레이어 한 명을 지목한다. 지목된 대상은 반드시 가지고 있는 카드를 버린 후 새 카드를 드로우한다.
             * 해당 효과로 <9: 공주> 카드가 버려진 경우에도 공주의 효과는 정상적으로 발동된다.
             */
            case 5:
                opponentPlayer = selectPlayer(currentPlayer);

                // 현재 생존한 모든 플레이어가 시녀의 보호를 받는지 확인
                checkGuard = true;
                for (Player player : playerList) {
                    if(player.isRetired())
                        continue;

                    if(!player.isGuarded()) {
                        checkGuard = false;
                        break;
                    }
                }

                // 만약 생존한 모든 플레이어가 시녀의 보호를 받는 경우 선택한 카드를 버림
                if(checkGuard) {
                    System.out.println("생존한 모든 플레이어가 보호받고 있습니다... 해당 카드를 버립니다...");
                    break;
                }

                // 선택한 대상이 시녀의 효과를 받는 경우
                while (opponentPlayer.isGuarded()) {
                    System.out.println("선택할 수 없는 대상입니다! 다른 플레이어를 선택해주세요.");
                    opponentPlayer = selectPlayer(currentPlayer);
                }

                if (opponentPlayer.getHands().isEmpty()) {
                    System.out.println("상대 플레이어의 손에 카드가 없습니다!");
                    break;
                }

                Card trashed = opponentPlayer.getHands().removeFirst();
                System.out.println(trashed + "를 버립니다.");

                if(trashed.getValue() == 9) {
                    System.out.println("공주 카드가 버려집니다.");
                    System.out.println("player" + opponentPlayer.getNumber() + "는 공주 카드의 효과로 즉시 탈락합니다!");

                    opponentPlayer.setRetired(true);
                    return opponentPlayer;
                }

                opponentPlayer.getTrash().add(trashed);
                gui.updateDummyCard(trashed);
                dummy.add(trashed);

                opponentPlayer.getHands().add(deck.poll());

                return null;

            /*
             * 6 : 수상
             * 카드를 사용한 후 덱에서 카드를 2장 뽑는다.
             * 가지고 있는 3장의 카드 중 한 장을 선택하여 자신의 패로 가져가고, 나머지 2장은 덱의 제일 아래에 원하는 순서대로 놓는다.
             */
            case 6:
                for(int i=0; i<2; i++) {
                    Card drewCard = deck.poll();
                    currentPlayer.getHands().add(drewCard);
                }

                gui.updatePlayerInfo(playerList);

                System.out.println("0: " + currentPlayer.getHands().get(0));
                System.out.println("1: " + currentPlayer.getHands().get(1));
                System.out.println("2: " + currentPlayer.getHands().get(2));

                System.out.println("카드 한 장을 선택하세요! 나머지 두 장은 덱의 아래로 내려갑니다. [ 0, 1, 2 ]");
                int num = sc.nextInt();

                // 선택한 대상이 시녀의 효과를 받는 경우
                while (num < 0 || num > 2) {
                    System.out.println("잘못 선택하셨습니다. 0 ~ 2 사이의 숫자를 선택해주세요!");
                    num = sc.nextInt();
                }

                Card selected = null;
                for(int i=0; i<=2; i++) {
                    if(i == num) {
                        selected = currentPlayer.getHands().removeFirst();
                    } else {
                        deck.add(currentPlayer.getHands().removeFirst());
                    }
                }
                currentPlayer.getHands().add(selected);
                System.out.println("수상의 효과로 두 장의 카드가 덱의 제일 아래에 묻힙니다!");
                return null;

            /*
             * 7 : 왕
             * 상대 플레이어 한 명을 지목하여 서로의 카드를 교환한다.
             * <9: 공주> 카드의 효과는 발동되지 않는다. "버리는" 행위가 아니기 때문이다.
             */
            case 7:
                opponentPlayer = selectPlayer(currentPlayer);

                // 현재 생존한 모든 플레이어가 시녀의 보호를 받는지 확인
                checkGuard = true;
                for (Player player : playerList) {
                    if(player.isRetired())
                        continue;

                    if(!player.isGuarded()) {
                        checkGuard = false;
                        break;
                    }
                }

                // 만약 생존한 모든 플레이어가 시녀의 보호를 받는 경우 선택한 카드를 버림
                if(checkGuard) {
                    System.out.println("생존한 모든 플레이어가 보호받고 있습니다... 해당 카드를 버립니다...");
                    break;
                }

                // 선택한 대상이 시녀의 효과를 받는 경우
                while (opponentPlayer.isGuarded()) {
                    System.out.println("선택할 수 없는 대상입니다! 다른 플레이어를 선택해주세요.");
                    opponentPlayer = selectPlayer(currentPlayer);
                }

                opponentHand = opponentPlayer.getHands().getFirst();
                currentHand = currentPlayer.getHands().getFirst();

                opponentPlayer.getHands().removeFirst();
                currentPlayer.getHands().removeFirst();

                opponentPlayer.getHands().add(currentHand);
                currentPlayer.getHands().add(opponentHand);

                System.out.println("플레이어 " + currentPlayer + ", 플레이어" + opponentPlayer + "의 카드가 교환됩니다");

                return null;

            /*
             * 8 : 백작 부인
             * 자신의 패에 <5: 왕자> 또는 <7: 왕>이 백작 부인과 공존하는 경우, 반드시 <8: 백작 부인> 카드를 버린다.
             */
            case 8:
                System.out.println("백작 부인 카드가 버려집니다.");
                System.out.println("자의로 버렸는지 아닌지는... 본인만이 알고 있을 겁니다.");

                return null;

            /*
             * 9 : 공주
             * 이 카드가 자의 또는 상대의 공격에 의해 버려지는 즉시 이 게임에서 탈락한다.
             */
            case 9:
                System.out.println("공주 카드가 버려집니다.");
                System.out.println("player" + currentPlayer.getNumber() + "는 공주 카드의 효과로 즉시 탈락합니다!");

                currentPlayer.setRetired(true);

                return currentPlayer;

            default:
                return null;
        }

        return null;
    }

    private Player selectPlayer(Player currentPlayer) {
        Scanner sc = new Scanner(System.in);
        Player selectedPlayer;
        int line;

        while (true) {
            System.out.println("상대 플레이어를 선택하세요: 0, 1, 2, 3");
            line = sc.nextInt();

            if (line < 0 || line > 3) {
                System.out.println("잘못된 입력입니다. 0~3 사이의 숫자를 입력하세요.");
                continue;
            }
            selectedPlayer = playerList.get(line);

            // 죽은 플레이어를 선택할 경우 -> 재선택
            if(selectedPlayer.isRetired()) {
                System.out.println("이미 탈락한 플레이어입니다. 다른 플레이어를 선택하세요.");
                continue;
            }

            // 보호받는 플레이어를 선택할 경우 -> 재선택
            if(selectedPlayer.isGuarded()) {
                System.out.println("<4: 시녀> 카드의 효과로 보호받고 있습니다. 다른 플레이어를 선택하세요.");
                continue;
            }

            // 자신을 선택한 경우 -> 재선택
            if(selectedPlayer.equals(currentPlayer)) {
                System.out.println("플레이어 자신을 선택할 수 없습니다. 다른 플레이어를 선택하세요.");
                continue;
            }

            break;
        }
        return selectedPlayer;
    }

    // 경비병 카드 선택 처리
    private int selectCard(){

        Scanner sc = new Scanner(System.in);
        int line;

        while (true) {
            System.out.println("상대가 어떤 카드를 들고 있을지 추측하세요!");
            System.out.println("0: [첩자], 2: [사제], 3: [남작], 4: [시녀], " +
                    "5: [왕자], 6: [수상], 7: [왕], 8: [백작 부인], 9: [공주]");
            line = sc.nextInt();

            if (line < 0 || line > 9) {
                System.out.println("잘못된 입력입니다. 0~9 사이의 숫자를 입력하세요.");
                continue;
            }

            if (line == 1) {
                System.out.println("경비병은 선택할 수 없습니다. 다른 숫자를 입력하세요.");
                continue;
            }

            return line;
        }
    }
}
