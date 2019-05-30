package com.lifeistech.camp.memo;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.VectorEnabledTintResources;

import io.realm.Realm;

public class DetailActivity extends AppCompatActivity {
    public Realm realm;

    public EditText titleText;
    public EditText contentText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        realm = Realm.getDefaultInstance();  //realm open

        titleText = (EditText) findViewById(R.id.titleEditText);
        contentText = (EditText) findViewById(R.id.contentEditText);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        realm.close();
    }

    public void showData(){
        final Memo memo = realm.where(Memo.class).equalTo("updateDate",getIntent().getStringExtra("updateDate")).findFirst();

//        titleText.setText(memo.title);
//        contentText.setText(memo.content);

    }

    public void update(View v){
        final Memo memo = realm.where(Memo.class).equalTo("updateDate",getIntent().getStringExtra("updateDate")).findFirst();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                /*memo.title = titleText.getText().toString();
                memo.content = contentText.getText().toString();
                */
                memo.yotei = titleText.getText().toString();
            }
        });
        finish();
    }
}