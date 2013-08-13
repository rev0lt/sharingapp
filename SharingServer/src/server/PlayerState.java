package server;

public class PlayerState {

    private short gameId;
    private short characterId;
    private boolean ready;
    private boolean scoresSent;
    private int potatoesShot;
    private int losses;

    public PlayerState() {
        gameId = -1;
        characterId = -1;
        ready = false;
        scoresSent = false;
        potatoesShot = 0;
        losses = 0;
    }

    public PlayerState(short gameId, short characterId) {
        this.gameId = gameId;
        this.characterId = characterId;
        ready = false;
        scoresSent = false;
        potatoesShot = 0;
        losses = 0;
    }

    public short getGameId() {
        return gameId;
    }

    public void setGameId(short gameId) {
        this.gameId = gameId;
    }

    public short getCharacterId() {
        return characterId;
    }

    public void setCharacterId(short characterId) {
        this.characterId = characterId;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
    
    public boolean scoresSent() {
        return scoresSent;
    }

    public void setScoresSent(boolean scoresSent) {
        this.scoresSent = scoresSent;
    }

    public int getPotatoesShot() {
        return potatoesShot;
    }

    public void setPotatoesShot(int score) {
        this.potatoesShot = score;
    }
    
    public void setLosses(int num) {
        this.losses = num;
    }
    
    public int getLosses() {
        return this.losses;
    }

}
