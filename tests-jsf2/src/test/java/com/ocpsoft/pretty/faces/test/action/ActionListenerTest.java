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

import javax.faces.context.FacesContext;

import org.jboss.arquillian.MavenArtifactResolver;
import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.jsfunit.jsfsession.JSFClientSession;
import org.jboss.jsfunit.jsfsession.JSFServerSession;
import org.jboss.jsfunit.jsfsession.JSFSession;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.test.MetasyntacticVariable;

@RunWith(Arquillian.class)
public class ActionListenerTest
{
   @Deployment
   public static Archive<?> createDeployment()
   {
      return ShrinkWrap.create(WebArchive.class, "test.war")
               .addResource("action/page1.xhtml", "page1.xhtml")
               .addResource("action/page2.xhtml", "page2.xhtml")
               .addClasses(DataBean.class, PageOne.class, PageTwo.class, MetasyntacticVariable.class)
               .addWebResource("faces-config.xml")
               .addWebResource("pretty-config.xml")
               .addLibrary(MavenArtifactResolver.resolve(
                        "com.ocpsoft:prettyfaces-jsf2:3.3.1-SNAPSHOT"))
               .setWebXML("jsf-web.xml");
   }

   private JSFSession jsfSession;
   private JSFServerSession server;
   private JSFClientSession client;

   @Before
   public void pageOne() throws Exception {
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
      client.click("button");
      assertDestination("injectedPath");
      assertEquals("button", server.getManagedBeanValue("#{pageTwo.paramString}"));
   }

}
