/*
 * JINAH Project - Java Is Not A Hammer
 * http://obadaro.com/jinah
 *  
 * Copyright (C) 2010-2012 Roberto Badaro 
 * and individual contributors by the @authors tag.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.obadaro.jinah.sql;

import com.obadaro.jinah.common.JinahException;

/**
 * @author Roberto Badaro
 */
public class JinahSqlException extends JinahException {

    private static final long serialVersionUID = 1L;

    public JinahSqlException() {
        super();
    }

    /**
     * @param message
     */
    public JinahSqlException(final String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public JinahSqlException(final Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public JinahSqlException(final String message, final Throwable cause) {
        super(message, cause);
    }

}