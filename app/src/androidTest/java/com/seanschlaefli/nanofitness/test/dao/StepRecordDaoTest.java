package com.seanschlaefli.nanofitness.test.dao;

import android.content.Context;

import com.seanschlaefli.nanofitness.dao.StepRecordDao;
import com.seanschlaefli.nanofitness.database.AppDatabase;
import com.seanschlaefli.nanofitness.model.StepRecord;
import com.seanschlaefli.nanofitness.test.TestUtil;

import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;


@RunWith(AndroidJUnit4.class)
public class StepRecordDaoTest {

    private static final int expectedStepRecord = 1000;
    private static final int expectedRecordTime = 100;

    private StepRecordDao stepRecordDao;
    private AppDatabase testDatabase;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        testDatabase = Room.inMemoryDatabaseBuilder(
                context,
                AppDatabase.class
        ).build();
        stepRecordDao = testDatabase.stepRecordDao();
    }

    @After
    public void closeDb() {
        testDatabase.close();
    }

    @Test
    public void writeAndReadSingleStepRecord() {
        int workoutId = getWorkoutId();
        StepRecord record = TestUtil.createStepRecord(workoutId, expectedStepRecord, expectedRecordTime);
        int id = (int) stepRecordDao.insert(record);
        List<StepRecord> records = stepRecordDao.getAll();
        assertEquals(records.size(), 1);
        assertEquals(records.get(0).recordStep, expectedStepRecord);
        assertEquals(records.get(0).recordTime, expectedRecordTime);
        StepRecord result = stepRecordDao.loadById(id);
        assertEquals(result.recordStep, expectedStepRecord);
        assertEquals(result.recordTime, expectedRecordTime);
    }

    @Test
    public void deleteSingleStepRecord() {
        int workoutId = getWorkoutId();
        StepRecord record = TestUtil.createStepRecord(workoutId, expectedStepRecord, expectedRecordTime);
        record.id = (int) stepRecordDao.insert(record);
        stepRecordDao.delete(record);
        List<StepRecord> records = stepRecordDao.getAll();
        assertEquals(records.size(), 0);
    }

    @Test
    public void updateSingleStepRecord() {
        int workoutId = getWorkoutId();
        StepRecord record = TestUtil.createStepRecord(workoutId, expectedStepRecord, expectedRecordTime);
        int id = (int) stepRecordDao.insert(record);
        record.id = id;
        record.recordStep = expectedStepRecord + 10;
        stepRecordDao.update(record);
        StepRecord updated = stepRecordDao.loadById(id);
        assertEquals(updated.id, id);
        assertEquals(updated.recordStep, expectedStepRecord + 10);
        assertEquals(updated.recordTime, expectedRecordTime);
        assertEquals(updated.workoutId, workoutId);
    }

    private int getWorkoutId() {
        return (int) testDatabase.workoutDao().insert(TestUtil.createWorkout(0));
    }

}
