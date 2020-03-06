package burp;

import com.sun.org.apache.bcel.internal.generic.NEW;

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

    public static boolean sendTestConnect(){
        HttpClient hc = new HttpClient(BurpExtender.gui.getURL());
        hc.setReadTimeout(Integer.valueOf(BurpExtender.gui.getTimeout()));
        hc.setConnTimeout(Integer.valueOf(BurpExtender.gui.getTimeout()));
        hc.sendGet();
        int n = BurpExtender.helpers.indexOf(hc.getRspData().getBytes(), "hello".getBytes(), false, 0, hc.getRspData().length());
        if((hc.getStatusCode() == 200)&&(n != -1)){
            return true;
        }else{
            return false;
        }
    }

    public static String getBanner(){
        String bannerInfo =
                          "[+] " + BurpExtender.extensionName + " is loaded\n"
                        + "[+] ^_^\n"
                        + "[+]\n"
                        + "[+] #####################################\n"
                        + "[+]    " + BurpExtender.extensionName + " v" + BurpExtender.version +"\n"
                        + "[+]    anthor: c0ny1\n"
                        + "[+]    email:  root@gv7.me\n"
                        + "[+]    github: http://github.com/c0ny1/jsEncrypter\n"
                        + "[+] ####################################";
        return bannerInfo;
    }

    /**
     * 转换字符串编码
     */
    public static String transformCharset(String str,String charsetName){
        String newStr = null;
        try {
            newStr = new String(str.getBytes(), charsetName);
        }catch (Exception e){
            BurpExtender.stdout.println("[-] Utils.transformCharset erro: " + e.getMessage());
            newStr = str;
        }
        return newStr;
    }
}
