package com.indax.taskmanager;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

public class ContactActivity extends SherlockFragmentActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private static final int CONTACT_LOADER = 0;
	private Uri mContactURL;
	private TextView txt_name;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);

		txt_name = (TextView) findViewById(R.id.txt_name);
		mContactURL = getIntent().getData();

		getSupportLoaderManager().initLoader(CONTACT_LOADER, null, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_contact, menu);
		return true;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {
		if (id == CONTACT_LOADER) {
			return new CursorLoader(this, mContactURL, null, null, null, null);
		}

		throw new IllegalArgumentException("Unknown loader id!");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (cursor.moveToNext()) {
			int idx_display_name = cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
			String display_name = cursor.getString(idx_display_name);
			txt_name.setText(display_name);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
	}
}
