/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 */

package com.alipay.android.msp.demo;

//
// 请参考 Android平台安全支付服务(msp)应用开发接口(4.2 RSA算法签名)部分，并使用压缩包中的openssl RSA密钥生成工具，生成一套RSA公私钥。
// 这里签名时，只需要使用生成的RSA私钥。
// Note: 为安全起见，使用RSA私钥进行签名的操作过程，应该尽量放到商家服务器端去进行。
public final class Keys {

    // 合作商户ID，用签约支付宝账号登录www.alipay.com后，在商家服务页面中获取。
	public static final String DEFAULT_PARTNER = "2088011165209331";

    // 商户收款的支付宝账号
	public static final String DEFAULT_SELLER = "admin@uulm.com";

    // 商户（RSA）私钥
	public static final String PRIVATE = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIdUt3wxUPLKxQgZCMe2c1GlxKT6oIHhClJ8nMtQuTBPi8mthqcINSs9jELuYg2bWdkGOZz3JdasS4qx6QxiZdYv7Q75L2wIfQYc7dspS7xjMnXvM0/Nn+cvNpqtbS4EstZL+cH9da+dpV/AqiscQf3X05yXLYvw8YUkamoV7gkRAgMBAAECgYBOaiIfThwPcrqUJJ1XyzoqtxioV6ilWRY4EYhwrod/nLfs1iVNp43XTkR1i9c49ykqfVbowrQkHZVv+gKFcIZ8o/q84wf/pIL+yHM/ZZapzCI/1BZ5isZC41ok5aDZ9DS5qLv4jT6r97VTvvGtPnGjmvQDwzF84Z2YK1pOGbrEGQJBAPIT3YkM7kQ+oQu+P0G1i6bmhSNTkCExia2olrKetdWcVcMsoQcVfJvH/03vRxUIjIgT4h/T7vhrt0ymi/LnEF8CQQCPHTVj2/wOu4VcyND7ML/FNWJLEdqM0KT7SPRT3OKxGVkPYG8yDm5DGY392cB5FOPfLsTQHPP7Z6F6SO8qXpyPAkBzcsPpc5BCXbgCWLXkSYeTjkaahWSsX+qwNkHAZ+6CNC+2iBc3wKiPxr7cVSwKPOyAjTL/SNyj/wMI/DisHERzAkAPOz2WbjBKZRzRfjH3uf1/AKqAcNsAx6f3cpFGZ6jczmTTJSFQg4aqr4qULNu2R0nlU/XwbFRvOl1ORXrm2IDzAkBjlADICx0/xb6D4JsPwyDTWtl1yLZBl7w4h34B5vfNcCJrZFodwBXQYNI3TW0BfvoLdzSmwbhMeQCFQaWT0rLG";
    // 支付宝（RSA）公钥
	public static final String PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

    //商户公钥
	//MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCHVLd8MVDyysUIGQjHtnNRpcSk+qCB4QpSfJzLULkwT4vJrYanCDUrPYxC7mINm1nZBjmc9yXWrEuKsekMYmXWL+0O+S9sCH0GHO3bKUu8YzJ17zNPzZ/nLzaarW0uBLLWS/nB/XWvnaVfwKorHEH919Ocly2L8PGFJGpqFe4JEQIDAQAB 
}
