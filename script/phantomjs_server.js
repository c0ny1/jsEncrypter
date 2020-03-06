/**
 * author: c0ny1
 * date: 2017-12-16
 * last update: 2020-03-03
 */
var fs = require('fs');
var webserver = require('webserver');
server = webserver.create();

var logfile = 'jsEncrypter.log';
var host = '127.0.0.1';
var port = '1664';

/* 1.在这引入实现加密所有js文件,注意引入顺序和网页一致 */
loadScript("script-1.js");
loadScript("script-2.js");
load_script("script-n.js");
/**********************************************/

function loadScript(scriptName) {
    var isSuccess = phantom.injectJs(scriptName);
    if(isSuccess){
        console.log("[*] load " + scriptName + " successful")
    }else{
        console.log("[!] load " + scriptName + " fail")
        console.log("[*] phantomjs server exit");
        phantom.exit();
    }
}

function jsEncrypt(burp_payload){
	var new_payload;
	/* 2.在这里编写调用加密函数进行加密的代码,并把结果赋值给new_payload */

	/*********************************************************/
	return new_payload;
}

console.log("[*] Phantomjs server for jsEncrypter started successfully!");
console.log("[*] address: http://"+host+":"+port);
console.log("[!] ^_^");

var service = server.listen(host+':'+port,function(request, response){
 	try{
		if(request.method == 'POST'){
			var payload = request.post['payload'];
			var encrypt_payload = jsEncrypt(payload);
			var log = payload + ':' + encrypt_payload;
			console.log('[+] ' + log);
            fs.write(logfile,log + '\n', 'w+');
			response.statusCode = 200;
            response.setEncoding('UTF-8');
			response.write(encrypt_payload.toString());
			response.close();
		}else{
			  response.statusCode = 200;
			  response.setEncoding('UTF-8');
			  response.write("^_^\n\rhello jsEncrypter!");
			  response.close();
		}
	}catch(e){
		//console.log('[Error]'+e.message+' happen '+e.line+'line');
		console.log('\n-----------------Error Info--------------------');
		var fullMessage = "Message: "+e.toString() + ':'+ e.line;
		for (var p in e) {
			fullMessage += "\n" + p.toUpperCase() + ": " + e[p];
		} 
		console.log(fullMessage);
		console.log('---------------------------------------------');
        response.statusCode = 200;
        response.setEncoding('UTF-8');
        response.write(fullMessage);
        response.close();
		console.log('[*] phantomJS exit!');
		phantom.exit();
    }	
});