package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.DBHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionsDAO extends SQLiteOpenHelper implements TransactionDAO {
    private final List<Transaction> transactions;
    private DBHelper helper;

    public PersistentTransactionsDAO(Context context, DBHelper helper) {
        super(context,"appDatabase",null,1);
        this.helper=helper;
        transactions = new LinkedList<>();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createStatement="create table transactions(transactionNo integer primary key autoincrement,accountNo text,expenseType integer,amount real,date text)";
        sqLiteDatabase.execSQL(createStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
        transactions.add(transaction);
        helper.insertTransactions(date,accountNo,expenseType,amount);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }

    public void loadValues() {
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from transactions",null);
        Transaction transaction;
        String accNo;
        ExpenseType expenseType;
        Double amount;
        Date date;
        while(cursor.moveToNext()){
            accNo=cursor.getString(1);
            expenseType=ExpenseType.values()[cursor.getInt(2)];
            amount=cursor.getDouble(3);
            try {
                date=new SimpleDateFormat("yyyy/MM/dd").parse(cursor.getString(4));
            } catch (ParseException e) {
                e.printStackTrace();
                break;
            }
            transaction=new Transaction(date,accNo,expenseType,amount);
            transactions.add(transaction);
        }
        db.close();
    }

}
