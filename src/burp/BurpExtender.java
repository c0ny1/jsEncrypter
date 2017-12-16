package burp;

import java.awt.Component;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class BurpExtender implements IBurpExtender,IIntruderPayloadProcessor,ITab {
	private String extensionName = "jsEncrypter";
	private String version ="v1.0";
	private IBurpExtenderCallbacks callbacks;
	private IExtensionHelpers helpers;
	private PrintWriter stdout;
	private PrintWriter stderr;
	private CloseableHttpClient  client;
	private RequestConfig requestConfig;
	private GUI gui;
	
	@Override
	public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
		this.callbacks = callbacks;
		this.helpers = callbacks.getHelpers();
		this.stdout = new PrintWriter(callbacks.getStdout(),true);
		this.stderr = new PrintWriter(callbacks.getStderr(),true);
		
		callbacks.setExtensionName(extensionName+" "+version);
		callbacks.registerIntruderPayloadProcessor(this);
		
		this.requestConfig = RequestConfig.custom().setConnectionRequestTimeout(3000).setConnectTimeout(3000).setSocketTimeout(3000).build();
		this.client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
		//this.client = HttpClients.createDefault();
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
		byte[] newpayload ="".getBytes();
		String payload = new String(currentPayload);
		HttpPost httpPost = new HttpPost(gui.getURL());
		try {
			List nameValuePairs = new ArrayList(1);
			nameValuePairs.add(new BasicNameValuePair("payload",payload));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			CloseableHttpResponse response = client.execute(httpPost);
			
			String responseAsString = EntityUtils.toString(response.getEntity());
			newpayload = helpers.stringToBytes(responseAsString);
			
		} catch (Exception e) {
			stderr.println(e.getMessage());
			newpayload = "JsEncrypter cannot connect phantomJS!".getBytes();
		}
		return newpayload;
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
				  + "[+]    jsEncrypter v1.0\n"
				  + "[+]    anthor: c0ny1\n"
				  + "[+]    email:  root@gv7.me\n"
				  + "[+]    github: http://github.com/c0ny1/jsEncrypter\n"
				  + "[+] ####################################";
		return bannerInfo;
	}	
}
