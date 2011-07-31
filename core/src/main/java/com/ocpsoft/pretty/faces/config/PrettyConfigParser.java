/*
 * Copyright 2010 Lincoln Baxter, III
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

package com.ocpsoft.pretty.faces.config;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.SAXException;

/**
 * Parses Pretty Faces configuraion from an input stream.
 * 
 * @author Aleksei Valikov
 */
public interface PrettyConfigParser
{

    /**
     * Parses the input stream and feeds configuration elements to the builder.
     * 
     * @param builder
     *            target builder, must not be <code>null</code>.
     * @param resource
     *            input stream to be parsed, must not be <code>null</code>
     * @param resource
     *            if the parser should perform validation of the xml file
     * @throws IOException
     *             If input stream could not be read.
     * @throws SAXException
     *             If configuration could not be parsed.
     */
    public void parse(PrettyConfigBuilder builder, InputStream resource, boolean validate) 
          throws IOException, SAXException;
}
