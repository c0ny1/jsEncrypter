package burp;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.FlowLayout;
import javax.swing.JSplitPane;
import java.awt.Component;
import javax.swing.Box;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class GUI{
	private IBurpExtenderCallbacks mCall;
	private IExtensionHelpers helpers;
	private PrintWriter stdout;
	private PrintWriter stderr;
	
	private JPanel contentPane;
	private JLabel lbHost;
	private JTextField tfHost;
	private JLabel lbPort;
	private JTextField tfPort;
	private JButton btnConn;
	private JLabel lbConnectInfo;
	private JLabel lbConnectStatus;
	private JButton btnTest;
	private JSplitPane splitPane;
	private Component verticalStrut;
	private JTextArea taTestPayload;
	private JScrollPane spTestPayload;
	private JTextArea taResultPayload;
	private JScrollPane spResultPayload;
	
	private CloseableHttpClient  client;
	private RequestConfig requestConfig;
	private boolean isSucces = false;
	private String testPayload[] = {
			"123456","a123456","123456a","5201314",
			"111111","woaini1314","qq123456","123123",
			"000000","1qaz2wsx","1q2w3e4r","qwe123",
			"7758521","123qwe","a123123","123456aa",
			"woaini520","woaini","100200","1314520"
	};

	public GUI(IBurpExtenderCallbacks callbacks) {
		this.mCall = callbacks;
		this.helpers = callbacks.getHelpers();
		this.stdout = new PrintWriter(callbacks.getStdout(), true);
		this.stderr = new PrintWriter(callbacks.getStderr(), true);
		
		this.requestConfig = RequestConfig.custom().setConnectionRequestTimeout(300).setConnectTimeout(300).setSocketTimeout(300).build();
		this.client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		contentPane.add(panel, BorderLayout.NORTH);

		lbHost = new JLabel("Host:");
		panel.add(lbHost);

		tfHost = new JTextField();
		tfHost.setColumns(20);
		tfHost.setText("127.0.0.1");
		panel.add(tfHost);
		
		verticalStrut = Box.createVerticalStrut(20);
		panel.add(verticalStrut);

		lbPort = new JLabel("Port:");
		panel.add(lbPort);

		tfPort = new JTextField();
		tfPort.setText("1664");
		panel.add(tfPort);
		tfPort.setColumns(20);

		btnConn = new JButton("Connect");
		btnConn.setToolTipText("Test the connection phantomJS");
		btnConn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				GUI.this.testConnect();
			}
		});
		panel.add(btnConn);
		
		lbConnectInfo = new JLabel("IsConnect:");
		panel.add(lbConnectInfo);
		lbConnectStatus = new JLabel("noknow");
		lbConnectStatus.setForeground(new Color(0, 0, 255));
		panel.add(lbConnectStatus);
		
		btnTest = new JButton("Test");
		btnTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				testConnect();
				if(isSucces){
					GUI.this.Test();
					GUI.this.stdout.println("[+] test...");
				}else{
					JOptionPane.showMessageDialog(contentPane, "Please check if you can connect phantomJS!", "alert", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		btnTest.setToolTipText("Start test run");
		panel.add(btnTest);

		splitPane = new JSplitPane();
		splitPane.setDividerLocation(0.5);
		contentPane.add(splitPane, BorderLayout.CENTER);

		taTestPayload = new JTextArea();
		taTestPayload.setColumns(30);
		for (String payload : testPayload) {
			taTestPayload.append(payload + "\n");
		}
        spTestPayload = new JScrollPane(taTestPayload,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		splitPane.setLeftComponent(spTestPayload);

		taResultPayload = new JTextArea();
		taResultPayload.setColumns(30);
        spResultPayload = new JScrollPane(taResultPayload,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		splitPane.setRightComponent(spResultPayload);
		
		mCall.customizeUiComponent(panel);
		mCall.customizeUiComponent(btnTest);
		mCall.customizeUiComponent(btnConn);
		mCall.customizeUiComponent(taTestPayload);
		mCall.customizeUiComponent(splitPane);
		mCall.customizeUiComponent(contentPane);
	}
	
	public Component getComponet(){
		return contentPane;
	}
	
	// 测试
	private void Test() {
		taResultPayload.setText("");
		String[] payloads = taTestPayload.getText().split("\n");
		for (String payload : payloads) {
			String newPayload = sendTestPaylaod(payload);
			taResultPayload.append(newPayload + "\n");
		}
	}

	// 发送测试payload
	private String sendTestPaylaod(String payload) {
		String newPayload = null;
		String currentPayload = payload;

		HttpPost httpPost = new HttpPost(this.getURL());
		try {
			List nameValuePairs = new ArrayList(1);
			nameValuePairs.add(new BasicNameValuePair("payload", currentPayload));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			CloseableHttpResponse response = client.execute(httpPost);

			String responseAsString = EntityUtils.toString(response.getEntity());
			newPayload = responseAsString;
		} catch (Exception e) {
			stderr.println(e.getMessage());
		}
		return newPayload;
	}
	
	// 获取phantomJS
	public String getURL(){
		String URL;
		String host = tfHost.getText().trim();
		String port = tfPort.getText().trim();
		URL = String.format("http://%s:%s",host,port);
		return URL;
	}
	
	//测试连接phantomJS
	private void testConnect(){
		HttpGet httpGet = new HttpGet(this.getURL());
		try {
			
			CloseableHttpResponse response = client.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			String responseAsString = EntityUtils.toString(response.getEntity());
			int n = helpers.indexOf(responseAsString.getBytes(), "hello".getBytes(), false, 0, responseAsString.length());
			if((statusCode == 200)&&(n != -1)){
				stdout.println("[+] connect success!");
				lbConnectStatus.setText("True");
				isSucces = true;
				lbConnectStatus.setForeground(new Color(0,255,0));
			}else{
				stdout.println("[-] connect fail!");
				lbConnectStatus.setText("False");
				isSucces = false;
				lbConnectStatus.setForeground(new Color(255,0,0));
			}
		} catch (Exception e) {
			stderr.println(e.getMessage());
			stdout.println("[-] connect fail!");
			lbConnectStatus.setText("False");
			isSucces = false;
			lbConnectStatus.setForeground(new Color(255,0,0));
		}
	}
}
