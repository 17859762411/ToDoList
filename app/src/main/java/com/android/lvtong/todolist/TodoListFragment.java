package com.android.lvtong.todolist;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.lvtong.todolist.menu.AboutActivity;
import com.android.lvtong.todolist.menu.SettingsActivity;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;

public class TodoListFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private HashSet<String> dateArray = new HashSet<>();
    private HashSet<String> dateArrayUsed = new HashSet<>();

    private RecyclerView mTodoRecyclerView;
    private TodoAdapter mAdapter;
    private FloatingActionButton mFab;
    private TextView mNullTodoListTextView;
    private DrawerLayout mDrawerLayout;
    private boolean mVibrate = true;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private TimeChangeReceiver timeChangeReceiver;
    private CharSequence channelName = "channel_name";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        timeChangeReceiver = new TimeChangeReceiver();

        Objects.requireNonNull(getActivity())
               .registerReceiver(timeChangeReceiver, intentFilter);
        View view = inflater.inflate(R.layout.fragment_todo_list, container, false);
        //实现toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        //添加ic_menu图标
        mDrawerLayout = view.findViewById(R.id.drawer_layout);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black);
        }

        collapsingToolbarLayout = view.findViewById(R.id.toolbar_layout);

        mNullTodoListTextView = view.findViewById(R.id.null_todo_list);

        mTodoRecyclerView = view.findViewById(R.id.todo_recycler_view);
        mTodoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mTodoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 0) {
                    mFab.show();
                } else {
                    mFab.hide();
                }
            }
        });
        TextView tv = view.findViewById(R.id.test_tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("时间数组:" + dateArray);
            }
        });

        mFab = view.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Todo todo = new Todo();
                TodoLab.get(getActivity())
                       .addmTodo(todo);
                Intent intent = TodoPagerActivity.newIntent(getActivity(), todo.getmId());
                startActivity(intent);
            }
        });
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
        Objects.requireNonNull(getActivity())
               .unregisterReceiver(timeChangeReceiver);
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                         .unregisterOnSharedPreferenceChangeListener(this);
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
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                Intent intent1 = new Intent();
                intent1.setClass(Objects.requireNonNull(getActivity()), SettingsActivity.class);
                startActivity(intent1);
                return true;
            case R.id.about_us:
                Intent intent2 = new Intent();
                intent2.setClass(Objects.requireNonNull(getActivity()), AboutActivity.class);
                startActivity(intent2);
                return true;
            case R.id.time_choose:
                Toast.makeText(getActivity(), "暂无", Toast.LENGTH_SHORT)
                     .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUI() {
        TodoLab todoLab = TodoLab.get(getActivity());
        List<Todo> todos = todoLab.getmTodos();
        if (mAdapter == null) {
            mAdapter = new TodoAdapter(todos);
            mTodoRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setmTodos(todos);
            mAdapter.notifyDataSetChanged();
        }
        if (!todos.isEmpty()) {
            mNullTodoListTextView.setVisibility(View.INVISIBLE);
        } else {
            mNullTodoListTextView.setVisibility(View.VISIBLE);
        }
    }

    //偏好设置
    private void setupShardPreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //隐藏头图
        collapsingToolbarLayout.setBackgroundResource(
                sharedPreferences.getBoolean("top_imageview_switch", true) ? R.drawable.img000_changed : R.color.gray);
        mVibrate = sharedPreferences.getBoolean("button_vibrate", true);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_top_imageview_switch))) {
            collapsingToolbarLayout.setBackgroundResource(
                    sharedPreferences.getBoolean("top_imageview_switch", true) ? R.drawable.img000_changed :
                    R.color.gray);
        }
        if (key.equals(getString(R.string.pref_button_vibrate))) {
            mVibrate = sharedPreferences.getBoolean("button_vibrate", true);
            System.out.println("button_vibrate = " + mVibrate);
        }
    }

    private void vibrateIt() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) Objects.requireNonNull(getActivity())
                               .getSystemService(VIBRATOR_SERVICE)).vibrate(
                    VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) Objects.requireNonNull(getActivity())
                               .getSystemService(VIBRATOR_SERVICE)).vibrate(30);
        }
    }

    /**
     * 时间广播接收类
     */
    class TimeChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), Intent.ACTION_TIME_TICK)) {
                Date date = new Date();
                String dateformated = TodoFragment.getDateStringYMD(date);
                Toast.makeText(context, "当前时间：" + dateformated, Toast.LENGTH_SHORT)
                     .show();
                if (dateArray.contains(dateformated) && !dateArrayUsed.contains(dateformated)) {
                    PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);
                    String channelID = "1";
                    NotificationChannel channel =
                            new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
                    NotificationManager notificationManager =
                            (NotificationManager) Objects.requireNonNull(getActivity())
                                                         .getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.createNotificationChannel(channel);
                    Notification notification =
                            new NotificationCompat.Builder(getActivity()).setContentTitle("New Todos!")
                                                                         .setContentText(dateformated)
                                                                         .setSmallIcon(R.drawable.ic_launcher)
                                                                         .setLargeIcon(BitmapFactory.decodeResource(
                                                                                 getResources(),
                                                                                 R.drawable.ic_launcher))
                                                                         .setContentIntent(pendingIntent)
                                                                         .setDefaults(NotificationCompat.DEFAULT_ALL)
                                                                         .setPriority(NotificationCompat.PRIORITY_MAX)
                                                                         .setChannelId(channelID)
                                                                         .setAutoCancel(true)
                                                                         .build();
                    notificationManager.notify(1, notification);
                    dateArray.remove(dateformated);
                    dateArrayUsed.add(dateformated);
                }
            }
        }
    }

    private class TodoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Todo mTodo;

        private TextView mTitleTV;
        private TextView mBeizhuTV;
        private Button mImportant;

        TodoHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_todo, parent, false));
            itemView.setOnClickListener(this);
            //长按弹窗删除item
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mVibrate) {
                        vibrateIt();
                    }
                    showListDialog();
                    return true;
                }
            });

            mTitleTV = itemView.findViewById(R.id.todo_title);
            mBeizhuTV = itemView.findViewById(R.id.todo_beizhu);
            mImportant = itemView.findViewById(R.id.todo_importance);
        }

        /**
         * 长按列表Dialog
         */
        private void showListDialog() {
            final String[] items = {"删除", "分享"};
            AlertDialog.Builder listDialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
            listDialog.setTitle("请选择以下操作之一：");
            listDialog.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("确认要删除此代办事项?")
                                   .setMessage("标题:" + mTodo.getmTitle() + "\n" + "日期:" +
                                               TodoFragment.getDateStringYMDE(mTodo.getmDate()) + "\n" + "重要程度:" +
                                               mTodo.getmImportance())
                                   .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {
                                           removeItem();
                                       }
                                   })
                                   .setNegativeButton("取消", null)
                                   .create()
                                   .show();
                            break;
                        case 1:
                            ShareCompat.IntentBuilder
                                    //传入Activity
                                    .from(getActivity())
                                    //设置数据
                                    .setText("标题:" + mTodo.getmTitle() + "\n" + "备注:" + mTodo.getmBeizhu() + "\n" +
                                             "日期:" + TodoFragment.getDateStringYMDE(mTodo.getmDate()) + "\n" + "重要程度:" +
                                             mTodo.getmImportance())
                                    //设置选择器标题
                                    .setChooserTitle("分享")
                                    //分享数据的类型
                                    .setType("text/plain")
                                    //启动
                                    .startChooser();
                            break;
                        default:
                    }
                }
            });
            listDialog.show();
        }

        //删除item
        private void removeItem() {
            TodoLab.get(getActivity())
                   .removeTodo(mTodo);
            updateUI();
            mAdapter.notifyDataSetChanged();
        }

        void bind(Todo todo) {
            mTodo = todo;
            mTitleTV.setText(mTodo.getmTitle());
            mBeizhuTV.setText(mTodo.getmBeizhu() == null || mTodo.getmBeizhu()
                                                                 .isEmpty() ? "备注未填写" : mTodo.getmBeizhu());
            mImportant.setText(String.valueOf(mTodo.getmImportance()));
        }

        @Override
        public void onClick(View v) {
            Intent intent = TodoPagerActivity.newIntent(getActivity(), mTodo.getmId());
            startActivity(intent);
        }
    }

    private class TodoAdapter extends RecyclerView.Adapter<TodoHolder> {

        private List<Todo> mTodos;

        TodoAdapter(List<Todo> todos) {
            mTodos = todos;
        }

        @NonNull
        @Override
        public TodoHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new TodoHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull TodoHolder todoHolder, int i) {
            Todo todo = mTodos.get(i);
            dateArray.add(TodoFragment.getDateStringYMD(todo.getmDate()));
            todoHolder.bind(todo);
        }

        @Override
        public int getItemCount() {
            return mTodos.size();
        }

        void setmTodos(List<Todo> todos) {
            mTodos = todos;
        }
    }
}
