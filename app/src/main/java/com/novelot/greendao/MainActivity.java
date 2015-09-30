package com.novelot.greendao;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.novelot.greendao.dao.DaoMaster;
import com.novelot.greendao.dao.DaoSession;
import com.novelot.greendao.dao.DownloadTask;
import com.novelot.greendao.dao.DownloadTaskDao;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv;
    private Button btnCreateDb;
    private Button btnCreateTable;
    private Button btnInsert;
    private Button btnDelete;

    private void assignViews() {
        tv = (TextView) findViewById(R.id.tv);
        btnCreateDb = (Button) findViewById(R.id.btnCreateDb);
        btnCreateTable = (Button) findViewById(R.id.btnCreateTable);
        btnInsert = (Button) findViewById(R.id.btnInsert);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        //
        btnCreateDb.setOnClickListener(this);
        btnCreateTable.setOnClickListener(this);
        btnInsert.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignViews();

//        DaoMaster.DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(this, "db_download", null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCreateDb:
                break;
            case R.id.btnCreateTable:
                break;
            case R.id.btnInsert: {
                DownloadTask task = new DownloadTask();
                task.setUrl("http://www.novelot.com/test.mp3");
                task.setLocalPath("/sdcard/download/test.mp3");

                //
                DaoMaster.DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(this, "db_download", null);
                SQLiteDatabase db = openHelper.getWritableDatabase();
                DaoMaster daoMaster = new DaoMaster(db);
                DaoSession session = daoMaster.newSession();
                DownloadTaskDao taskDao = session.getDownloadTaskDao();
                taskDao.insert(task);
                //
                task.getId();
                //
                StringBuilder sb = new StringBuilder();
                List<DownloadTask> tasks = taskDao.loadAll();
                if (tasks != null) {
                    int size = tasks.size();
                    if (size > 0) {
                        for (int i = 0; i < size; i++) {
                            DownloadTask t = tasks.get(i);
                            sb.append(t.getId()).append(":").append(t.getUrl()).append(":").append(t.getLocalPath()).append("\n\r");
                        }
                    } else {
                        sb.append("db is empty");
                    }
                }

                tv.setText(sb.toString());
            }
            break;
            case R.id.btnDelete:
                DaoMaster.DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(this, "db_download", null);
                SQLiteDatabase db = openHelper.getWritableDatabase();
                DaoMaster daoMaster = new DaoMaster(db);
                DaoSession session = daoMaster.newSession();
                DownloadTaskDao taskDao = session.getDownloadTaskDao();
                taskDao.deleteByKey(1L);
                //
                StringBuilder sb = new StringBuilder();
                List<DownloadTask> tasks = taskDao.loadAll();
                if (tasks != null) {
                    int size = tasks.size();
                    if (size > 0) {
                        for (int i = 0; i < size; i++) {
                            DownloadTask t = tasks.get(i);
                            sb.append(t.getId()).append(":").append(t.getUrl()).append(":").append(t.getLocalPath()).append("\n\r");
                        }
                    } else {
                        sb.append("db is empty");
                    }
                }

                tv.setText(sb.toString());
                break;
        }
    }
}
