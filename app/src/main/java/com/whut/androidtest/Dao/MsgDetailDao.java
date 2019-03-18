package com.whut.androidtest.Dao;



import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.whut.androidtest.Bean.MsgDetailBean;

import java.util.List;



@Dao
public interface MsgDetailDao {
    @Query("SELECT * FROM Msgs")
    public List<MsgDetailBean> getAllMsg();

    @Insert
    public void insertMsg(MsgDetailBean... msgDetailBeans);

    @Update
    public void updateMsg(MsgDetailBean... msgDetailBeans);
}
