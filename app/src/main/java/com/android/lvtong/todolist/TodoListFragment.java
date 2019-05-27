package com.android.lvtong.todolist;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.lvtong.todolist.menu.AboutActivity;
import com.android.lvtong.todolist.menu.SettingsActivity;

import java.util.List;

public class TodoListFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private RecyclerView mTodoRecyclerView;
    private TodoAdapter mAdapter;
    private FloatingActionButton mFab;
    private ImageView mTop;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    private void setupShardPreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //隐藏头图
        if (sharedPreferences.getBoolean("top_imageview_switch",true)){
            mTop.setVisibility(View.VISIBLE);
        }else{
            mTop.setVisibility(View.GONE);
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_todo_list,container,false);
        //实现toolbar
        Toolbar mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);

        mTodoRecyclerView = (RecyclerView)view.findViewById(R.id.todo_recycler_view);
        mTodoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mFab = (FloatingActionButton)view.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Todo todo = new Todo();
                TodoLab.get(getActivity()).addmTodo(todo);
                Intent intent = TodoPagerActivity
                        .newIntent(getActivity(), todo.getmId());
                startActivity(intent);
            }
        });
        mTop = (ImageView)view.findViewById(R.id.top_imageview);
        updateUI();
        setupShardPreference();
        return view;
    }

    /**
     * 生命周期部分
     */
    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);
    }
    /**
     * menu部分
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_todo_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
//            case R.id.home:
//                mDrawerLayout.openDrawer(GravityCompat.START);
//                break;
            case R.id.action_settings:
                Intent intent1 = new Intent();
                intent1.setClass(getActivity(), SettingsActivity.class);
                startActivity(intent1);
                return true;
            case R.id.about_us:
                Intent intent2 = new Intent();
                intent2.setClass(getActivity(), AboutActivity.class);
                startActivity(intent2);
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }
//        return true;

    }


    private void updateUI() {
        TodoLab todoLab = TodoLab.get(getActivity());
        List<Todo> todos = todoLab.getmTodos();
        if (mAdapter == null){
            mAdapter = new TodoAdapter(todos);
            mTodoRecyclerView.setAdapter(mAdapter);
        }else {
            mAdapter.setmTodos(todos);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_top_imageview_switch))){
            if (sharedPreferences.getBoolean("top_imageview_switch",true)){
                mTop.setVisibility(View.VISIBLE);
            }else{
                mTop.setVisibility(View.GONE);
            }
        }
    }

    private class TodoHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Todo mTodo;

        private TextView mTitleTV;
        private TextView mBeizhuTV;
        private Button mImportant;
        //删除item
        private void removeItem() {
            TodoLab.get(getActivity()).removeTodo(mTodo);
            updateUI();
            mAdapter.notifyDataSetChanged();
        }



        public TodoHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_todo,parent,false));
            itemView.setOnClickListener(this);
            //长按弹窗删除item
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("确认要删除此代办事项?")
                            .setMessage("标题:"+mTodo.getmTitle()+"\n"
                                    +"日期:"+TodoFragment.getDateString(mTodo.getmDate())+"\n"
                                    +"重要程度:"+mTodo.getmImportance())
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    removeItem();
                                }
                            })
                            .setNegativeButton("取消",null)
                            .create().show();
                    return true;
                }
            });

            mTitleTV = (TextView)itemView.findViewById(R.id.todo_title);
            mBeizhuTV = (TextView)itemView.findViewById(R.id.todo_beizhu);
            mImportant = (Button) itemView.findViewById(R.id.todo_importance);
        }

        public void bind(Todo todo){
            mTodo = todo;
            mTitleTV.setText(mTodo.getmTitle());
            mBeizhuTV.setText(mTodo.getmBeizhu());
            mImportant.setText(String.valueOf(mTodo.getmImportance()));
        }

        @Override
        public void onClick(View v) {
            Intent intent = TodoPagerActivity.newIntent(getActivity(),mTodo.getmId());
            startActivity(intent);
        }
    }

    private class TodoAdapter extends RecyclerView.Adapter<TodoHolder>{

        private List<Todo> mTodos;

        public TodoAdapter(List<Todo> todos){
            mTodos = todos;
        }

        @NonNull
        @Override
        public TodoHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new TodoHolder(layoutInflater,parent);
        }

        @Override
        public void onBindViewHolder(@NonNull TodoHolder todoHolder, int i) {
            Todo todo = mTodos.get(i);
            todoHolder.bind(todo);
        }

        @Override
        public int getItemCount() {
            return mTodos.size();
        }

        public void setmTodos(List<Todo> todos){
            mTodos = todos;
        }
    }
}
