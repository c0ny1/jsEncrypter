package burp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class HttpClient {
    private String url = "";
    private String data = "";
    private Integer connTimeout = 3000;
    private Integer readTimeout = 3000;
    private String ua = "jsEncrypter client";
    private Integer statusCode = 0;
    private String rspData = null;

    public HttpClient(String url){
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getConnTimeout() {
        return connTimeout;
    }

    public void setConnTimeout(Integer connTimeout) {
        this.connTimeout = connTimeout;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    public String getUa() {
        return ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getRspData() {
        return rspData;
    }

    public void sendGet(){
        String rspData = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            URLConnection urlConn = realUrl.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) urlConn;
            httpConn.setRequestProperty("user-agent", this.ua);
            httpConn.setConnectTimeout(this.connTimeout);
            httpConn.setReadTimeout(this.readTimeout);
            httpConn.connect();

            this.statusCode = httpConn.getResponseCode();
            in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                rspData += line;
            }
        }catch (MalformedURLException e){
            rspData =  "jsEncrypter wanning:" + e.getMessage();
        }catch (IOException e){
            rspData =  "jsEncrypter wanning:" + e.getMessage();
        }
        this.rspData = rspData;
    }

    public  void sendPost(){
        PrintWriter out = null;
        String rspData = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(this.url);
            URLConnection urlConn = realUrl.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) urlConn;
            httpConn.setRequestProperty("user-agent", this.ua);
            httpConn.setConnectTimeout(this.connTimeout);
            httpConn.setReadTimeout(this.readTimeout);

            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            out = new PrintWriter(httpConn.getOutputStream());
            out.print(data);
            httpConn.connect();
            out.flush();

            this.statusCode = httpConn.getResponseCode();
            in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                rspData += line;
            }
        }catch (MalformedURLException e){
            rspData =  "jsEncrypter wanning:" + e.getMessage();
        }catch (IOException e){
            rspData = "jsEncrypter wanning:" + e.getMessage();
        }
        this.rspData = rspData;
    }
}
