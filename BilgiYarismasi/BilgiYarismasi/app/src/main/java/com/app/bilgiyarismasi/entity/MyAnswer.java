package com.app.bilgiyarismasi.entity;


public class MyAnswer {

    private int myAnswerPosition;
    private String myAnswer;


    public MyAnswer(int myAnswerPosition, String myAnswer) {
        this.myAnswerPosition = myAnswerPosition;
        this.myAnswer = myAnswer;
    }

    public int getMyAnswerPosition() {
        return myAnswerPosition;
    }

    public void setMyAnswerPosition(int myAnswerPosition) {
        this.myAnswerPosition = myAnswerPosition;
    }

    public String getMyAnswer() {
        return myAnswer;
    }

    public void setMyAnswer(String myAnswer) {
        this.myAnswer = myAnswer;
    }
}
