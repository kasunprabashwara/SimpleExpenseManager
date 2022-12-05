package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;



public class PersistentAccountDAO extends SQLiteOpenHelper implements AccountDAO {
    private final Map<String, Account> accounts;    //temp variable
    public PersistentAccountDAO(Context context) {
        super(context,"ppDatabase",null, 1);
        this.accounts = new HashMap<>(); //these are temp step

    }

    @Override
    public void loadValues() {
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
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createStatement="create table account(accountNo text primary key, bankName text, accountHolderName text,balance real)";
        sqLiteDatabase.execSQL(createStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    @Override
    public List<String> getAccountNumbersList() {
        return new ArrayList<>(accounts.keySet());
    }

    @Override
    public List<Account> getAccountsList() {
        return new ArrayList<>(accounts.values());
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        if (accounts.containsKey(accountNo)) {
            return accounts.get(accountNo);
        }
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {
        accounts.put(account.getAccountNo(), account);
        SQLiteDatabase db=getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("accountNo",account.getAccountNo());
        cv.put("bankName",account.getBankName());
        cv.put("accountHolderName",account.getAccountHolderName());
        cv.put("balance",account.getBalance());
        db.insert("account",null,cv);
        db.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        if (!accounts.containsKey(accountNo)) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        accounts.remove(accountNo);
        SQLiteDatabase db=getWritableDatabase();
        String where="accountNo"+accountNo;
        db.delete("account",where,null);
        db.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        if (!accounts.containsKey(accountNo)) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        Account account = accounts.get(accountNo);
        // specific implementation based on the transaction type
        switch (expenseType) {
            case EXPENSE:
                account.setBalance(account.getBalance() - amount);
                break;
            case INCOME:
                account.setBalance(account.getBalance() + amount);
                break;
        }
        accounts.put(accountNo, account);
        SQLiteDatabase db=getWritableDatabase();
        ContentValues cv=new ContentValues();
        String where="accountNo="+accountNo;
        cv.put("balance",account.getBalance());
        db.update("account",cv,where,null);
        db.close();
    }

}
