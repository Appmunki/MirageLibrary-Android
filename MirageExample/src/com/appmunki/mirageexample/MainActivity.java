package com.appmunki.mirageexample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.appmunki.mirage.Mirage;
import com.appmunki.mirage.UploadProgressListener;
import com.appmunki.mirage.Utils;

public class MainActivity extends Activity {

	private final static int CREATE = 0;
	private final static int MATCH = 1;

	Mirage mirage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mirage = new Mirage(Utils.api_key, MainActivity.this, true);

		Button btnCreate = (Button) findViewById(R.id.button1);
		btnCreate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, CREATE);
			}
		});

		Button btnEdit = (Button) findViewById(R.id.button2);
		btnEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// editPattern();
				Intent intent = new Intent(MainActivity.this, EditActivity.class);
				startActivity(intent);
			}
		});

		Button btnDelete = (Button) findViewById(R.id.button3);
		btnDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this, DeleteActivity.class);
				startActivity(intent);
			}
		});

		Button btnMatch = (Button) findViewById(R.id.button4);
		btnMatch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, MATCH);
			}
		});

	}

	public void createPattern(String path) {
		try {

			mirage.createPattern(path, "test", "description", new UploadProgressListener() {
				@Override
				public void progress(int progress) {
					// TODO Auto-generated method stub
					Log.v("", progress + "");
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

		// mirage.createPattern("", name, description, listener);

		Toast.makeText(this, "TEST", Toast.LENGTH_SHORT).show();
	}

	public void doMatch(String path) {

		try {
			mirage.doMatch(path, new UploadProgressListener() {
				@Override
				public void progress(int progress) {
					Log.v("", progress + "");
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		Toast.makeText(this, "TEST", Toast.LENGTH_SHORT).show();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {

			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			switch (requestCode) {
			case CREATE:
				createPattern(picturePath);
				break;
			case MATCH:
				doMatch(picturePath);
				break;
			}

		}
	}

}
