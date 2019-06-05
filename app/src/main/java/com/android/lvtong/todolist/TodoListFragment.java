package com.android.lvtong.todolist;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.lvtong.todolist.menu.AboutActivity;
import com.android.lvtong.todolist.menu.SettingsActivity;

import java.util.List;

import static android.content.Context.VIBRATOR_SERVICE;

public class TodoListFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private RecyclerView mTodoRecyclerView;
    private TodoAdapter mAdapter;
    private FloatingActionButton mFab;
    private ImageView mTop;
    private TextView mNullTodoListTextView;
    private DrawerLayout mDrawerLayout;
    private boolean mVibrate=true;

    private static final String VIBRATE = "vibrate";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    //偏好设置
    private void setupShardPreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //隐藏头图
        mTop.setVisibility(sharedPreferences.getBoolean("top_imageview_switch",true)?View.VISIBLE:View.GONE);
        mVibrate = sharedPreferences.getBoolean("button_vibrate",true);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_top_imageview_switch))){
            mTop.setVisibility(sharedPreferences.getBoolean("top_imageview_switch",true)?View.VISIBLE:View.GONE);
        }
        if (key.equals(getString(R.string.pref_button_vibrate))){
            mVibrate = sharedPreferences.getBoolean("button_vibrate", true);
            System.out.println("button_vibrate = "+ mVibrate);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_todo_list,container,false);
        //实现toolbar
        final Toolbar mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        //添加ic_menu图标
        mDrawerLayout = (DrawerLayout)view.findViewById(R.id.drawer_layout);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        mNullTodoListTextView = (TextView)view.findViewById(R.id.null_todo_list);

        mTodoRecyclerView = (RecyclerView)view.findViewById(R.id.todo_recycler_view);
        mTodoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mTodoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 0){
                    mFab.show();
                }else {
                    mFab.hide();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

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
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
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
        if (todos.size() != 0) {
            mNullTodoListTextView.setVisibility(View.INVISIBLE);
        } else {
            mNullTodoListTextView.setVisibility(View.VISIBLE);
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
                    if (mVibrate){
                        vibrateIt();
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("请选择以下操作之一：")
                            .setPositiveButton("分享", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ShareCompat.IntentBuilder
                                            //传入Activity
                                            .from(getActivity())
                                            //设置数据
                                            .setText("标题:"+mTodo.getmTitle()+"\n"
                                                    +"备注:"+mTodo.getmBeizhu()+"\n"
                                                    +"日期:"+TodoFragment.getDateString(mTodo.getmDate())+"\n"
                                                    +"重要程度:"+mTodo.getmImportance())
                                            //设置选择器标题
                                            .setChooserTitle("分享")
                                            //分享数据的类型
                                            .setType("text/plain")
                                            //启动
                                            .startChooser();
                                }
                            })
                            .setNegativeButton("删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
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
                                }
                            })
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
            mBeizhuTV.setText(mTodo.getmBeizhu()== null||mTodo.getmBeizhu().isEmpty()?"备注未填写":mTodo.getmBeizhu());
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

    private void vibrateIt() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator)getActivity().getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator)getActivity().getSystemService(VIBRATOR_SERVICE)).vibrate(30);
        }
    }

}
