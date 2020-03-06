package burp;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.FlowLayout;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.UnsupportedEncodingException;

public class GUI{
	private JPanel contentPane;
	private JLabel lbHost;
	private JTextField tfHost;
	private JLabel lbPort;
	private JTextField tfPort;
	private JLabel lbTimeout;
	private JTextField tfTimeout;
	private JButton btnConn;
	private JLabel lbConnectInfo;
	private JLabel lbConnectStatus;
	private JButton btnTest;
	private JSplitPane splitPane;
	private Component verticalStrut;
	private JTextArea taTestPayload;
	//private JTextPane taTestPayload;
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

	public GUI() {
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
		tfPort.setColumns(10);

		verticalStrut = Box.createVerticalStrut(20);
		panel.add(verticalStrut);
		lbTimeout = new JLabel("Timeout:");
		panel.add(lbTimeout);
		tfTimeout = new JTextField();
		tfTimeout.setText("5000");
		panel.add(tfTimeout);
		tfTimeout.setColumns(10);

		btnConn = new JButton("Connect");
		btnConn.setToolTipText("Test the connection phantomJS");
		btnConn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				GUI.this.TestConnect();
			}
		});
		panel.add(btnConn);
		
		lbConnectInfo = new JLabel("IsConnect:");
		panel.add(lbConnectInfo);
		lbConnectStatus = new JLabel("unknown");
		lbConnectStatus.setForeground(new Color(0, 0, 255));
		panel.add(lbConnectStatus);
		
		btnTest = new JButton("Test");
		btnTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				GUI.this.TestConnect();
				if(isSucces){
					GUI.this.TestPayload();
					BurpExtender.stdout.println("[+] test...");
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
		String tmp = "";
		for (String payload : testPayload) {
			//JTextArea.append 会导致汉化版无法换行
			//taTestPayload.append(payload + System.lineSeparator());
			 payload += System.lineSeparator();
			 tmp += payload;
		}
		taTestPayload.setText(tmp);

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
		
		BurpExtender.callbacks.customizeUiComponent(panel);
		BurpExtender.callbacks.customizeUiComponent(btnTest);
		BurpExtender.callbacks.customizeUiComponent(btnConn);
		BurpExtender.callbacks.customizeUiComponent(taTestPayload);
		BurpExtender.callbacks.customizeUiComponent(splitPane);
		BurpExtender.callbacks.customizeUiComponent(contentPane);
	}
	
	public Component getComponet(){
		return contentPane;
	}

	public Integer getTimeout(){
		return Integer.valueOf(tfTimeout.getText());
	}

	// 发送连接测试，确定是否能连接加密服务端
	private void TestConnect(){
		boolean isConn = Utils.sendTestConnect();
		if(isConn){
			BurpExtender.stdout.println("[+] connect success!");
			lbConnectStatus.setText("True");
			isSucces = true;
			lbConnectStatus.setForeground(new Color(0,255,0));
		}else{
			BurpExtender.stdout.println("[-] connect fail!");
			lbConnectStatus.setText("False");
			isSucces = false;
			lbConnectStatus.setForeground(new Color(255,0,0));
		}
	}

	
	// 发送测试payload，确定是否加密成功
	private void TestPayload() {
		taResultPayload.setText("");
		btnTest.setEnabled(false);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				String[] payloads = taTestPayload.getText().split("\r|\n");
				String tmp = "";
				for (String payload : payloads) {
					if(payload.equals(null)||payload.equals("")){
						continue;
					}
					String newPayload = Utils.sendPayload(payload);
					newPayload += System.lineSeparator();
					tmp += newPayload;
				}

				// 如果是Windows，先UTF-8编码在显示，解决Windows上乱码问题
				if(System.getProperty("os.name").toLowerCase().contains("win")){
					tmp = Utils.transformCharset(tmp,"UTF-8");
				}

				taResultPayload.setText(tmp);
				btnTest.setEnabled(true);
			}
		});
	}

	// 获取phantomJS
	public String getURL(){
		String URL;
		String host = tfHost.getText().trim();
		String port = tfPort.getText().trim();
		URL = String.format("http://%s:%s",host,port);
		return URL;
	}
}
