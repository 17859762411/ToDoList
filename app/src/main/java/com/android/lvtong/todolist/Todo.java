package com.android.lvtong.todolist;

import java.util.Date;
import java.util.UUID;

public class Todo {

    private UUID mId;
    private String mTitle;
    private String mBeizhu;
    private int mImportance;
    private Date mDate;

    Todo() {
        this(UUID.randomUUID());
    }

    public Todo(UUID id) {
        mId = id;
        mDate = new Date();
    }

    UUID getmId() {
        return mId;
    }

    String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    String getmBeizhu() {
        return mBeizhu;
    }

    public void setmBeizhu(String mBeizhu) {
        this.mBeizhu = mBeizhu;
    }

    int getmImportance() {
        return mImportance;
    }

    public void setmImportance(int mImportance) {
        this.mImportance = mImportance;
    }

    Date getmDate() {
        return mDate;
    }

    public void setmDate(Date mDate) {
        this.mDate = mDate;
    }
}
