package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.InMemoryAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionsDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.ui.MainActivity;

public class PersistentExpenseManager extends ExpenseManager {
    private Context context;
    public PersistentExpenseManager(Context context){
        this.context=context;
        setup();
    }
    @Override
    public void setup(){
        DBHelper dbHelper=new DBHelper(context);
        TransactionDAO PersistentTransactionsDAO = new PersistentTransactionsDAO(context,dbHelper);
        setTransactionsDAO(PersistentTransactionsDAO);

        //AccountDAO inMemoryAccountDAO = new InMemoryAccountDAO();
        //setAccountsDAO(inMemoryAccountDAO);
        AccountDAO PersistentAccountDAO = new PersistentAccountDAO(context);
        PersistentAccountDAO.loadValues();
        setAccountsDAO(PersistentAccountDAO);

        // dummy data
        Account dummyAcct1 = new Account("12345A", "Yoda Bank", "Anakin Skywalker", 10000.0);
        Account dummyAcct2 = new Account("78945Z", "Clone BC", "Obi-Wan Kenobi", 80000.0);
        PersistentAccountDAO.addAccount(dummyAcct1);
        PersistentAccountDAO.addAccount(dummyAcct2);
    }
}
