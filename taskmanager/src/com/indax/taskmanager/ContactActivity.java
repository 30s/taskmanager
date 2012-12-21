package com.indax.taskmanager;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.indax.taskmanager.fragments.ContactFragment;
import com.indax.taskmanager.fragments.EventFragment;

public class ContactActivity extends SherlockFragmentActivity implements
		LoaderManager.LoaderCallbacks<Cursor>, TabListener {

	private static final int CONTACT_LOADER = 0;
	private Uri mContactURL;
	private String mContact;
	private final String[] tabs = { "Contact", "Events" };
	private ContactFragment mFragContact;
	private EventFragment mFragEvent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_contact);

		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		for (int i = 0; i < tabs.length; i++) {
			ActionBar.Tab tab = getSupportActionBar().newTab();
			tab.setText(tabs[i]);
			tab.setTabListener(this);
			getSupportActionBar().addTab(tab);
		}

		mContactURL = getIntent().getData();
		getSupportLoaderManager().initLoader(CONTACT_LOADER, null, this);
		setSupportProgressBarIndeterminateVisibility(false);
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
			mContact = display_name;
			if (mFragContact != null) {
				mFragContact.setContact(mContact);
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (tab.getText().equals("Contact")) {
			if (mFragContact == null) {
				mFragContact = new ContactFragment();
			}
			ft.replace(R.id.frag_container, mFragContact);
		} else if (tab.getText().equals("Events")) {
			if (mFragEvent == null) {
				mFragEvent = new EventFragment();
				mFragEvent.setContact(mContact);
			}
			ft.replace(R.id.frag_container, mFragEvent);
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}
}
