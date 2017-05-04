package com.example.administrator.richeditor;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import jp.wasabeef.richeditor.RichEditor;

public class MainActivity extends AppCompatActivity {

    private RichEditor mEditor;
    private Button btn_insert;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //创建一个新的数据库,新的数据表
        db = openOrCreateDatabase("NoteBook",MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS info (content varchar(50))");
        intiView();
        Cursor cursor = db.rawQuery("select * from info", null);
        if (cursor != null) {
            cursor.moveToLast();
            String content = cursor.getString(cursor.getColumnIndex("content"));
            mEditor.setHtml(content);
            cursor.close();
        }
    }

    private void intiView() {
        mEditor = (RichEditor) findViewById(R.id.editor);
        btn_insert = (Button) findViewById(R.id.btn_insert);
        mEditor.setEditorHeight(200);//起始编辑设置高度
        mEditor.setEditorFontSize(22);//设置字体大小
        mEditor.setEditorFontColor(Color.RED);//设置字体颜色
        mEditor.setBold();//设置粗体
        mEditor.setItalic();//设置斜体
/*      mEditor.requestFocus();
        mEditor.requestFocusFromTouch();*/
        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1001);
            }
        });
        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                Log.d("AAA", text);//在 这里存储
                db.execSQL("insert into info(content) values('" + text + "')");
            }
        });
    }

    //获取到图片的路径,也可以是网络图片的路径
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                final String picturePath = cursor.getString(columnIndex);
                cursor.close();
                Log.d("AAA", picturePath);
                //自动获取输入焦点
                mEditor.requestFocus();
                mEditor.requestFocusFromTouch();
                mEditor.insertImage(picturePath, "image");
                Log.d("AAA", "onActivityResult: "+mEditor.getHtml());
                //db.execSQL("insert into info(content) values('" + mEditor.getHtml() + "')");
            }
        }
    }
}
