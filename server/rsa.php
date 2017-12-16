<?php
/** 
 * 公钥加密 
 * 
 * @param string 明文 
 * @param string 公钥 
 * @return string 密文
 */  
function rsa_encode($plaintext,$public_key)  
{ 
    $pu_key = openssl_get_publickey($public_key);  
      
    if (openssl_public_encrypt($plaintext, $ciphertext, $pu_key))  
    {
        return base64_encode($ciphertext);  
    }else{
        return null;
    }
}

/** 
 * 私钥解密 
 * 
 * @param string 密文
 * @param string 私钥
 * @return string 明文 
 */  
function rsa_decode($ciphertext,$private_key)  
{ 
    $pi_key = openssl_get_privatekey($private_key);
    $ciphertext = base64_decode($ciphertext);
    
    if (openssl_private_decrypt($ciphertext, $plaintext, $pi_key, OPENSSL_PKCS1_PADDING))  
    {
        return $plaintext;
    }else{
        return null;
    }
}