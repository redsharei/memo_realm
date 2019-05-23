package com.lifeistech.camp.memo;

import io.realm.RealmObject;

public class Memo extends RealmObject {
    public String title;
    public String updateDate;
    public String content;
    public int free;
    public int free_sum;


}
