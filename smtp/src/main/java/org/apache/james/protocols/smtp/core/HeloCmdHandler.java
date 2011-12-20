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

package org.apache.james.protocols.smtp.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.james.protocols.api.Response;
import org.apache.james.protocols.smtp.SMTPResponse;
import org.apache.james.protocols.smtp.SMTPRetCode;
import org.apache.james.protocols.smtp.SMTPSession;
import org.apache.james.protocols.smtp.dsn.DSNStatus;
import org.apache.james.protocols.smtp.hook.HeloHook;
import org.apache.james.protocols.smtp.hook.HookResult;

/**
 * Handles HELO command
 */
public class HeloCmdHandler extends AbstractHookableCmdHandler<HeloHook> {

	private static final String COMMAND_NAME = "HELO";
    /**
     * The name of the command handled by the command handler
     */
    private static final Collection<String> COMMANDS = Collections.unmodifiableCollection(Arrays.asList(COMMAND_NAME));

    /**
     * @see org.apache.james.protocols.api.handler.CommandHandler#getImplCommands()
     */
    public Collection<String> getImplCommands() {
        return COMMANDS;
    }

    /**
     * @see org.apache.james.protocols.smtp.core.AbstractHookableCmdHandler#doCoreCmd(org.apache.james.protocols.smtp.SMTPSession,
     *      java.lang.String, java.lang.String)
     */
    protected Response doCoreCmd(SMTPSession session, String command,
            String parameters) {
        session.getConnectionState().put(SMTPSession.CURRENT_HELO_MODE,
        		COMMAND_NAME);
        StringBuilder response = new StringBuilder();
        response.append(session.getConfiguration().getHelloName()).append(
                " Hello ").append(parameters).append(" [").append(
                session.getRemoteAddress().getAddress().getHostAddress()).append("])");
        return new SMTPResponse(SMTPRetCode.MAIL_OK, response);
    }

    /**
     * @see org.apache.james.protocols.smtp.core.AbstractHookableCmdHandler#doFilterChecks(org.apache.james.protocols.smtp.SMTPSession,
     *      java.lang.String, java.lang.String)
     */
    protected Response doFilterChecks(SMTPSession session, String command,
            String parameters) {
        session.resetState();

        if (parameters == null) {
            return new SMTPResponse(SMTPRetCode.SYNTAX_ERROR_ARGUMENTS,
                    DSNStatus.getStatus(DSNStatus.PERMANENT,
                            DSNStatus.DELIVERY_INVALID_ARG)
                            + " Domain address required: " + COMMAND_NAME);
        } else {
            // store provided name
            session.getState().put(SMTPSession.CURRENT_HELO_NAME, parameters);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    protected Class<HeloHook> getHookInterface() {
        return HeloHook.class;
    }


    /**
     * {@inheritDoc}
     */
    protected HookResult callHook(HeloHook rawHook, SMTPSession session, String parameters) {
        return rawHook.doHelo(session, parameters);
    }


}
