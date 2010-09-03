/*
 * JBoss, Home of Professional Open Source
 * Copyright (c) 2010, JBoss Inc., and individual contributors as indicated
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
package org.jboss.ejb3.timeout.impl;

import java.lang.reflect.Method;


import javax.ejb.TimedObject;
import javax.ejb.Timer;

import org.jboss.ejb3.timeout.spi.TimeoutMethodCallbackRequirements;
import org.jboss.logging.Logger;

/**
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
public abstract class AbstractTimeoutMethodCallbackRequirements implements TimeoutMethodCallbackRequirements
{
   /** Logger */
   private static final Logger logger = Logger.getLogger(AbstractTimeoutMethodCallbackRequirements.class);
   
   protected static final Method EJB_TIMEOUT;

   static
   {
      try
      {
         EJB_TIMEOUT = TimedObject.class.getMethod("ejbTimeout", Timer.class);
      }
      catch(NoSuchMethodException e)
      {
         throw new RuntimeException(e);
      }
   }

   protected abstract void check(Method method) throws IllegalArgumentException;
   
   /**
    * 
    * @param cls
    * @param methodName
    * @return
    * @deprecated Use {@link #findMethod(Class, String, Class[])}
    */
   @Deprecated
   private static Method findMethod(Class<?> cls, String methodName)
   {
      if(cls == null)
         return null;

      // this is not the fastest way, but it ensures we get the method and can analyze it for errors
      // if we were to do a search with parameter types, we might end up empty handed (no method, no error)
      for(Method method : cls.getDeclaredMethods())
      {
         if(method.getName().equals(methodName))
         {
            return method;
         }
      }

      return findMethod(cls.getSuperclass(), methodName);
   }

   /**
    * Checks the passed <code>cls</code> for a method named <code>methodName</code> and whose
    * parameters are of type <code>paramTypes</code>. If no such method is found on the <code>cls</code>
    * then its superclass(es) are checked for the method, until the method is found or there's no more a
    * superclass.
    * <p>
    *   The passed <code>paramTypes</code> can be null. In such a case, a method named <code>methodName</code>,
    *   which doesn't accept any parameter, is searched for.   
    * </p> 
    * 
    * 
    * @param cls The {@link Class} which is being checked for the method  
    * @param methodName The name of the method
    * @param paramTypes The param types of the method. Can be null
    * @return Returns the method corresponding to the passed <code>methodName</code> and <code>paramTypes</code>.
    *           If no such method is found, then returns null
    */
   private Method findMethod(Class<?> cls, String methodName, Class<?>[] paramTypes)
   {
      if(cls == null)
         return null;

      Class<?>[] methodParams = paramTypes;
      if (methodParams == null)
      {
         methodParams = new Class<?>[0];
      }
      
      // this is not the fastest way, but it ensures we get the method and can analyze it for errors
      // if we were to do a search with parameter types, we might end up empty handed (no method, no error)
      for(Method method : cls.getDeclaredMethods())
      {
         if(method.getName().equals(methodName))
         {
            if (paramsMatch(method.getParameterTypes(), methodParams))
            {
               return method;
            }
         }
      }

      return findMethod(cls.getSuperclass(), methodName, paramTypes);
   }
   
   private static boolean paramsMatch(Class<?>[] params, Class<?>[] otherParams)
   {
      if (params == null || otherParams == null)
      {
         return false;
      }
      
      if (params.length != otherParams.length)
      {
         return false;
      }
      for (int i = 0; i < params.length; i++)
      {
         if (params[i] == null || otherParams[i] == null)
         {
            return false;
         }
         if (params[i].equals(otherParams[i]) == false)
         {
            return false;
         }
      }
      return true;
   }

   /**
    * {@inheritDoc}
    * @deprecated Use {@link #getTimeoutMethod(Class, String, Class[])} instead
    */
   @Deprecated
   @Override
   public Method getTimeoutMethod(Class<?> beanClass, String methodName) throws IllegalArgumentException
   {
      if(TimedObject.class.isAssignableFrom(beanClass))
      {
         return EJB_TIMEOUT;
      }

      if(methodName == null)
         return null;

      Method method = findMethod(beanClass, methodName);
      if(method != null)
      {
         check(method);
         method.setAccessible(true);
      }

      return method;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Method getTimeoutMethod(java.lang.Class<?> beanClass, String methodName, java.lang.Class<?>[] paramTypes) throws IllegalArgumentException
   {
      if(TimedObject.class.isAssignableFrom(beanClass))
      {
         return EJB_TIMEOUT;
      }

      if(methodName == null)
      {
         return null;
      }
      
      // if the param types are null, then it means find all methods with the specified
      // timeout method name and then pick an eligible one (note, paramtTypes == null
      // means that the timeout method may or may not accept params)
      if (paramTypes == null)
      {
         return this.getTimeoutMethodWithName(beanClass, methodName);
      }

      // find the timeout method with the specified params types and method name
      Method method = findMethod(beanClass, methodName, paramTypes);
      if(method != null)
      {
         // make sure it meets the timeout requirements
         check(method);
         method.setAccessible(true);
      }

      return method;
   }
   
   
   /**
    * Checks the passed <code>cls</code> for a method named <code>methodName</code>.
    * Any method which matches the <code>methodName</code> is then checked for timeout method requirements. If that
    * method meets the timeout method requirements, then that {@link Method} is returned back.
    * <p>
    * If no such method is found on the <code>cls</code>
    * then its superclass(es) are checked for the method, until the method is found or there's no more a
    * superclass.
    * </p>
    *  
    * @param cls The {@link Class} which is being checked for the method  
    * @param methodName The name of the method
    * @return Returns the method matching the <code>methodName</code> and meeting the timeout method requirements. Null, otherwise.
    */
   private Method getTimeoutMethodWithName(Class<?> cls, String methodName)
   {
      if(cls == null || methodName == null)
      {
         return null;
      }

      // fetch the methods and compare the name
      for(Method method : cls.getDeclaredMethods())
      {
         if(method.getName().equals(methodName))
         {
            try
            {
               // make sure it meets the timeout method requirements
               this.check(method);
               method.setAccessible(true);
               // found a valid timeout method with the specifed name,
               return method;
            }
            catch (IllegalArgumentException iae)
            {
               // ignore this method (and the exception) if the method
               // doesn't match the timeout method requirements
               logger.debug("Ignoring method: " + method + " since it doesn't meet the timeout method requirements");
            }
         }
      }
      // now try on super class
      return getTimeoutMethodWithName(cls.getSuperclass(), methodName);
   }
   
}
