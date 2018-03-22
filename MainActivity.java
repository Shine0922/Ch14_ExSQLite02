package com.example.win7.exsqlite02;

         import android.database.Cursor;
         import android.database.SQLException;
         import android.database.sqlite.SQLiteDatabase;
         import android.support.v7.app.AppCompatActivity;
         import android.os.Bundle;
         import android.view.View;
         import android.widget.AdapterView;
         import android.widget.Button;
         import android.widget.EditText;
         import android.widget.ListView;
         import android.widget.SimpleCursorAdapter;
         import android.widget.Toast;

         import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private SQLiteDatabase db =null;
    //  建立 table01 資料表
    private final static
    String CREATE_TABLE = "CREATE TABLE table01(_id INTEGER PRIMARY KEY,name TEXT,price INTERGER)";

    ListView listView01;
    Button btnSearch,btnSearchAll;
    EditText edtID;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  取得元件
        edtID = (EditText)findViewById(R.id.edtID);
        btnSearch = (Button)findViewById(R.id.btnSearch);
        btnSearchAll = (Button)findViewById(R.id.btnSearchAll);
        listView01 = (ListView)findViewById(R.id.listView01);

        //  設定偵聽
        btnSearch.setOnClickListener(btnListener);
        btnSearchAll.setOnClickListener(btnListener);
        listView01.setOnItemClickListener(listView01Listener);

        //  建立資料庫 如果已存在將之開啟
        db = openOrCreateDatabase("db1.db",MODE_PRIVATE,null);
        try
        {
            db.execSQL(CREATE_TABLE);   //  建立資料表
            db.execSQL("INSERT INTO table01 (name,price) values(' 點數50點 ',50)");         // 新增資料
            db.execSQL("INSERT INTO table01 (name,price) values(' 點數100點 ',100)");
            db.execSQL("INSERT INTO table01 (name,price) values(' 點數150點 ',150)");
            db.execSQL("INSERT INTO table01 (name,price) values(' 點數300點 ',300)");
        }
        catch(Exception e)
        {

        }
        cursor = getAll();      //  查詢所有資料
        UpdateAdapter(cursor);  //  載入資料表至 ListView 中
    }

    private ListView.OnItemClickListener listView01Listener = new ListView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            cursor.moveToPosition(position);
            Cursor c = get(id);
            String s = "id=" + id + "\r\n" + "name=" + c.getString(1) + "\r\n" + "price=" + c.getInt(2);
            Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();

        }
    };
    @Override
    protected  void onDestroy()
    {
        super.onDestroy();
        db.close();         //  關閉資料庫
    }

    private Button.OnClickListener btnListener = new Button.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            try
            {
                switch(v.getId())
                {
                    case R.id.btnSearch:    //  查詢單筆資料
                    {
                        long id = Integer.parseInt(edtID.getText().toString());
                        cursor = get(id);
                        UpdateAdapter(cursor);  //  載入資料表至 ListView
                        break;
                    }
                    case R.id.btnSearchAll:     //  查詢全部

                        cursor = getAll();      //  查詢所有資料
                        UpdateAdapter(cursor);  //  載入資料表至 ListView
                        break;
                }
            }
            catch(Exception err)
            {
                Toast.makeText(getApplicationContext()," 查無此資料 ",Toast.LENGTH_LONG).show();
            }
        }
    };

    public void UpdateAdapter (Cursor cursor)
    {
        if(cursor != null && cursor.getCount() >=0 )
        {
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_2,              //  包含兩個資料項
                    cursor,                                          //  資料庫的 Cursor 物件
                    new String[]{"pname","price"},                  //  pname、price 欄位
                    new int[]{android.R.id.text1,android.R.id.text2}, //    與pname、price對應的元件
                    0);                                          //   adapter 行為最佳化

            listView01.setAdapter(adapter);     //  將adapter 增加到 listView01 中
        }
    }

    public Cursor getAll()  //  查詢所有資料
    {
        Cursor cursor = db.rawQuery("SELECT _id,_id||'.'||name pname,price FROM table01",null);
        return cursor;  //  傳回 _id、panme、price 欄位
    }

    public Cursor get(long rowId) throws SQLException //  查詢指定 ID 的資料
    {
        Cursor cursor = db.rawQuery("SELECT _id,_id||'.'||name pname,price FROM table01 WHERE _id="+rowId,null);
        if (cursor.getCount()>0)
            cursor.moveToFirst();
        else
            Toast.makeText(getApplicationContext()," 查無此資料 ! ",Toast.LENGTH_SHORT).show();
        return cursor;      //  傳回 _id pname price 欄位
    }
}
