package com.android.lvtong.todolist;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class TodoFragment extends Fragment {

    private static final String ARG_TODO_ID = "todo_id";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;
    Boolean isSpinner = false;
    private Todo mTodo;
    private EditText mTitleField;
    private EditText mBeizhuField;
    private Button mDateButton;
    private Button mImportanceButton;

    public static TodoFragment newInstance(UUID todoID) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TODO_ID, todoID);

        TodoFragment fragment = new TodoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 将date对象转为年月日格式string
     *
     * @param date Date时间戳
     * @return 返回string
     */
    public static String getDateStringYMD(Date date) {
        CharSequence cs = "yyyy, MMMM dd";
        //几年,月份,几号
        CharSequence re = DateFormat.format(cs, date);
        return re.toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mTodo.setmDate(date);
            updateDate();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID todoId = null;
        if (getArguments() != null) {
            todoId = (UUID) getArguments().getSerializable(ARG_TODO_ID);
        }
        mTodo = TodoLab.get(getActivity())
                       .getTodo(todoId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_todo, container, false);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar())
               .setElevation(0);

        //时间
        mDateButton = v.findViewById(R.id.button_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mTodo.getmDate());
                dialog.setTargetFragment(TodoFragment.this, REQUEST_DATE);
                if (manager != null) {
                    dialog.show(manager, DIALOG_DATE);
                }
            }
        });
        //重要程度选择
        mImportanceButton = v.findViewById(R.id.button_importance);
        switch (mTodo.getmImportance()) {
            case 0:
                mImportanceButton.setText("一般：0");
                break;
            case 1:
                mImportanceButton.setText("不重要：1");
                break;
            case 2:
                mImportanceButton.setText("重要：2");
                break;
            default:
        }
        mImportanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListDialog();
            }
        });
        //标题
        mTitleField = v.findViewById(R.id.editText_title);
        mTitleField.setText(mTodo.getmTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //beforeTextChanged
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTodo.setmTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //afterTextChanged
            }
        });
        //备注
        mBeizhuField = v.findViewById(R.id.ed_beizhu);
        mBeizhuField.setText(mTodo.getmBeizhu());
        mBeizhuField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //beforeTextChanged
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTodo.setmBeizhu(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mBeizhuField.getText() == null) {
                    mBeizhuField.setError("不可为空");
                }
            }
        });
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (TextUtils.isEmpty(mTodo.getmTitle())) {
            TodoLab.get(getActivity())
                   .removeTodo(mTodo);
        } else {
            TodoLab.get(getActivity())
                   .updateTodo(mTodo);
        }
    }

    /**
     * menu部分
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_ok, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_ok) {
            if (TextUtils.isEmpty(mTitleField.getText())) {
                mTitleField.setError("不可为空");
                Toast.makeText(getActivity(), "标题为空！", Toast.LENGTH_SHORT)
                     .show();
            } else {
                Toast.makeText(getActivity(), "保存成功！", Toast.LENGTH_SHORT)
                     .show();
                Objects.requireNonNull(getActivity())
                       .finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 列表Dialog
     */
    public void showListDialog() {
        final String[] items = {"一般：0", "不重要：1", "重要：2"};
        AlertDialog.Builder listDialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        listDialog.setTitle("请选择重要程度(默认为一般)：");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTodo.setmImportance(which);
                mImportanceButton.setText(items[which]);
            }
        });
        listDialog.show();
    }

    private void updateDate() {
        if (mTodo.getmDate() != null) {
            //日期格式化
            mDateButton.setText(getDateStringYMDE(mTodo.getmDate()));
        }
    }

    /**
     * 将date对象转为年月日星期格式string
     *
     * @param date Date时间戳
     * @return 返回string
     */
    public static String getDateStringYMDE(Date date) {
        CharSequence cs = "yyyy, MMMM dd,EEEE";
        //几年,月份,几号,星期
        CharSequence re = DateFormat.format(cs, date);
        return re.toString();
    }
}
