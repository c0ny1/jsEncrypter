# jsEncrypter
一个用于加密传输爆破的Burp Suite插件

## 编译

该项目依赖包如下：

```
mkdir ./jsEncrypter/lib
cd jsEncrypter/lib 
wget http://central.maven.org/maven2/commons-codec/commons-codec/1.6/commons-codec-1.6.jar
wget http://central.maven.org/maven2/commons-logging/commons-logging/1.1.3/commons-logging-1.1.3.jar
wget http://central.maven.org/maven2/org/apache/httpcomponents/fluent-hc/4.3.6/fluent-hc-4.3.6.jar
wget http://central.maven.org/maven2/org/apache/httpcomponents/httpclient/4.3.6/httpclient-4.3.6.jar
wget http://central.maven.org/maven2/org/apache/httpcomponents/httpclient-cache/4.3.6/httpclient-cache-4.3.6.jar
wget http://central.maven.org/maven2/org/apache/httpcomponents/httpcore/4.3.3/httpcore-4.3.3.jar
wget http://central.maven.org/maven2/org/apache/httpcomponents/httpmime/4.3.6/httpmime-4.3.6.jar
```

导入eclipse中编译

## 安装

![加载](./doc/load.png)

![加载](./doc/tab.png)

## 使用
#### 运行靶机
项目提供了一个用php编写的靶机（jsEncrypter/server）,靶机提供了7个算法对密码进行加密后传输，后台解密，最后进行密码匹配。

* base64
* md5
* sha1
* sha254
* sha384
* sha512
* RSA

![靶机](./doc/server.png)

#### 编写phantomJS运行脚本

`jsEncrypter/js/jsEncrypter_base.js`为插件phantomJS脚本模板。我们只需要将实现加密算法的js文件引入模板脚本，并在模板脚本的js_encrypt函数体中完成对加密函数的调用。

````
......
......
......
// 加载实现加密算法的js脚本
var wasSuccessful = phantom.injectJs('xxx.js');/*引入实现加密的js文件*/

// 处理函数
function js_encrypt(payload){
	var newpayload;
	/**********在这里编写调用加密函数进行加密的代码************/

	/**********************************************************/
	return newpayload;
}
......
......
......
````

项目jsEncrypter/server/TestScript目录下是编写好的对应靶机各个加密算法的phantomJS脚本，可以参考！

#### 运行phantomJS并测试
运行phantomJS
```
>phantomJS.exe jsEncrypter_sha1.js 
```

测试的目的是为了确保我们编写的phantomJS脚本能够正常加密payload。

![运行phantomJS并测试](./doc/test.gif)

#### 抓包暴力破解
![抓包暴力破解](./doc/crack.gif)

## 更多
[编写加密传输爆破插件jsEncrypter](http://gv7.me/articles/2017/jsEncrypter/)