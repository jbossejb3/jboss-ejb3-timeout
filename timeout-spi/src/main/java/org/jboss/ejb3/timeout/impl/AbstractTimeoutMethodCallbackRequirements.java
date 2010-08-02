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

/**
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
public abstract class AbstractTimeoutMethodCallbackRequirements implements TimeoutMethodCallbackRequirements
{
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

   protected abstract void check(Method method);
   
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
   private static Method findMethod(Class<?> cls, String methodName, Class<?>[] paramTypes)
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
    */
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
         return null;

      Method method = findMethod(beanClass, methodName, paramTypes);
      if(method != null)
      {
         check(method);
         method.setAccessible(true);
      }

      return method;
   }
}
