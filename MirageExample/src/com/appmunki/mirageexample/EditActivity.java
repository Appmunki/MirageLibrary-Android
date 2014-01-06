package com.appmunki.mirageexample;

import com.appmunki.mirage.Mirage;
import com.appmunki.mirage.Utils;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditActivity extends Activity {

	Mirage mirage;
	
	EditText etID;
	EditText etName;
	EditText etDescription;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);

		mirage = new Mirage(Utils.api_key, EditActivity.this, true);

		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    
		
		
		Button btnEdit = (Button) findViewById(R.id.buttonEdit);
		btnEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				editPattern();
			}
		});
		
		etID = (EditText)findViewById(R.id.EditTextID);
		etName = (EditText)findViewById(R.id.editTextName);
		etDescription = (EditText)findViewById(R.id.editTextDescription);

	}

	public void editPattern() {
		try {
			String id = etID.getText().toString();
			String name = etName.getText().toString();
			String description = etDescription.getText().toString();
			
			mirage.editPattern(id, name, description);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Toast.makeText(this, "TEST", Toast.LENGTH_SHORT).show();
	}

}
