package burp;

import java.awt.Component;
import java.io.PrintWriter;
import javax.swing.SwingUtilities;

public class BurpExtender implements IBurpExtender,IIntruderPayloadProcessor,ITab {
    public final static String extensionName = "jsEncrypter";
	public final static String version ="0.3.2";
	public static IBurpExtenderCallbacks callbacks;
	public static IExtensionHelpers helpers;
	public static PrintWriter stdout;
	public static PrintWriter stderr;
	public static GUI gui;
	
	@Override
	public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
		this.callbacks = callbacks;
		this.helpers = callbacks.getHelpers();
		this.stdout = new PrintWriter(callbacks.getStdout(),true);
		this.stderr = new PrintWriter(callbacks.getStderr(),true);
		
		callbacks.setExtensionName(extensionName+" "+version);
		callbacks.registerContextMenuFactory(new Menu());
		callbacks.registerIntruderPayloadProcessor(this);

		BurpExtender.this.gui = new GUI();
		SwingUtilities.invokeLater(new Runnable()
	      {
	        public void run()
	        {
	          BurpExtender.this.callbacks.addSuiteTab(BurpExtender.this);
	          stdout.println(Utils.getBanner());
	        }
	      });
		
	}
	
	//
	// 实现IIntruderPayloadProcessor
	//
	
	@Override
	public String getProcessorName() {
		return extensionName;
	}

	@Override
	public byte[] processPayload(byte[] currentPayload, byte[] originalPayload, byte[] baseValue) {
		String payload = new String(currentPayload);
		String newPayload = Utils.sendPayload(payload);
		return helpers.stringToBytes(newPayload);
	}

	//
	// 实现ITab
	//
	
	@Override
	public String getTabCaption() {
		return extensionName;
	}

	@Override
	public Component getUiComponent() {
		return gui.getComponet();
	}
}
