/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ocpsoft.pretty.faces.test.action;

import static org.junit.Assert.assertEquals;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.jsfunit.jsfsession.JSFClientSession;
import org.jboss.jsfunit.jsfsession.JSFServerSession;
import org.jboss.jsfunit.jsfsession.JSFSession;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.test.MetasyntacticVariable;
import com.ocpsoft.pretty.faces.test.PrettyFacesTestBase;

@RunWith(Arquillian.class)
public class ActionListenerTest
{
   @Deployment
   public static WebArchive createDeployment()
   {
      return PrettyFacesTestBase.createDeployment()
               .addResource("action/page1.xhtml", "page1.xhtml")
               .addResource("action/page2.xhtml", "page2.xhtml")
               .addClasses(DataBean.class, PageOne.class, PageTwo.class, MetasyntacticVariable.class)
               .addWebResource("pretty-config.xml");
   }

   private JSFSession jsfSession;
   private JSFServerSession server;
   private JSFClientSession client;

   /**
    * Cannot be done using a @Before annotation because JSFUnit setup is not fully available
    * @throws Exception
    */
   private void pageOneSetup() throws Exception {
      jsfSession = new JSFSession("/home");
      server = jsfSession.getJSFServerSession();
      client = jsfSession.getJSFClientSession();
      assertPretty("home");
      client.setValue("textValue", "data");
   }

   private void assertPretty(String expectedMapping) {
      assertEquals(expectedMapping, PrettyContext.getCurrentInstance(server.getFacesContext()).getCurrentMapping().getId());
   }

   private void assertDestination(String expectedMapping) {
      assertPretty(expectedMapping);
      assertEquals("data", server.getManagedBeanValue("#{pageTwo.data.value}"));
   }

   @Test
   public void testBasicPathParameterAction() throws Exception {
      pageOneSetup();

      client.click("button");
      assertDestination("injectedPath");
      assertEquals("button", server.getManagedBeanValue("#{pageTwo.paramString}"));
   }

}
