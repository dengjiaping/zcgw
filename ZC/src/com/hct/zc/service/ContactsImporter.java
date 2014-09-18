package com.hct.zc.service;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.hct.zc.bean.Client;
import com.hct.zc.utils.LogUtil;

/**
 * @todo 从通讯录导入
 * @time 2014年6月11日 下午3:28:31
 * @author liuzenglong163@gmail.com
 */

public class ContactsImporter {

	private final Activity mActivity;

	public ContactsImporter(Activity activity) {
		mActivity = activity;
	}

	/**
	 * 
	 * 跳到手机通讯录中，准备导入联系人
	 * 
	 * @time 2014年5月19日 上午10:07:28
	 * @param requestCode
	 *            请求码，用于Activity返回时判断
	 * @author jie.liu
	 */
	public void importInfoFromContacts(int requestCode) {
		if (mActivity == null) {
			LogUtil.w(this, "Activity 是null");
			return;
		}

		Intent intentContact = new Intent(Intent.ACTION_PICK,
				ContactsContract.Contacts.CONTENT_URI);
		mActivity.startActivityForResult(intentContact, requestCode);
	}

	/**
	 * 
	 * 获取从通讯录中返回的联系人
	 * 
	 * @time 2014年7月2日 下午2:47:18
	 * @author liuzenglong163@gmail.com
	 * @param intent
	 *            onActivityResult的参数
	 * @return
	 */
	public Client getContactInfo(Intent intent) {
		Client client = new Client();
		Cursor cursor = mActivity.managedQuery(intent.getData(), null, null,
				null, null);
		while (cursor.moveToNext()) {
			String contactId = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts._ID));
			String name = cursor
					.getString(cursor
							.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

			String phoneNumber = getPhoneNum(cursor, contactId);

			String emailAddress = getEmail(contactId);

			client.setName(name);
			client.setPhone(phoneNumber);
			client.setEmail(emailAddress);
		}
		return client;
	}

	private String getPhoneNum(Cursor cursor, String contactId) {
		String hasPhone = cursor.getString(cursor
				.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
		if (hasPhone.equalsIgnoreCase("1"))
			hasPhone = "true";
		else
			hasPhone = "false";

		String phoneNumber = "";
		if (Boolean.parseBoolean(hasPhone)) {
			Cursor phones = mActivity.getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
							+ contactId, null, null);
			while (phones.moveToNext()) {
				phoneNumber = phones
						.getString(phones
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			}
			phones.close();
		}
		return phoneNumber;
	}

	private String getEmail(String contactId) {
		Cursor emails = mActivity.getContentResolver().query(
				ContactsContract.CommonDataKinds.Email.CONTENT_URI,
				null,
				ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = "
						+ contactId, null, null);

		String emailAddress = "";
		while (emails.moveToNext()) {
			emailAddress = emails
					.getString(emails
							.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
		}
		emails.close();
		return emailAddress;
	}
}
