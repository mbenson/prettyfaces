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
package com.ocpsoft.pretty.faces.config.annotation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.servlet.ServletContext;

import com.ocpsoft.logging.Logger;

/**
 * Base class for implementations of the {@link ClassFinder} interface.
 * 
 * @see ClassFinder
 * @author Christian Kaltepoth
 */
public abstract class AbstractClassFinder implements ClassFinder
{

   /**
    * Common logger for all implementations
    */
   protected final Logger log = Logger.getLogger(this.getClass());

   /**
    * The {@link ServletContext}
    */
   protected final ServletContext servletContext;

   /**
    * The {@link ClassLoader} to get classes from
    */
   protected final ClassLoader classLoader;

   /**
    * The filter for checking which classes to process
    */
   protected final PackageFilter packageFilter;

   /**
    * The filter to check bytecode for interesting annotations
    */
   private final ByteCodeAnnotationFilter byteCodeAnnotationFilter;

   /**
    * Initialization procedure
    * 
    * @param servletContext The {@link ServletContext} of the web application.
    * @param classLoader The {@link ClassLoader} to use for loading classes
    * @param packageFilter The {@link PackageFilter} used to check if a package has to be scanned.
    */
   public AbstractClassFinder(final ServletContext servletContext, final ClassLoader classLoader,
            final PackageFilter packageFilter)
   {
      this.servletContext = servletContext;
      this.classLoader = classLoader;
      this.packageFilter = packageFilter;
      this.byteCodeAnnotationFilter = new ByteCodeAnnotationFilter();
   }

   /**
    * Strip everything up to and including a given prefix from a string.
    * 
    * @param str The string to process
    * @param prefix The prefix
    * @return the stripped string or <code>null</code> if the prefix has not been found
    */
   protected String stripKnownPrefix(final String str, final String prefix)
   {

      int startIndex = str.lastIndexOf(prefix);
      if (startIndex != -1)
      {
         return str.substring(startIndex + prefix.length());
      }

      return null;
   }

   /**
    * <p>
    * Creates a FQCN from an {@link URL} representing a <code>.class</code> file.
    * </p>
    * 
    * @param url The path of the class file
    * @return the FQCN of the class
    */
   protected static String getClassName(final String filename)
   {

      // end index is just before ".class"
      int endIndex = filename.length() - ".class".length();

      // extract relevant part of the path
      String relativePath = filename.substring(0, endIndex);

      // replace / by . to create FQCN
      return relativePath.replace('/', '.');
   }

   /**
    * Checks if a supplied class has to be processed by checking the package name against the {@link PackageFilter}.
    * 
    * @param className FQCN of the class
    * @return <code>true</code> for classes to process, <code>false</code> for classes to ignore
    */
   protected boolean mustProcessClass(final String className)
   {

      // the default package
      String packageName = "";

      // find last dot in class name to determine the package name
      int packageEndIndex = className.lastIndexOf(".");
      if (packageEndIndex != -1)
      {
         packageName = className.substring(0, packageEndIndex);
      }

      // check filter
      return packageFilter.isAllowedPackage(packageName);

   }

   /**
    * <p>
    * Handle a single class to process. This method should only be called if the class name is accepted by the
    * {@link PackageFilter}.
    * </p>
    * <p>
    * If <code>classFileStream</code> is not <code>null</code> the method will first try to check whether the class
    * files may contain annotations by scanning it with the {@link ByteCodeAnnotationFilter}. If no {@link InputStream}
    * is supplied, this check will be skipped. After that the method will create an instance of the class and then call
    * {@link PrettyAnnotationHandler#processClass(Class)}.
    * </p>
    * <p>
    * Please not the the called of this method is responsible to close the supplied {@link InputStream}!
    * </p>
    * 
    * @param className The FQCN of the class
    * @param classFileStream The Java class file of the class (may be <code>null</code>)
    * @param handler the handler to notify
    */
   protected void processClass(final String className, final InputStream classFileStream,
            final PrettyAnnotationHandler handler)
   {

      // bytecode check is only performed if the InputStream is available
      if (classFileStream != null)
      {

         // we must take care of IOExceptions thrown by ByteCodeAnnotationFilter
         try
         {

            // call bytecode filter
            boolean shouldScanClass = byteCodeAnnotationFilter.accept(classFileStream);

            // No annotations -> abort
            if (!shouldScanClass)
            {
               return;
            }

            // filter says we should scan the class
            if (log.isDebugEnabled())
            {
               log.debug("Bytecode filter recommends to scan class: " + className);
            }

         }
         catch (IOException e)
         {
            if (log.isDebugEnabled())
            {
               log.debug("Failed to parse class file: " + className, e);
            }
         }
      }

      try
      {
         // request this class from the ClassLoader
         Class<?> clazz = classLoader.loadClass(className);

         // call handler
         handler.processClass(clazz);

      }
      catch (NoClassDefFoundError e)
      {
         // reference to another class unknown to the classloader
         log.debug("Could not load class '" + className + "': " + e.toString());
      }
      catch (ClassNotFoundException e)
      {
         // should no happen, because we found the class on the classpath
         throw new IllegalStateException("Unable to load class: " + className, e);
      }

   }

}
