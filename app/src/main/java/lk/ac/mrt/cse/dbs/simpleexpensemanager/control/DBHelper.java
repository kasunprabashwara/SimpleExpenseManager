package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DBHelper extends SQLiteOpenHelper {
    private final SimpleDateFormat format;
    public DBHelper(@Nullable Context context) {
        super(context,"appDatabase",null,1);
        this.format=new SimpleDateFormat("yyyy/MM/dd");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createAccount="create table account(accountNo text primary key, bankName text, accountHolderName text,balance real)";
        sqLiteDatabase.execSQL(createAccount);
        String createTransactions="create table transactions(transactionNo integer primary key autoincrement,accountNo text,expenseType integer,amount real,date text,foreign key(accountNo) references accounts(AccountNo))";
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
        cv.put("date",format.format(date));
        cv.put("amount",amount);
        db.insert("transactions",null,cv);
        db.close();
    }
    public List<Transaction> getInitialTransactions(){
        List<Transaction> transactions=new LinkedList<>();
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
                date=format.parse(cursor.getString(4));
            } catch (ParseException e) {
                e.printStackTrace();
                break;
            }
            transaction=new Transaction(date,accNo,expenseType,amount);
            transactions.add(transaction);
        }
        cursor.close();
        db.close();
        return transactions;
    }

    public void insertAccounts(Account account){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("accountNo",account.getAccountNo());
        cv.put("bankName",account.getBankName());
        cv.put("accountHolderName",account.getAccountHolderName());
        cv.put("balance",account.getBalance());
        db.insert("account",null,cv);
        db.close();
    }

    public void deleteAccounts(String accountNo){
        SQLiteDatabase db=getWritableDatabase();
        String where="accountNo"+"'"+accountNo+"'";
        db.delete("account",where,null);
        db.close();
    }

    public void updateAccounts(Account account){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues cv=new ContentValues();
        String where="accountNo="+"'"+account.getAccountNo()+"'";
        cv.put("balance",account.getBalance());
        db.update("account",cv,where,null);
        db.close();
    }

    public Map<String,Account> getInitialAccounts(){
        Map<String,Account> accounts=new HashMap<>();
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from account",null);
        Account acc;
        String accNo;
        String bankName;
        String holName;
        Double bal;
        while(cursor.moveToNext()){
            accNo=cursor.getString(0);
            bankName=cursor.getString(1);
            holName=cursor.getString(2);
            bal=cursor.getDouble(3);
            acc=new Account(accNo,bankName,holName,bal);
            accounts.put(accNo,acc);
        }
        cursor.close();
        db.close();
        return accounts;
    }
}
