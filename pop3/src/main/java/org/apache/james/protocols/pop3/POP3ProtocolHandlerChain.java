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

package org.apache.james.protocols.pop3;

import java.util.ArrayList;
import java.util.List;

import org.apache.james.protocols.api.handler.CommandDispatcher;
import org.apache.james.protocols.api.handler.ProtocolHandler;
import org.apache.james.protocols.api.handler.ProtocolHandlerChainImpl;
import org.apache.james.protocols.api.handler.WiringException;
import org.apache.james.protocols.pop3.core.CapaCmdHandler;
import org.apache.james.protocols.pop3.core.DeleCmdHandler;
import org.apache.james.protocols.pop3.core.ListCmdHandler;
import org.apache.james.protocols.pop3.core.NoopCmdHandler;
import org.apache.james.protocols.pop3.core.PassCmdHandler;
import org.apache.james.protocols.pop3.core.QuitCmdHandler;
import org.apache.james.protocols.pop3.core.RetrCmdHandler;
import org.apache.james.protocols.pop3.core.RsetCmdHandler;
import org.apache.james.protocols.pop3.core.StatCmdHandler;
import org.apache.james.protocols.pop3.core.StlsCmdHandler;
import org.apache.james.protocols.pop3.core.TopCmdHandler;
import org.apache.james.protocols.pop3.core.UidlCmdHandler;
import org.apache.james.protocols.pop3.core.UnknownCmdHandler;
import org.apache.james.protocols.pop3.core.UserCmdHandler;
import org.apache.james.protocols.pop3.core.WelcomeMessageHandler;
import org.apache.james.protocols.pop3.mailbox.MailboxFactory;

public class POP3ProtocolHandlerChain extends ProtocolHandlerChainImpl{

    public POP3ProtocolHandlerChain() {
        super();
    }

        
    /**
     * The {@link MailboxFactory} to use. If a <code>not null</code> {@link MailboxFactory} is given, the {@link POP3ProtocolHandlerChain}
     * will add all default handlers
     * 
     * @param mailboxFactory
     * @throws WiringException 
     */
    public POP3ProtocolHandlerChain(MailboxFactory mailboxFactory) throws WiringException {
        if (mailboxFactory != null) {
            addAll(initDefaultHandlers(mailboxFactory));      
            wireExtensibleHandlers();
        }
    }
    
    protected List<ProtocolHandler> initDefaultHandlers(MailboxFactory mailboxFactory) {
        List<ProtocolHandler> handlers = new ArrayList<ProtocolHandler>();
        handlers.add(new CapaCmdHandler());
        handlers.add(new UserCmdHandler());
        handlers.add(new PassCmdHandler(mailboxFactory));
        handlers.add(new ListCmdHandler());
        handlers.add(new UidlCmdHandler());
        handlers.add(new RsetCmdHandler());
        handlers.add(new DeleCmdHandler());
        handlers.add(new NoopCmdHandler());
        handlers.add(new RetrCmdHandler());
        handlers.add(new TopCmdHandler());
        handlers.add(new StatCmdHandler());
        handlers.add(new QuitCmdHandler());
        handlers.add(new WelcomeMessageHandler());
        handlers.add(new UnknownCmdHandler());
        handlers.add(new StlsCmdHandler());
        handlers.add(new CommandDispatcher<POP3Session>());
       
        return handlers;
    }
}
