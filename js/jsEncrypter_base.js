/**
 * author: c0ny1
 * date: 2017-12-16
 */
 
var webserver = require('webserver');
server = webserver.create();

var host = '127.0.0.1';
var port = '1664';

// 加载实现加密算法的js脚本
var wasSuccessful = phantom.injectJs('xxx.js');/*引入实现加密的js文件*/

// 处理函数
function js_encrypt(payload){
	var newpayload;
	/**********在这里编写调用加密函数进行加密的代码************/

	/**********************************************************/
	return newpayload;
}

if(wasSuccessful){
	console.log("[*] load js successful");
	console.log("[!] ^_^");
	console.log("[*] jsEncrypterJS start!");
	console.log("[+] address: http://"+host+":"+port);
}else{
	console.log('[*] load js fail!');
}

var service = server.listen(host+':'+port,function(request, response){
	if(request.method == 'POST'){
		var payload = request.post['payload'];
        var encrypt_payload = js_encrypt(payload); 
        console.log('[+] ' + payload + ':' + encrypt_payload);
		response.statusCode = 200;
  		response.write(encrypt_payload.toString());
        response.close();
	}else{
		  response.statusCode = 200;
  		  response.write("^_^\n\rhello jsEncrypter!");
          response.close();
	}
});