package com.hfad.messenger2021.LocalDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.hfad.messenger2021.Objects.User;

@Dao
public interface LocalDatabaseDao {
    @Insert
    void insertUser(User user);

    @Query("SELECT * FROM User")
    LiveData<User> getUser();
}
