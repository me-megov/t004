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
package me.megov.emc.t004.exceptions;

/**
 *
 * @author megov
 */
public class T004Exception extends Exception {

    public T004Exception() {
    }

    public T004Exception(Throwable th) {
        super(th);
    }

    public T004Exception(String str) {
        super(str);
    }
    
    public T004Exception(String message, Throwable cause) {    
        super(message, cause);
    }

}
