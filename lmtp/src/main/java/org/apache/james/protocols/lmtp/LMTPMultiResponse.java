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

package org.apache.james.protocols.lmtp;

import java.util.ArrayList;
import java.util.List;

import org.apache.james.protocols.api.Response;

public class LMTPMultiResponse implements Response {

    private List<Response> responses = new ArrayList<Response>();

    public LMTPMultiResponse(Response response) {
        addResponse(response);
    }
    
    public void addResponse(Response response) {
        this.responses .add(response);
        
    }
    
    @Override
    public String getRetCode() {
        return responses.get(0).getRetCode();
    }

    @Override
    public List<CharSequence> getLines() {
        List<CharSequence> lines = new ArrayList<CharSequence>();
        for (Response response: responses) {
            lines.addAll(response.getLines());
        }
        return lines;
    }


    @Override
    public boolean isEndSession() {
        for (Response response: responses) {
            if (response.isEndSession()) {
                return true;
            }
        }
        return false;
    }

}
