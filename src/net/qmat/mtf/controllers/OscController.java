package net.qmat.mtf.controllers;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

import net.qmat.mtf.Main;
import net.qmat.mtf.utils.Settings;
import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;

public class OscController extends Thread {
	
	OscP5 oscP5;
	LinkedBlockingQueue<QueueMessage> queue;

	NetAddress soundMachine;
	NetAddress sequencerMachine;
	
	public OscController() {
		oscP5 = new OscP5(this, Settings.getInteger(Settings.OSC_LOCAL_PORT));
		queue = new LinkedBlockingQueue<QueueMessage>();
		
		/*
		 * We're not sending anything in this sketch, only receiving
		soundMachine = new NetAddress(Settings.getString(Settings.OSC_SOUND_REMOTE_IP),
									  Settings.getInteger(Settings.OSC_SOUND_REMOTE_PORT));
		 */
	}
	
	/*
	 * Not sending anything. If in the future we are, change this...
	public void queueSoundEvent(String endPoint, Object... objects) {
		queueEvent(soundMachine, endPoint, objects);
	}
	*/
	
	private void queueEvent(NetAddress address, String endPoint, Object[] objects) {
		try {
			queue.put(new QueueMessage(address, endPoint, objects));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void sendEvent(QueueMessage qm) {
		OscMessage m = new OscMessage(qm.endPoint);
		for(Object obj : qm.objects) {
			if(obj instanceof Integer)
				m.add((Integer)obj);
			if(obj instanceof Long)
				m.add((Long)obj);
			if(obj instanceof Float)
				m.add((Float)obj);
			if(obj instanceof Double)
				m.add((Double)obj);
			if(obj instanceof String)
				m.add((String)obj);
		}
		oscP5.send(m, qm.address);
	}

	@Override
	public void run() {
		while(true) {
			try {
				QueueMessage qm = (QueueMessage)queue.take();
				sendEvent(qm);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class QueueMessage {
		NetAddress address;
		String endPoint;
		Object[] objects;
		
		public QueueMessage(NetAddress address, String endPoint, Object[] objects) {
			this.address = address;
			this.endPoint = endPoint;
			this.objects = objects;
		}
	}
	
	// TODO: implement for audio analysis
	void oscEvent(OscMessage theOscMessage) {
		if(theOscMessage.checkAddrPattern("/analysis")==true) {
			// don't bother with checking 25 ffff...
			//if(theOscMessage.checkTypetag("")) {}
			Object[] original = theOscMessage.arguments();
			original = Arrays.copyOfRange(original, 3, original.length);
			Float[] data = new Float[original.length];
			for(int i=0; i<data.length; i++)
				data[i] = (Float)(original[i]);
			Controllers.getAnalysisController().setAnalysis(data);
		}
	}
}
