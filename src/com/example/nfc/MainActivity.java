package com.example.nfc;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Intent intent = getIntent();
		byte[] tagid = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
        Tag tag = (Tag)intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        
        boolean writemode = false;
        
        int sector = 1;
        if(tag!=null)
        {
        	Log.i("nfc","nfc tagid="+stringFromBytes(tagid));
        	MifareClassic mfc = MifareClassic.get(tag);
            try {
                if(!mfc.isConnected())mfc.connect();
                
                if(writemode)
                {
                	if(mfc.authenticateSectorWithKeyA(sector,MifareClassic.KEY_DEFAULT)){
                    	byte[] data = new byte[16];
                    	data[0] = (byte)0x01;
                    	data[1] = (byte)0x02;
                    	data[2] = (byte)0x03;
                        mfc.writeBlock(sector*4+0,data);
                        
                        for(int i=0; i<6; i++)data[i] = (byte)(i+1);
                        data[6] = (byte)0x70;
                        data[7] = (byte)0xF0;
                        data[8] = (byte)0xF8;
                        for(int i=0; i<6; i++)data[i+10] = (byte)(i+1);
                        mfc.writeBlock(sector*4+3,data);
                        
                        Log.i("nfc","nfc written");
                    }else{
                        Log.i("nfc","nfc auth fail");
                    }
                }else{
                	byte[] keyB = new byte[16];
                	for(int i=0; i<6; i++)keyB[i] = (byte)(i+1);
                	if(mfc.authenticateSectorWithKeyB(sector,keyB)){
                        byte[] Block0 = mfc.readBlock(sector*4+0);
                        String Block0String = stringFromBytes(Block0);
                        Log.i("nfc","nfc Block0="+Block0String);
                    }else{
                        Log.i("nfc","nfc auth fail");
                    }
                }
                mfc.close();
            }catch (Exception e)
            {
            	 Log.i("nfc","nfc connection fail");
            }finally{

            }
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public String stringFromBytes(byte[] bytes) {
        StringBuilder buffer = new StringBuilder();
        for (byte b : bytes) {
            String hex = String.format("%02X", b);
            buffer.append(hex);
        }

        String text = buffer.toString().trim();
        return text;
    }

}
