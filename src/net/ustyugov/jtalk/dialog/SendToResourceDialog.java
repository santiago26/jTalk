/*
 * Copyright (C) 2012, Igor Ustyugov <igor@ustyugov.net>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses/
 */

package net.ustyugov.jtalk.dialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.ustyugov.jtalk.adapter.ResourceAdapter;
import net.ustyugov.jtalk.service.JTalkService;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;

import com.jtalk2.R;

public class SendToResourceDialog implements OnClickListener {
	private Activity a;
	private String jid;
	private String message;
	private List<String> list = new ArrayList<String>();
	private JTalkService service;
	
	public SendToResourceDialog(Activity activity, String jid, String message) {
		this.a = activity;
		this.jid = jid;
		this.message = message;
		this.service = JTalkService.getInstance();
	}
	
	public void show() {
		int slash = jid.lastIndexOf("/");
		if (slash == -1) {
			Iterator<Presence> it =  service.getRoster().getPresences(jid);
			while (it.hasNext()) {
				Presence p = it.next();
				if (p.getType() != Presence.Type.unavailable) {
					list.add(StringUtils.parseResource(p.getFrom()));
				}
			}
			
			if (!list.isEmpty()) {
				ResourceAdapter adapter = new ResourceAdapter(a, jid, list);

		        AlertDialog.Builder builder = new AlertDialog.Builder(a);
		        builder.setTitle(a.getString(R.string.SelectResource));
		        builder.setAdapter(adapter, this);
		        builder.create().show();
			}
		}
	}

	public void onClick(DialogInterface dialog, int which) {
		if (service != null && service.isAuthenticated()) {
			service.sendMessage(jid, message, list.get(which));
			
			Intent intent = new Intent(net.ustyugov.jtalk.Constants.NEW_MESSAGE);
			intent.putExtra("jid", jid);
	        intent.putExtra("clear", true);
	        a.sendBroadcast(intent);
		}
	}
}
