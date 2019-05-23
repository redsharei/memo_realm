package com.lifeistech.camp.memo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends FragmentActivity
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    public Realm realm;
    public ListView listView;
    int pos = 0;
    public TextView textView1, textView2;
    String date_str = "";
    String str1 = "";
    String str2 = "";
    int hour1 = 0;
    int hour2 = 0;
    int minute1 = 0;
    int minute2 = 0;
    int hour_sum = 0;
    int minute_sum = 0;
    int free_sum = 0;
    static final int num = 1;
    private boolean isFirst = true;
    private boolean isFirst2 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        realm = Realm.getDefaultInstance();

        listView = (ListView) findViewById(R.id.listView);
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);


        //clicked 変更をするところ
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Memo memo = (Memo) parent.getItemAtPosition(position);      //position番目を持ってくる
                Intent intent = new Intent(MainActivity.this, DetailActivity.class); //遷移
                intent.putExtra("updateDate", memo.updateDate);
                startActivity(intent);
            }
        });

        //長押しの時、画面からもレルムからも削除
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter adapter = (ArrayAdapter) listView.getAdapter();

                Memo memo = (Memo) parent.getItemAtPosition(position);
                adapter.remove(memo);

                pos = position;
                deleteMemo();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setMemoList();
        /*if(isFirst2){

            Calendar calendar = Calendar.getInstance();

        }else{

        }
*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public void create(View v) {
        Intent intent = new Intent(this, CreateActivity.class);
        startActivity(intent);
    }

    //realmからデータを取ってきている
/*
    @NonNull
    protected RealmResults<Memo> results (Realm realm){
        return realm.where(Memo.class).equalTo("updateDate",true).findAll();
    }*/

    //dateでデータを絞りたい！！where
    //選択された日付のデータのみ
    public void setMemoList(String str) {

        RealmResults<Memo> results = realm.where(Memo.class).equalTo("updateDate",str).findAll();
        List<Memo> items = realm.copyFromRealm(results);

        MemoAdapter adapter = new MemoAdapter(this, R.layout.layout_item_memo, items);

        listView.setAdapter(adapter);
    }

    //realmから削除
    public void deleteMemo() {
        final RealmResults<Memo> results = realm.where(Memo.class).findAll();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results.deleteFromRealm(pos);
                //results.deleteLastFromRealm();
            }
        });
        List<Memo> items = realm.copyFromRealm(results);

        MemoAdapter adapter = new MemoAdapter(this, R.layout.layout_item_memo, items);

        listView.setAdapter(adapter);
    }


    //PickDate
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        //saveはしない！！
//日の保存、表示  おｋされた後
        date_str = String.format(Locale.US, "%d/%d/%d", year, monthOfYear + 1, dayOfMonth);





        setMemoList(date_str);
        textView1.setText(date_str);
        //textView2.setText(String.valueOf(memo.free_sum));  まだデータがない日はデータがない。

        //finish();
        //startActivity(getIntent());
    }


    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePick();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    //PickTime
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        int[][] timec = new int[num][2];

        if (isFirst) {
            hour1 = hourOfDay;
            minute1 = minute;

            str1 = String.valueOf(String.format("%02d", hour1)) + ":" + String.valueOf(String.format("%02d", minute1));
            DialogFragment newFragment = new TimePick();
            newFragment.show(getSupportFragmentManager(), "timePicker");


            isFirst = false;
        } else {
            hour2 = hourOfDay;
            minute2 = minute;

            int hourc = hour2 - hour1;
            int minutec = minute2 - minute1;

            if (minutec < 0) {
                minutec = minutec + 60;
                hourc = hourc - 1;
            } else if (minutec >= 60) {
                minutec = minutec - 60;
                hourc = hourc + 1;
            }

            str2 = String.valueOf(String.format("%02d", hourOfDay)) + ":" + String.valueOf(String.format("%02d", minute));
            //hour_sum += hourc;
            //minute_sum += minutec;
            int free_part = hourc * 60 + minutec;



           final Memo memo = realm.where(Memo.class).equalTo("updateDate",date_str).findFirst();
            try {
                if(memo.updateDate != null) {


                    //memo.free_sum  realm更新
                    free_sum = free_part+memo.free_sum;
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            memo.free_sum = free_sum;
                        }
                    });
                    textView2.setText(String.valueOf(free_sum));
                    Log.d("memo_free",String.valueOf(memo.free_sum));
                    Log.d("free_sum",String.valueOf(free_sum));
                    Log.d("date",date_str);
                    Log.d("free",String.valueOf(memo.free));

                } else {
                    throw new NullPointerException();
                }
            } catch(NullPointerException e){
                e.printStackTrace();
            }


            isFirst = true;
            String time_str_sum = String.valueOf(String.format("%02d", hour_sum)) + ":" + String.valueOf(String.format("%02d", minute_sum));

            save(str1, date_str, str2, free_part, free_sum);
//            final Memo memo = realm.where(Memo.class).equalTo("time_str_sum",getIntent().getStringExtra("time_str_sum")).findFirst();
//            textView2.setText(memo.free_sum);
             //finish();
             //startActivity(getIntent());
        }


        //////時間取って来る
//realmに日,時間を保存
        /*
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                Memo memo = realm.createObject(Memo.class);
                memo.date = date_str;
            }
        });*/
    }

    public void showTimePickerDialog1(View v) {
        DialogFragment newFragment = new TimePick();
        newFragment.show(getSupportFragmentManager(), "timePicker1");
    }
/*
    public void showTimePickerDialog2(View v) {
        DialogFragment newFragment = new TimePick();
        newFragment.show(getSupportFragmentManager(), "timePicker2");
    }
*/

    //   public void save(final String title, final String updateDate, final String content) {
    public void save(final String title, final String updateDate, final String content, final int free, final int free_sum) {

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                Memo memo = realm.createObject(Memo.class);
                memo.title = title;

                memo.updateDate = updateDate;
                memo.content = content;
                memo.free = free;
                memo.free_sum = free_sum;
            }
        });

    }

}


//next->onResume処理

//でーた消したときに、free_timeも引かねば!!
//listviewクリックしたときに、timepickでデータ変更

// S start_time | S date |S end_time | int free (その時間) | int free_sum (その日)

//free timeたちをStringでなくint(分)で保存してdone!、表示するときに商と余りで計算すればok
//保存したでーたをTextViewでshowしたい

//next->sort done!
//realm 要素一つ追加 done!
//timepickで時間を取って来る done!
//datepickedのとき、データを取得したい。whereでデータを絞るところから。done!
//日と時間の紐づけ maybe done!
//日付を選んで、その日の時間が表示される。
