package com.whut.androidtest;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.whut.androidtest.Bean.MsgDetailBean;
import com.whut.androidtest.Dao.MsgDetailDao;


@Database(entities = MsgDetailBean.class, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DB_NAME = "MsgDatabase";
    private static volatile AppDatabase instance;

    static synchronized AppDatabase getInstance(Context context){
        if(instance==null){
            instance = create(context);
        }
        return instance;
    }
    private static AppDatabase create(final Context context){
        return Room.databaseBuilder(context,
                AppDatabase.class,
                DB_NAME).build();
    }
    public abstract MsgDetailDao msgDetailDao();
}
