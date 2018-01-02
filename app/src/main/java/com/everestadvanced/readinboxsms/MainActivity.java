package com.everestadvanced.readinboxsms;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import static android.R.id.message;

public class MainActivity extends AppCompatActivity {

    public String ServiceProvider="RM-WAYSMS";
    public String MY_PREFS_NAME="SMS_ID";
    public String address;
    public int SmsMessageId;
    SharedPreferences.Editor editor;
    public ArrayList sms;

    static final int READ_BLOCK_SIZE = 100;
    public String texFileName="SmsId.txt";
    public String DirectroyName="SmsFile";
    public  OutputStreamWriter outputWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
//        GetSmsData();
//        ShowSmsId();


        ///////store data in txt file
        GetSmsDataForSotrge();
        ReadStoreSmsId();
    }

    public void GetSmsData() {

        final Uri SMS_INBOX = Uri.parse("content://sms/inbox");

        //Retrieves all SMS (if you want only unread SMS, put "read = 0" for the 3rd parameter)
        Cursor cursor = getContentResolver().query(SMS_INBOX, null, "read=0", null, null);
        sms = new ArrayList();

        //Get all lines
        while (cursor.moveToNext())
        {
            //Gets the SMS information
            address = cursor.getString(cursor.getColumnIndex("address"));
            String person = cursor.getString(cursor.getColumnIndex("person"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String protocol = cursor.getString(cursor.getColumnIndex("protocol"));
            String read = cursor.getString(cursor.getColumnIndex("read"));
            String status = cursor.getString(cursor.getColumnIndex("status"));
            String type = cursor.getString(cursor.getColumnIndex("type"));
            String subject = cursor.getString(cursor.getColumnIndex("subject"));
            String body = cursor.getString(cursor.getColumnIndex("body"));
            SmsMessageId =Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id")));

            sms.add(address+"\n"+body);

            if(address.equals(ServiceProvider))
            {
                int i=sms.size();
                editor.putString("id" + i, String.valueOf(SmsMessageId));
                editor.commit();

            }

        }
    }

    public void ShowSmsId() {

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        for(int j =1;j<sms.size()+1;j++)
        {
            String smsIds = prefs.getString("id"+j, null);

            if (smsIds != null)
            {
                Toast.makeText(getApplicationContext(),"Message Id:"+String.valueOf(smsIds),Toast.LENGTH_LONG).show();
            }
        }
    }

    public void GetSmsDataForSotrge() {

        final Uri SMS_INBOX = Uri.parse("content://sms/inbox");

        //Retrieves all SMS (if you want only unread SMS, put "read = 0" for the 3rd parameter)
        Cursor cursor = getContentResolver().query(SMS_INBOX, null, "read=0", null, null);
        sms = new ArrayList();


        try
        {
            FileOutputStream fileout= null;
            File root = new File(Environment.getExternalStorageDirectory(), DirectroyName);
            if (!root.exists())
            {
                root.mkdirs();
            }
            fileout = openFileOutput(DirectroyName+texFileName, MODE_PRIVATE);
            outputWriter=new OutputStreamWriter(fileout);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }


        //Get all lines
        while (cursor.moveToNext())
        {
            //Gets the SMS information
            address = cursor.getString(cursor.getColumnIndex("address"));
            String person = cursor.getString(cursor.getColumnIndex("person"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String protocol = cursor.getString(cursor.getColumnIndex("protocol"));
            String read = cursor.getString(cursor.getColumnIndex("read"));
            String status = cursor.getString(cursor.getColumnIndex("status"));
            String type = cursor.getString(cursor.getColumnIndex("type"));
            String subject = cursor.getString(cursor.getColumnIndex("subject"));
            String body = cursor.getString(cursor.getColumnIndex("body"));
            SmsMessageId =Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id")));

            sms.add(address+"\n"+body);

            if(address.equals(ServiceProvider))
            {
                try
                {
                    outputWriter.append(String.valueOf(SmsMessageId)+"\n");
                    outputWriter.flush();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }

        }
        try {
            outputWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ReadStoreSmsId() {

        try
        {
            FileInputStream fileIn=openFileInput(DirectroyName+texFileName);
            InputStreamReader InputRead= new InputStreamReader(fileIn);

            char[] inputBuffer= new char[READ_BLOCK_SIZE];
            String s="";
            int charRead;

            while ((charRead=InputRead.read(inputBuffer))>0)
            {
                // char to string conversion
                String readstring=String.copyValueOf(inputBuffer,0,charRead);
                s +=readstring;
            }
            InputRead.close();
            Toast.makeText(getBaseContext(),s,Toast.LENGTH_SHORT).show();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
