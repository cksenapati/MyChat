package com.example.android.mychat.models;

/**
 * Created by chandan on 18-04-2017.
 */
public class TicTacToe {
   private String user1,user2;
    private int point1,point2;
    private String whoseTurn;
    int[][] gameTable;

    public TicTacToe() {
    }

    public TicTacToe(String user1, String user2, int point1, int point2, String whoseTurn,int[][] gameTable) {
        this.user1 = user1;
        this.user2 = user2;
        this.point1 = point1;
        this.point2 = point2;
        this.whoseTurn = whoseTurn;
        this.gameTable = gameTable;

    }

    public String getUser1() {
        return user1;
    }

    public String getUser2() {
        return user2;
    }

    public int getPoint1() {
        return point1;
    }

    public int getPoint2() {
        return point2;
    }

    public String getWhoseTurn() {
        return whoseTurn;
    }

    public int[][] getGameTable() {
        return gameTable;
    }

    //Setter


    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public void setPoint1(int point1) {
        this.point1 = point1;
    }

    public void setPoint2(int point2) {
        this.point2 = point2;
    }

    public void setWhoseTurn(String whoseTurn) {
        this.whoseTurn = whoseTurn;
    }

    public void setGameTable(int[][] gameTable) {
        this.gameTable = gameTable;
    }
}

