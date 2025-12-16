package com.aizihe.codeaai.component.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseMailComponent implements MailComponent {

    private static final Logger log = LoggerFactory.getLogger(BaseMailComponent.class);



    protected abstract void doSendMailVerifyCode(String to, String code);

}
