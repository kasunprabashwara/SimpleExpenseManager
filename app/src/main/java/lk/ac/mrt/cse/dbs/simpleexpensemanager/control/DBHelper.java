package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.util.Date;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(@Nullable Context context) {
        super(context,"appDatabase",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createAccount="create table account(accountNo text primary key, bankName text, accountHolderName text,balance real)";
        sqLiteDatabase.execSQL(createAccount);
        String createTransactions="create table transactions(transactionNo integer primary key autoincrement,accountNo text,expenseType integer,amount real,date text)";
        sqLiteDatabase.execSQL(createTransactions);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insertTransactions(Date date, String accountNo, ExpenseType expenseType, double amount){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("accountNo",accountNo);
        cv.put("expenseType",expenseType.ordinal());
        cv.put("date",date.toString());
        cv.put("amount",amount);
        db.insert("transactions",null,cv);
        db.close();
    }
}
