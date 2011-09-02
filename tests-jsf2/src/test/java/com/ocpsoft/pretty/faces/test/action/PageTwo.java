/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.ocpsoft.pretty.faces.test.action;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import com.ocpsoft.pretty.faces.test.MetasyntacticVariable;

/**
 * Page two.
 * @author Matt Benson
 */
@ManagedBean
@RequestScoped
@URLMappings(mappings = { @URLMapping(id = "injectedPath", pattern = "/page2/#{pageTwo.paramString}", viewId = "/page2.jsf") })
public class PageTwo
{
   @ManagedProperty("#{dataBean}")
   private DataBean data;
   private String paramString;
   private int paramInt;
   private MetasyntacticVariable paramVar;

   public DataBean getData()
   {
      return data;
   }

   public void setData(DataBean data)
   {
      this.data = data;
   }

   public String getParamString()
   {
      return paramString;
   }

   public void setParamString(String paramString)
   {
      this.paramString = paramString;
   }

   public int getParamInt()
   {
      return paramInt;
   }

   public void setParamInt(int paramInt)
   {
      this.paramInt = paramInt;
   }

   public MetasyntacticVariable getParamVar()
   {
      return paramVar;
   }

   public void setParamVar(MetasyntacticVariable paramVar)
   {
      this.paramVar = paramVar;
   }

}
