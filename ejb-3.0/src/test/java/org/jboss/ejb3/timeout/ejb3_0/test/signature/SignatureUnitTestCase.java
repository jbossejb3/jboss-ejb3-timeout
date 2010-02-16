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
package org.jboss.ejb3.timeout.ejb3_0.test.signature;

import org.jboss.ejb3.timeout.spi.TimeoutMethodCallbackRequirements;
import org.junit.Test;

import javax.ejb.TimedObject;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.imageio.spi.ServiceRegistry;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Note that the @Timeout annotations are for show.
 * 
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class SignatureUnitTestCase
{
   protected static final TimeoutMethodCallbackRequirements requirements = ServiceRegistry.lookupProviders(TimeoutMethodCallbackRequirements.class).next();

   public static class Bean1
   {

   }

   public static class Bean2 implements TimedObject
   {
      public void ejbTimeout(Timer timer)
      {
      }
   }

   public static class Bean3
   {
      @Timeout
      public void timeout(Timer timer)
      {
      }
   }

   public static class Bean4 extends Bean3
   {
   }

   public static class BeanFinal
   {
      @Timeout
      public final void timeout(Timer timer)
      {

      }
   }

   public static class BeanReturnType
   {
      @Timeout
      public Object timeout(Timer timer)
      {
         return null;
      }
   }

   public static class BeanStatic
   {
      @Timeout
      public static void timeout(Timer timer)
      {

      }
   }

   @Test
   public void test1()
   {
      Class<?> beanClass = Bean1.class;
      Method timeoutMethod = requirements.getTimeoutMethod(beanClass, null);
      assertNull(timeoutMethod);
   }

   @Test
   public void test2() throws Exception
   {
      Class<?> beanClass = Bean2.class;
      Method timeoutMethod = requirements.getTimeoutMethod(beanClass, null);
      Method expected = TimedObject.class.getMethod("ejbTimeout", Timer.class);
      assertEquals(expected, timeoutMethod);
   }

   @Test
   public void test3() throws Exception
   {
      Class<?> beanClass = Bean3.class;
      Method timeoutMethod = requirements.getTimeoutMethod(beanClass, "timeout");
      Method expected = Bean3.class.getMethod("timeout", Timer.class);
      assertEquals(expected, timeoutMethod);
   }

   @Test
   public void test4() throws Exception
   {
      Class<?> beanClass = Bean4.class;
      Method timeoutMethod = requirements.getTimeoutMethod(beanClass, "timeout");
      Method expected = Bean3.class.getMethod("timeout", Timer.class);
      assertEquals(expected, timeoutMethod);
   }

   @Test
   public void testFinal() throws Exception
   {
      Class<?> beanClass = BeanFinal.class;
      try
      {
         requirements.getTimeoutMethod(beanClass, "timeout");
         fail("expected IllegalArgumentException");
      }
      catch(IllegalArgumentException e)
      {
         // good
      }
   }

   @Test
   public void testReturnType() throws Exception
   {
      Class<?> beanClass = BeanReturnType.class;
      try
      {
         requirements.getTimeoutMethod(beanClass, "timeout");
         fail("expected IllegalArgumentException");
      }
      catch(IllegalArgumentException e)
      {
         // good
      }
   }

   @Test
   public void testStatic() throws Exception
   {
      Class<?> beanClass = BeanStatic.class;
      try
      {
         requirements.getTimeoutMethod(beanClass, "timeout");
         fail("expected IllegalArgumentException");
      }
      catch(IllegalArgumentException e)
      {
         // good
      }
   }

   @Test
   public void testWrongParameterType() throws Exception
   {
      class Bean
      {
         @Timeout
         public void timeout(Object wrong)
         {

         }
      }
      try
      {
         requirements.getTimeoutMethod(Bean.class, "timeout");
         fail("expected IllegalArgumentException");
      }
      catch(IllegalArgumentException e)
      {
         // good
      }
   }

   @Test
   public void testWrongParameterCount() throws Exception
   {
      class Bean
      {
         @Timeout
         public void timeout(Timer timer, Object wrong)
         {

         }
      }
      try
      {
         requirements.getTimeoutMethod(Bean.class, "timeout");
         fail("expected IllegalArgumentException");
      }
      catch(IllegalArgumentException e)
      {
         // good
      }
   }
}
