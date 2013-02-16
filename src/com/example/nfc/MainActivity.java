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
        
        int sector = 0;
        if(tag!=null)
        {
        	Log.i("nfc","nfc tagid="+stringFromBytes(tagid));
        	MifareClassic mfc = MifareClassic.get(tag);
            try {
                if(!mfc.isConnected())mfc.connect();
                if(mfc.authenticateSectorWithKeyA(sector,MifareClassic.KEY_DEFAULT)){
                    byte[] Block0 = mfc.readBlock(0);
                    String Block0String = stringFromBytes(Block0);
                    Log.i("nfc","nfc Block0="+Block0String);
                }else{
                    Log.i("nfc","nfc auth fail");
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
