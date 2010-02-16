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

import org.jboss.ejb3.timeout.spi.TimeoutMethodCallbackRequirements;

import javax.ejb.TimedObject;
import javax.ejb.Timer;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

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

   private static Method findMethod(Class<?> cls, String methodName)
   {
      if(cls == null)
         return null;

      // this is not the fastest way, but it ensures we get the method and can analyze it for errors
      // if we were to do a search with parameter types, we might end up empty handed (no method, no error)
      for(Method method : cls.getDeclaredMethods())
      {
         if(method.getName().equals(methodName))
            return method;
      }

      return findMethod(cls.getSuperclass(), methodName);
   }

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
}
