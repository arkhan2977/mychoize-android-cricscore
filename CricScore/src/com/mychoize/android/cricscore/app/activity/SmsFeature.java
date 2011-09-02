package com.mychoize.android.cricscore.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.mychoize.android.cricscore.app.CricScoreDBAdapter;
import com.mychoize.android.cricscore.app.GenericProperties;
import com.mychoize.android.cricscore.app.R;
import com.mychoize.android.cricscore.app.SMSContact;

public class SmsFeature extends Activity {
	
	private List<SMSContact> contacts;
	private SMSContactAdapter smsContactAdapter;
	private static int CONTACT_PICKER_RESULT = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CricScoreDBAdapter adapter = new CricScoreDBAdapter(
				getApplicationContext());
		adapter.open();
		contacts = adapter.getAllContacts();
		adapter.close();
		this.smsContactAdapter = new SMSContactAdapter(this);
		
		setContentView(R.layout.sms_feature);
		final ListView l1 = (ListView) findViewById(R.id.ListSMSContacts);
		l1.setAdapter(smsContactAdapter);
		l1.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				//Toast.makeText(getApplicationContext(), "Touch wored", Toast.LENGTH_SHORT).show();
			}
		});
		
		if(contacts == null || contacts.size() == 0){
			Toast.makeText(getApplicationContext(), "To add contact, Menu -> Add", Toast.LENGTH_LONG).show();
		}
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.sms_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.add:
			addContact();
			return true;
		case R.id.clear:
			CricScoreDBAdapter adapter = new CricScoreDBAdapter(
					getApplicationContext());
			adapter.open();
			adapter.clearSMSContacts();
			contacts = adapter.getAllContacts();
			adapter.close();
			updateScreen();
			Toast.makeText(getApplicationContext(), "Auto SMS contacts are cleared", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.options:
			Intent myIntent = new Intent(SmsFeature.this, UserPreference.class);
			startActivity(myIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void addContact() {
		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,  
	            Contacts.CONTENT_URI);  
	    startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);  
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == CONTACT_PICKER_RESULT) {
			List<String> nameAndNumbers = getNameAndNumbers(data);
			Log.d(GenericProperties.TAG, nameAndNumbers.toString());
			if(nameAndNumbers != null){
				if(nameAndNumbers.size() == 2){
					addToContacts(new SMSContact(nameAndNumbers.get(0), nameAndNumbers.get(1)));
				}
				else if(nameAndNumbers.size() > 2){
					buildUserChoice(nameAndNumbers);
				}
			}
		}
	}
	
	private void updateScreen(){
		this.smsContactAdapter.notifyDataSetChanged();		
	}
	
	private void buildUserChoice(List<String> nameAndNumbers){
		final String name = nameAndNumbers.get(0);
		nameAndNumbers.remove(0);
		final CharSequence[] items = nameAndNumbers.toArray(new CharSequence[nameAndNumbers.size()]);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(name);
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		        addToContacts(new SMSContact(name, (String) items[item]));
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void addToContacts(SMSContact contact){
		Toast.makeText(getApplicationContext(), contact.getName()+" "+ contact.getNumber()+" added", Toast.LENGTH_LONG).show();
		List<SMSContact> _contacts = new ArrayList<SMSContact>();
		_contacts.add(contact);
		CricScoreDBAdapter adapter = new CricScoreDBAdapter(
				getApplicationContext());
		adapter.open();
		adapter.insertSMSContacts(_contacts);
		contacts = adapter.getAllContacts();
		adapter.close();
		updateScreen();
	}
	
	private List<String> getNameAndNumbers(Intent data){
		List<String> nameAndNumbers = new ArrayList<String>();
		Cursor cursor = managedQuery(data.getData(), null, null, null, null);
		while (cursor.moveToNext()) {
			String contactId = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts._ID));
			String name = cursor
					.getString(cursor
							.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
			nameAndNumbers.add(name);
			Log.d(GenericProperties.TAG, name);
			String hasPhoneStr = cursor
					.getString(cursor
							.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

			if (hasPhoneStr.equalsIgnoreCase("1")) {
				Cursor phones = getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = " + contactId, null, null);
				while (phones.moveToNext()) {
					String phoneNumber = phones
							.getString(phones
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					Log.d(GenericProperties.TAG, phoneNumber);
					nameAndNumbers.add(phoneNumber);
				}
				phones.close();
			} else{
				Toast.makeText(getApplicationContext(), "No phone number found", Toast.LENGTH_SHORT).show();
			}
			cursor.close();
		}
		return nameAndNumbers;
	}
	
	private class SMSContactAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public SMSContactAdapter(Context context) {
			mInflater = LayoutInflater.from(context);

		}

		public int getCount() {
			return SmsFeature.this.contacts.size();
		}

		public Object getItem(int position) {
			return contacts.get(position);
		}

		public long getItemId(int position) {
			return contacts.get(position).getId();
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.sms_contact, null);
				holder = new ViewHolder();
				holder.name = (TextView) convertView.findViewById(R.id.Name);
				holder.number = (TextView) convertView.findViewById(R.id.Contact);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			try {
				SMSContact contact = contacts.get(position);
				holder.name.setText(contact.getName());
				holder.number.setText(contact.getNumber());
			} catch (ArrayIndexOutOfBoundsException e) {

			}

			return convertView;
		}

		class ViewHolder {
			TextView name;
			TextView number;
			boolean connected;
		}
	}
}
