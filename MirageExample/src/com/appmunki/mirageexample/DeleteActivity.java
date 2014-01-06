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

public class DeleteActivity extends Activity {

	EditText etID;

	Mirage mirage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delete);

		mirage = new Mirage(Utils.api_key, DeleteActivity.this, true);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		Button btnDelete = (Button) findViewById(R.id.buttonDelete);
		btnDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				deletePattern();
			}
		});

		etID = (EditText) findViewById(R.id.EditTextID);
	}

	public void deletePattern() {
		try {
			String id = etID.getText().toString();
			mirage.deletePattern(id);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Toast.makeText(this, "TEST", Toast.LENGTH_SHORT).show();
	}

}
