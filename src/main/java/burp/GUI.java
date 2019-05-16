package burp;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.FlowLayout;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;

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
			taTestPayload.append(payload + "\n\r");
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
		btnTest.setEnabled(false);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				String[] payloads = taTestPayload.getText().split("\n\r");
				for (String payload : payloads) {
					String newPayload = sendTestPaylaod(payload);
					taResultPayload.append(newPayload + "\n\r");
				}
				btnTest.setEnabled(true);
			}
		});
	}

	// 发送测试payload
	private String sendTestPaylaod(String payload) {
		String newPayload = null;
		try {
			HttpClient hc = new HttpClient(this.getURL());
			hc.setConnTimeout(3000);
			hc.setReadTimeout(3000);
			String data = "payload=" + payload;
			hc.setData(data);
			hc.sendPost();
			newPayload = hc.getRspData();
		} catch (Exception e) {
			stderr.println(e.getMessage());
			newPayload = e.getMessage();
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
		try {
			HttpClient hc = new HttpClient(this.getURL());
			hc.setReadTimeout(3000);
			hc.setConnTimeout(3000);
			hc.sendGet();
			int n = helpers.indexOf(hc.getRspData().getBytes(), "hello".getBytes(), false, 0, hc.getRspData().length());
			if((hc.getStatusCode() == 200)&&(n != -1)){
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
