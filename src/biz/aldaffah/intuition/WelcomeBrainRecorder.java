/*
 * 
 */
package biz.aldaffah.intuition;

//~--- non-JDK imports --------------------------------------------------------

import android.app.Activity;

import android.bluetooth.BluetoothAdapter;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;

import android.view.View;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.neurosky.thinkgear.*;

public class WelcomeBrainRecorder extends Activity {
    public String thought;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Spinner                    spinner = (Spinner) findViewById(R.id.userSelector);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.user_array,
                                                 android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void record(View view) {
        final TextView input = (TextView) findViewById(R.id.thoughtBar);

        thought = input.getText().toString();

        Intent intent = new Intent(this, BrainRecorderActivity.class);

        intent.putExtra("thought", thought);
        startActivity(intent);
    }

    public class MyOnItemSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            Toast.makeText(parent.getContext(), "Selected User " + parent.getItemAtPosition(pos).toString(),
                           Toast.LENGTH_LONG).show();
        }

        public void onNothingSelected(AdapterView parent) {

        	/** Do nothing. */
        }
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
