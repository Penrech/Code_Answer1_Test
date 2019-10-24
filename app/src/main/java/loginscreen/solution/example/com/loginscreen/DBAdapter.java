package loginscreen.solution.example.com.loginscreen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

class DBAdapter {

    static final String USERS_TABLE = "usuarios";

    DBAdapter(Context context) {
        String DATABASE_NAME = "users";
        int DATABASE_VERSION = 1;
        dbHelper = new DBHelper(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    private SQLiteDatabase database;
    private DBHelper dbHelper;

    void open(){
        database = dbHelper.getWritableDatabase();
    }

    void close(){
        database.close();
    }

    User insertNewUser(String email, String name, String password, String number){
        ContentValues register = new ContentValues();
        register.put("email",email);
        register.put("name",name);
        register.put("password",password);
        register.put("phone",number);

        try {
            database.insertOrThrow(USERS_TABLE,null,register);
        } catch (Exception e){
            e.printStackTrace();
            return null;

        }

        return new User(email,password,number,name);
    }

    User logInUser(String email, String password){
        String query = "SELECT * FROM " + USERS_TABLE + " WHERE email = '" + email + "' AND password = '" + password +"'";
        Cursor cursor = database.rawQuery(query,null);
        User user = null;

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            user = new User(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3));
        }

        cursor.close();
        return user;
    }

}
