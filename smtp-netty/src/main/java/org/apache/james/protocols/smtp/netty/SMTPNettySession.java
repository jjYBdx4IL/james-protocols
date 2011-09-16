/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/
package org.apache.james.protocols.smtp.netty;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.james.protocols.api.LineHandler;
import org.apache.james.protocols.api.ProtocolTransport;
import org.apache.james.protocols.impl.AbstractSession;
import org.apache.james.protocols.smtp.SMTPConfiguration;
import org.apache.james.protocols.smtp.SMTPSession;
import org.slf4j.Logger;

/**
 * {@link SMTPSession} implementation for use with Netty
 */
public class SMTPNettySession extends AbstractSession implements SMTPSession {
    public final static String SMTP_SESSION = "SMTP_SESSION";

    private boolean relayingAllowed;

    private Map<String, Object> connectionState;

    private SMTPConfiguration theConfigData;

    public SMTPNettySession(SMTPConfiguration theConfigData, Logger logger, ProtocolTransport transport) {
        super(logger, transport);
        this.theConfigData = theConfigData;
        connectionState = new HashMap<String, Object>();

        relayingAllowed = theConfigData.isRelayingAllowed(getRemoteIPAddress());
    }

    /**
     * @see org.apache.james.protocols.smtp.SMTPSession#getConnectionState()
     */
    public Map<String, Object> getConnectionState() {
        return connectionState;
    }

    /**
     * @see org.apache.james.protocols.smtp.SMTPSession#getState()
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getState() {
        Map<String, Object> res = (Map<String, Object>) getConnectionState().get(SMTPSession.SESSION_STATE_MAP);
        if (res == null) {
            res = new HashMap<String, Object>();
            getConnectionState().put(SMTPSession.SESSION_STATE_MAP, res);
        }
        return res;
    }

    /**
     * @see org.apache.james.protocols.smtp.SMTPSession#isRelayingAllowed()
     */
    public boolean isRelayingAllowed() {
        return relayingAllowed;
    }

    /**
     * @see org.apache.james.protocols.smtp.SMTPSession#resetState()
     */
    public void resetState() {
        // remember the ehlo mode between resets
        Object currentHeloMode = getState().get(CURRENT_HELO_MODE);

        getState().clear();

        // start again with the old helo mode
        if (currentHeloMode != null) {
            getState().put(CURRENT_HELO_MODE, currentHeloMode);
        }
    }

    /**
     * @see org.apache.james.protocols.smtp.SMTPSession#popLineHandler()
     */
    public void popLineHandler() {
        transport.popLineHandler();
    }

    /**
     * @see org.apache.james.protocols.smtp.SMTPSession#pushLineHandler(org.apache.james.smtpserver.protocol.LineHandler)
     */
    public void pushLineHandler(LineHandler<SMTPSession> overrideCommandHandler) {
        transport.pushLineHandler(overrideCommandHandler, this);
    }

    /**
     * @see org.apache.james.protocols.smtp.SMTPSession#getHelloName()
     */
    public String getHelloName() {
        return theConfigData.getHelloName();
    }

    /**
     * @see org.apache.james.protocols.smtp.SMTPSession#getMaxMessageSize()
     */
    public long getMaxMessageSize() {
        return theConfigData.getMaxMessageSize();
    }

    /**
     * @see org.apache.james.protocols.smtp.SMTPSession#getRcptCount()
     */
    @SuppressWarnings("unchecked")
    public int getRcptCount() {
        int count = 0;

        // check if the key exists
        if (getState().get(SMTPSession.RCPT_LIST) != null) {
            count = ((Collection) getState().get(SMTPSession.RCPT_LIST)).size();
        }

        return count;
    }

    /**
     * @see org.apache.james.protocols.smtp.SMTPSession#getSMTPGreeting()
     */
    public String getSMTPGreeting() {
        return theConfigData.getSMTPGreeting();
    }

    /**
     * @see org.apache.james.protocols.smtp.SMTPSession#isAuthSupported()
     */
    public boolean isAuthSupported() {
        return theConfigData.isAuthRequired(socketAddress.getAddress().getHostAddress());
    }

    /**
     * @see org.apache.james.protocols.smtp.SMTPSession#setRelayingAllowed(boolean)
     */
    public void setRelayingAllowed(boolean relayingAllowed) {
        this.relayingAllowed = relayingAllowed;
    }

    /**
     * @see org.apache.james.protocols.smtp.SMTPSession#sleep(long)
     */
    public void sleep(long ms) {
        // session.getFilterChain().addAfter("connectionFilter",
        // "tarpitFilter",new TarpitFilter(ms));
    }

    /**
     * @see org.apache.james.protocols.smtp.SMTPSession#useAddressBracketsEnforcement()
     */
    public boolean useAddressBracketsEnforcement() {
        return theConfigData.useAddressBracketsEnforcement();
    }

    /**
     * @see org.apache.james.protocols.smtp.SMTPSession#useHeloEhloEnforcement()
     */
    public boolean useHeloEhloEnforcement() {
        return theConfigData.useHeloEhloEnforcement();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.james.protocols.smtp.SMTPSession#getPushedLineHandlerCount()
     */
    public int getPushedLineHandlerCount() {
        return transport.getPushedLineHandlerCount();
    }
    
}