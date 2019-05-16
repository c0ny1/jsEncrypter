package burp;

import java.awt.Component;
import java.io.PrintWriter;
import javax.swing.SwingUtilities;

public class BurpExtender implements IBurpExtender,IIntruderPayloadProcessor,ITab {
    public final static String extensionName = "jsEncrypter";
	public final static String version ="0.2.1";
	private IBurpExtenderCallbacks callbacks;
	private IExtensionHelpers helpers;
	private PrintWriter stdout;
	private PrintWriter stderr;
	private GUI gui;
	
	@Override
	public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
		this.callbacks = callbacks;
		this.helpers = callbacks.getHelpers();
		this.stdout = new PrintWriter(callbacks.getStdout(),true);
		this.stderr = new PrintWriter(callbacks.getStderr(),true);
		
		callbacks.setExtensionName(extensionName+" "+version);
		callbacks.registerIntruderPayloadProcessor(this);

		BurpExtender.this.gui = new GUI(callbacks);
		SwingUtilities.invokeLater(new Runnable()
	      {
	        public void run()
	        {
	          BurpExtender.this.callbacks.addSuiteTab(BurpExtender.this); 
	          stdout.println("[+] jsEncrypter is loaded");
	          stdout.println("[+] ^_^");
	          stdout.println(getBanner());
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
		byte[] newPayload = "".getBytes();

		String payload = new String(currentPayload);

		String strPayload = null;
		try {
			HttpClient hc = new HttpClient(gui.getURL());
			hc.setConnTimeout(3000);
			hc.setReadTimeout(3000);
			String data = "payload=" + payload;
			hc.setData(data);
			hc.sendPost();
			strPayload = hc.getRspData();
		} catch (Exception e) {
			stderr.println(e.getMessage());
			newPayload = e.getMessage().getBytes();
		}
		newPayload = helpers.stringToBytes(strPayload);
		return newPayload;
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
	
	//////////////////////////////////////////////////////////////////////
	
	public String getBanner(){
		String bannerInfo = 
				    "[+]\n"
				  + "[+] #####################################\n"
				  + "[+]    " + extensionName + " v" + version +"\n"
				  + "[+]    anthor: c0ny1\n"
				  + "[+]    email:  root@gv7.me\n"
				  + "[+]    github: http://github.com/c0ny1/jsEncrypter\n"
				  + "[+] ####################################";
		return bannerInfo;
	}	
}
