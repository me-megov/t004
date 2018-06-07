/*
 * Copyright 2018 megov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.megov.emc.t004.entities;

import me.megov.emc.t004.exceptions.T004Exception;

/**
 *
 * @author megov
 */
public class LogLine {

    private final IPvXTuple addr;
    private final long byteCount;
    
    public LogLine(IPvXTuple _addr, long _byteCount) throws T004Exception {
        this.addr = _addr;
        this.byteCount = _byteCount;
    }  

    /**
     * @return the addr
     */
    public IPvXTuple getAddr() {
        return addr;
    }

    /**
     * @return the byteCount
     */
    public long getByteCount() {
        return byteCount;
    }
    
    
}
