package com.lifeistech.camp.memo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    public Realm realm;
    public ListView listView;
    int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        realm = Realm.getDefaultInstance();
        listView = (ListView) findViewById(R.id.listView);

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
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setMemoList();
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

    public void setMemoList() {
        RealmResults<Memo> results = realm.where(Memo.class).findAll();
        results = results.sort("title");
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

}

//next->sort done!