package com.android.lvtong.todolist.database;

public class TodoDbSchema {

    public static final class TodoTable {

        public static final String NAME = "todos";

        public static final class Cols {

            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String BEIZHU = "beizhu";
            public static final String IMPORTANCE = "importance";
            public static final String DATE = "date";
        }
    }
}
