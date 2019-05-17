package burp;

public class Utils {
    public static String sendPayload(String payload){
        String encryptPayload = "";
        HttpClient hc = new HttpClient(BurpExtender.gui.getURL());
        hc.setConnTimeout(BurpExtender.gui.getTimeout());
        hc.setReadTimeout(BurpExtender.gui.getTimeout());
        String data = "payload=" + payload;
        hc.setData(data);
        hc.sendPost();
        encryptPayload = hc.getRspData();
        return encryptPayload;
    }
}
