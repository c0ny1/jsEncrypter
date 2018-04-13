<?php
include './rsa.php';

$USER = 'admin'; //账号
$PWD = 'admin'; //密码
$isSucess = false;

$m = $_POST['m'];
$username = $_POST['username'];
$password = $_POST['password'];

switch ($m) {
	/* base64 */
	case '0':
		if($username == $USER && $password == base64_encode($PWD)){
			$isSucess = true;
		}
		break;
	/* md5 */
	case '1':
		if($username == $USER && $password == md5($PWD)){
			$isSucess = true;
		}
		break;
	/* sha1 */
	case '2':
		if($username == $USER && $password == sha1($PWD)){
			$isSucess = true;
		}
		break;	
	/* sha256 */
	case '3':
 		if($username == $USER && $password == hash('sha256', $PWD)){
 			$isSucess = true;
 		}
		break;
	/* sha384 */
	case '4':
 		if($username == $USER && $password == hash('sha384', $PWD)){
 			$isSucess = true;
 		}
		break;
	/* sha512 */
	case '5':
 		if($username == $USER && $password == hash('sha512', $PWD)){
 			$isSucess = true;
 		}
		break;

	/* RSA */
	case '6':
		$rsa_private_key = file_get_contents('./key/rsa_private_key.pem'); 
		if($username == $USER && rsa_decode($password,$rsa_private_key) == $PWD){
			$isSucess = true;
		}
		break;

	default:
		break;
}

if($isSucess){
	echo "Login successful!";
}else{
	echo "Login failed!";
}

?>