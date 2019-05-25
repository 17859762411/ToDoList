package com.android.lvtong.todolist;

import java.util.Date;
import java.util.UUID;

public class Todo {
    private UUID mId;
    private String mTitle;
    private String mBeizhu;
    private int mImportance;
    private Date mDate;

    public Todo(){
        this(UUID.randomUUID());
    }
    public Todo(UUID id){
        mId = id;
        mDate = new Date();
    }

    public UUID getmId() {
        return mId;
    }


    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmBeizhu() {
        return mBeizhu;
    }

    public void setmBeizhu(String mBeizhu) {
        this.mBeizhu = mBeizhu;
    }

    public int getmImportance() {
        return mImportance;
    }

    public void setmImportance(int mImportance) {
        this.mImportance = mImportance;
    }

    public Date getmDate() {
        return mDate;
    }

    public void setmDate(Date mDate) {
        this.mDate = mDate;
    }
}
