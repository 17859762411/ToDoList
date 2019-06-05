package com.android.lvtong.todolist;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.lvtong.todolist.menu.AboutActivity;
import com.android.lvtong.todolist.menu.SettingsActivity;

import java.util.Date;
import java.util.UUID;

import static android.Manifest.permission.VIBRATE;
import static android.content.Context.VIBRATOR_SERVICE;

public class TodoFragment extends Fragment {

    private static final String ARG_TODO_ID = "todo_id";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;

    private Todo mTodo;
    private EditText mTitleField;
    private EditText mBeizhuField;
    private Spinner mSpinner;
    private Button mDateButton;

    Boolean isSpinner = false;
    private Vibrator vibrator;

    public static TodoFragment newInstance(UUID todoID) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TODO_ID, todoID);

        TodoFragment fragment = new TodoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID todoId = (UUID) getArguments().getSerializable(ARG_TODO_ID);
        mTodo = TodoLab.get(getActivity()).getTodo(todoId);
    }

    @Override
    public void onPause() {
        super.onPause();
//        if (TextUtils.isEmpty(mBeizhuField.getText())){
//            mTodo.setmBeizhu("备注未填写");
//        }
        if (TextUtils.isEmpty(mTodo.getmTitle())){
            TodoLab.get(getActivity()).removeTodo(mTodo);
        }else {
            TodoLab.get(getActivity()).updateTodo(mTodo);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_todo, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setElevation(0);
        vibrator = (Vibrator)getActivity().getSystemService(VIBRATOR_SERVICE);

//        mAddButton = (Button)v.findViewById(R.id.btn_add);
//        mAddButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (TextUtils.isEmpty(mBeizhuField.getText())){
//                    mTodo.setmBeizhu("备注未填写");
//                }
//                if (TextUtils.isEmpty(mTitleField.getText())){
//                    Toast.makeText(getActivity(), "标题为空！", Toast.LENGTH_SHORT).show();
//                }else {
//                    Toast.makeText(getActivity(), "添加成功", Toast.LENGTH_SHORT).show();
//                    getActivity().finish();
//                }
//            }
//        });

        //时间
        mDateButton = (Button)v.findViewById(R.id.button_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mTodo.getmDate());
                dialog.setTargetFragment(TodoFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSpinner = (Spinner)v.findViewById(R.id.spinner);
        mSpinner.setDropDownVerticalOffset(100);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isSpinner){
                    String string = (String) mSpinner.getSelectedItem();
                    mTodo.setmImportance(Integer.valueOf(string));
                }
                isSpinner=true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //标题
        mTitleField = (EditText)v.findViewById(R.id.editText_title);
        mTitleField.setText(mTodo.getmTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTodo.setmTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //备注
        mBeizhuField = (EditText)v.findViewById(R.id.ed_beizhu);
        mBeizhuField.setText(mTodo.getmBeizhu());
        mBeizhuField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTodo.setmBeizhu(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mBeizhuField.getText() == null){
                    mBeizhuField.setError("不可为空");
                }
            }
        });

        return v;

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
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_ok:
//                if (TextUtils.isEmpty(mBeizhuField.getText())){
//                    mTodo.setmBeizhu("备注未填写");
//                }
                if (TextUtils.isEmpty(mTitleField.getText())){
                    mTitleField.setError("不可为空");
                    Toast.makeText(getActivity(), "标题为空！", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(), "保存成功！", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mTodo.setmDate(date);
            updateDate();
        }
    }

    private void updateDate() {
        if (mTodo.getmDate() != null){
            //日期格式化
            mDateButton.setText(getDateString(mTodo.getmDate()));
        }
    }

    /**将date对象转为年月日星期格式string
     *
     * @param date Date时间戳
     * @return 返回string
     */
    public static String getDateString(Date date) {
        CharSequence cs = "yyyy, MMMM dd,EEEE";
        //几年,月份,几号,星期
        CharSequence re = DateFormat.format(cs,date);
        return re.toString();
    }
}
