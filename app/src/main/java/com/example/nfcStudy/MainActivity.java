package com.example.nfcStudy;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.icu.text.SimpleDateFormat;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;
import android.util.Log;


import android.nfc.tech.MifareClassic;
import android.widget.TextView;


import com.example.nfcstudy.R;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    NfcAdapter mNfcAdapter;
       IntentFilter[] mFilters;
    private static final String TAG = "MyLog";
    NfcAdapter  nfcAdapter;
    AnimationDrawable mgAnimation;
    AnimationDrawable mgeAnimation;


    private final String[][] techList = new String[][] { new String[] { MifareClassic.class.getName() } };
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView mgImage = (ImageView) findViewById(R.id.imageView2);
        mgImage.setBackgroundResource(R.drawable.metrogochi_anim);
        mgAnimation = (AnimationDrawable) mgImage.getBackground();
        mgAnimation.start();


        //--- BEGIN Count all tickets
        SQLiteDatabase myDB =
                openOrCreateDatabase("my.db", MODE_PRIVATE, null);
        Cursor mCount= myDB.rawQuery("select count(*) from user", null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        Log.d(TAG,"Всего билетов прочитано "+String.valueOf(count));
        ((TextView) findViewById(R.id.textView_tcount)).setText("Всего съедено билетов - "+String.valueOf(count));
        mCount.close();
        myDB.close();

        //--- END Count all tickets

    }

    @Override
    protected void onResume() {
        super.onResume();

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //Log.d(TAG,"onResume");
        Intent nfcIntent = new Intent(this, getClass());
       // Log.d(TAG,"intent");
        nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
       // Log.d(TAG,"Started");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, nfcIntent, PendingIntent.FLAG_MUTABLE);

        // creating intent receiver for NFC events:
       // Log.d(TAG,"Pending");

        //IntentFilter[] intentFiltersArray = new IntentFilter[]{};
        IntentFilter filter = new IntentFilter();
        filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);

       // Log.d(TAG,"Intentfilter");
        String[][] techList = new String[][]{{android.nfc.tech.Ndef.class.getName()}, {android.nfc.tech.NdefFormatable.class.getName()}};
       // Log.d(TAG,"Adapter");

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[] { filter }, techList);

        if(mgeAnimation!=null) {
            Log.d(TAG,"Animation not null");
            Log.d(TAG,"Animation state "+String.valueOf(mgeAnimation.isRunning()));

           // mgeAnimation.stop();
            if(mgeAnimation.isRunning()==false) {
        Log.d(TAG,"OnResume");
        ImageView mgImage = (ImageView) findViewById(R.id.imageView2);
        mgImage.setBackgroundResource(R.drawable.metrogochi_anim);
        mgAnimation = (AnimationDrawable) mgImage.getBackground();
        mgAnimation.start();} else {
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        mgeAnimation.stop();
                        Log.d(TAG,"Остановись!!!");
                        ImageView mgImage = (ImageView) findViewById(R.id.imageView2);
                        mgImage.setBackgroundResource(R.drawable.metrogochi_anim);
                        mgAnimation = (AnimationDrawable) mgImage.getBackground();
                        mgAnimation.start();

                    }
                },1000);




                }}

        //mgAnimation.stop();
       /* NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        Log.d(TAG,"NFC adapter creation");
        Intent intent = new Intent(getApplicationContext(), this.getClass());
        Log.d(TAG,"intetnt creation");
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Log.d(TAG,"intetnt set flag");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        Log.d(TAG,"NFC adapter creation");
        String[][] filter = new String[][] { new String[] { "android.nfc.tech.MifareUltralight" } };
        adapter.enableForegroundDispatch(this, pendingIntent, null, filter);*/




    }

    @Override
    protected void onPause() {
        super.onPause();
       // Log.d(TAG,"onPause");
        // disabling foreground dispatch:
        //NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.disableForegroundDispatch(this);


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //mgAnimation.stop();
        ImageView mgeImage = (ImageView) findViewById(R.id.imageView2);
        mgeImage.setBackgroundResource(R.drawable.metrogoghi_e);
        mgeAnimation = (AnimationDrawable) mgeImage.getBackground();
        mgeAnimation.start();




        //setIntent(intent);
        Log.d(TAG,"onNewIntent");
      //---

            if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
                ((TextView) findViewById(R.id.text)).setText(ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));
                Log.d(TAG, String.valueOf(intent.getData()));
                Log.d(TAG, ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                if(tag == null){
                    //textViewInfo.setText("tag == null");
                    //Log.d(TAG,"tag == null");
                }else{
                    String tagInfo = tag.toString() + "\n";
                    tagInfo += "\nTag Id: \n";
                    byte[] tagId = tag.getId();
                    tagInfo += "length = " + tagId.length +"\n";
                    for(int i=0; i<tagId.length; i++){
                        tagInfo += Integer.toHexString(tagId[i] & 0xFF) + " ";
                    }
                    tagInfo += "\n";

                    String[] techList = tag.getTechList();
                    tagInfo += "\nTech List\n";
                    tagInfo += "length = " + techList.length +"\n";
                    for(int i=0; i<techList.length; i++){
                        tagInfo += techList[i] + "\n ";
                    }
                    //Log.d(TAG,tagInfo);
                    //textViewInfo.setText(tagInfo);
                    NfcA nfca = NfcA.get(tag);

                    //----REAd NFCA
                    try
                    {
                        NfcA nfcA = NfcA.get(tag);
                        nfcA.connect();
                        byte[] SELECT = {
                                (byte) 0x30,
                                (byte) 0x05,
                        };

                        byte[] result = nfcA.transceive(SELECT);
                        //Log.d(TAG,String.valueOf(result));
                        int data_len = ((result[0]&0x0f)<<8)+((result[1]&0xff));
                       // int data_len = ((result[0])+(result[1]));
                       // Log.d (TAG, "whether or not the written data" + result [0] + ", the write data length:" + data_len);
                        byte[] buf_res = new byte[data_len/2+4];
                        if (result[0]!=0 && data_len!=0){
                            {
                                byte[] DATA_READ = {
                                                    (byte) 0x30,  // READ
                                                    (byte) (4)
                                                    };
                                byte[] data_res = nfcA.transceive(DATA_READ);
                                Log.d (TAG, "successful reader");
                                //Log.d (TAG, String.valueOf(data_res));
                                String block = ByteArrayToHexString(data_res);
                                Log.d (TAG,block.substring(5,13));
                                //int daycount = (Integer.parseInt(block.substring(16,18), 16));
                                long n = Long.parseLong(block.substring(5,13), 16);
                                byte[] result1 = nfcA.transceive(SELECT);
                                byte[] DATA_READ1 = {
                                                    (byte) 0x30,  // READ
                                                    (byte) (5)// & 0x0FF)
                                                     };
                                byte[] data_res1 = nfcA.transceive(DATA_READ1);
                                String block1 = ByteArrayToHexString(data_res1);
                                if (block1.substring(24,26).equals("00")) {
                                                                            if (ProcessDB(String.valueOf(n))==true) { Toast.makeText(this, "Юбилейная печенька!",Toast.LENGTH_SHORT).show();} else
                                                                            { Toast.makeText(this, "Я уже съел эту юбилейную печеньку!",Toast.LENGTH_SHORT).show();}
                                                                            ((TextView) findViewById(R.id.text)).setText(String.valueOf(n));
                                                                            ((TextView) findViewById(R.id.textData)).setText("Юбилейная серия "+block1.substring(26,28));
                                                                            }
                                else {
                                    int daycount = Integer.parseInt(block1.substring(24, 28), 16);
                                    Log.d(TAG, "Блок 5 > " + block1);
                                    Log.d(TAG, String.valueOf(n));

                                    ((TextView) findViewById(R.id.text)).setText(String.valueOf(n));
                                    Log.d(TAG, DateCalc(daycount));
                                    ((TextView) findViewById(R.id.textData)).setText(DateCalc(daycount));
                                    if (ProcessDB(String.valueOf(n)) == true) {
                                        Toast.makeText(this, "Карата съедена", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(this, "Я уже съел эту карту!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                //Log.d (TAG,DateCalc(14400));
                            }
                        }


                    }catch(IOException e){
                        e.printStackTrace();
                        //cleanData();
                        Log.d (TAG, "the reader failed");
                    }

                }

            }


        //Toast.makeText(this, "Онемация",Toast.LENGTH_SHORT).show();

    }

    //----Help functions

    private String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
        String out = "";

        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i]; /// для пробелов поставить в конце +" "
        }
        return out;
    }

    private  String DateCalc (int days) {
        String dt = "1992-01-01";  // Start date
        SimpleDateFormat sdf = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
        }
        Calendar c = Calendar.getInstance();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                c.setTime(sdf.parse(dt));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, days);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
        SimpleDateFormat sdf1 = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sdf1 = new SimpleDateFormat("dd-MM-yyyy");
        }
        String output = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            output = sdf1.format(c.getTime());
        }
        return output;

    }

    private boolean ProcessDB (String number) {
        boolean result = false;
        boolean flag = false;
        SQLiteDatabase myDB =
                openOrCreateDatabase("my.db", MODE_PRIVATE, null);
        myDB.execSQL("CREATE TABLE IF NOT EXISTS user (ticketNumber VARCHAR(20))");
        Cursor myCursor = myDB.rawQuery("select ticketNumber from user", null);
        while(myCursor.moveToNext()) {
                                        String TNfromDB = myCursor.getString(0);
                                        //Log.d(TAG,"В переменной "+String.valueOf(TNfromDB));
                                        //Log.d(TAG,"В базе "+myCursor.getString(0));
                                        if (TNfromDB.equals(number)) {flag=true;}
                                     }
        if (flag==false) {
                    ContentValues insertValues = new ContentValues();
                    insertValues.put("ticketNumber", number);
                    myDB.insert("user", null, insertValues);
                    result = true;

                    } else {result=false;};
        myCursor.close();
        myDB.close();
        return result;
    }

    // ----
    }





