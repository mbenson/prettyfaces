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

package com.ocpsoft.pretty.faces.application;

import java.io.IOException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import com.ocpsoft.logging.Logger;
import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.PrettyException;
import com.ocpsoft.pretty.faces.beans.ExtractedValuesURLBuilder;
import com.ocpsoft.pretty.faces.config.PrettyConfig;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.url.QueryString;
import com.ocpsoft.pretty.faces.url.URL;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class PrettyRedirector
{
   private static final Logger log = Logger.getLogger(PrettyRedirector.class);
   private final ExtractedValuesURLBuilder builder = new ExtractedValuesURLBuilder();

   public static PrettyRedirector getInstance()
   {
      return new PrettyRedirector();
   }

   public boolean redirect(final FacesContext context, final String action)
   {
      try
      {
         PrettyContext prettyContext = PrettyContext.getCurrentInstance(context);
         PrettyConfig config = prettyContext.getConfig();
         ExternalContext externalContext = context.getExternalContext();

         String contextPath = prettyContext.getContextPath();
         if (PrettyContext.PRETTY_PREFIX.equals(action) && prettyContext.isPrettyRequest())
         {
            URL url = prettyContext.getRequestURL();
            QueryString query = prettyContext.getRequestQueryString();

            String target = contextPath + url.encode().toURL() + query.toQueryString();
            log.trace("Refreshing requested page [" + url + "]");
            String redirectUrl = externalContext.encodeRedirectURL(target, null);
            externalContext.redirect(redirectUrl);
            return true;
         }
         else if (isPrettyNavigationCase(prettyContext, action))
         {
            UrlMapping mapping = config.getMappingById(action);
            if (mapping != null)
            {
               String url = contextPath + builder.buildURL(mapping).encode()
                        + builder.buildQueryString(mapping).toString();
               log.trace("Redirecting to mappingId [" + mapping.getId() + "], [" + url + "]");
               String redirectUrl = externalContext.encodeRedirectURL(url, null);
               externalContext.redirect(redirectUrl);
            }
            else
            {
               throw new PrettyException("PrettyFaces: Invalid mapping id supplied to navigation handler: " + action);
            }
            return true;
         }
      }
      catch (IOException e)
      {
         throw new RuntimeException("PrettyFaces: redirect failed for target: " + action, e);
      }
      return false;
   }

   public void send404(final FacesContext facesContext)
   {
      try
      {
         HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
         response.sendError(HttpServletResponse.SC_NOT_FOUND);
      }
      catch (IOException e)
      {
         throw new PrettyException(e);
      }
   }

   private boolean isPrettyNavigationCase(final PrettyContext prettyContext, final String action)
   {
      PrettyConfig config = prettyContext.getConfig();
      return (action != null) && config.isMappingId(action) && action.trim().startsWith(PrettyContext.PRETTY_PREFIX);
   }
}
