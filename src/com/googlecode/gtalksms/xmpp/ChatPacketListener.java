package com.googlecode.gtalksms.xmpp;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.googlecode.gtalksms.MainService;
import com.googlecode.gtalksms.SettingsManager;
import com.googlecode.gtalksms.tools.Tools;

import org.jivesoftware.smack.PacketListener;

public class ChatPacketListener implements PacketListener {
        private SettingsManager mSettings;
        private Context mCtx;
        private XMPPConnection mConnection;
        
        public ChatPacketListener(XMPPConnection connection, Context ctx) {
            this.mConnection = connection;
            this.mCtx = ctx;
            this.mSettings = SettingsManager.getSettingsManager(ctx);
        }
        
        public void processPacket(Packet packet) {
            Message message = (Message) packet;
            String from = message.getFrom();
            
            if (mSettings.startWithNotifiedAddress(from) && !message.getFrom().equals(mConnection.getUser()) && message.getBody() != null) {
                if (mSettings.debugLog) {
                    Log.i(Tools.LOG_TAG, "XMPP packet received - sending Intent: " + MainService.ACTION_XMPP_MESSAGE_RECEIVED);
                }
                
                Tools.startSvcXMPPMsg(mCtx, message.getBody(), from);
            } else if (mSettings.debugLog) {
                if (!mSettings.startWithNotifiedAddress(from)) {
                    Log.i(Tools.LOG_TAG, "XMPP packet received - but from address \"" + from.toLowerCase() + "\" does not match notification address \"" 
                            + TextUtils.join("|",mSettings.getNotifiedAddresses()));
                } else if (message.getFrom().equals(mConnection.getUser())) {
                    Log.i(Tools.LOG_TAG, "XMPP packet received - but from the same user as the XMPP connection");
                } else if (message.getBody() == null) {
                    Log.i(Tools.LOG_TAG, "XMPP Packet received - but without body (body == null)");
                }
            }
        }
}
