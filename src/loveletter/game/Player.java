package loveletter.game;

import java.util.ArrayList;
import java.util.List;

public class Player {
    int number;
    private String name;
    List<Card> hands;
    List<Card> trash = new ArrayList<>();
    int coin = 0;
    boolean guarded;
    boolean stolen;
    boolean retired;

    public Player(int number, List<Card> hands) {
        this.number = number;
        this.hands = hands;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<Card> getHands() {
        return hands;
    }

    public void setHands(List<Card> hands) {
        this.hands = hands;
    }

    public List<Card> getTrash() {
        return trash;
    }

    public void setTrash(List<Card> trash) {
        this.trash = trash;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public boolean isGuarded() {
        return guarded;
    }

    public void setGuarded(boolean guarded) {
        this.guarded = guarded;
    }

    public boolean isStolen() {
        return stolen;
    }

    public void setStolen(boolean stolen) {
        this.stolen = stolen;
    }

    public boolean isRetired() {
        return retired;
    }

    public void setRetired(boolean retired) {
        this.retired = retired;
    }

    @Override
    public String toString() {
        return "Player [number=" + number + ", hands=" + hands + "]";
    }
}
