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
package org.jboss.ejb3.timeout.ejb3_1.test.signature;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.imageio.spi.ServiceRegistry;

import org.jboss.ejb3.timeout.spi.TimeoutMethodCallbackRequirements;
import org.junit.Test;

/**
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class Signature3_1UnitTestCase
{
   private static TimeoutMethodCallbackRequirements requirements = ServiceRegistry.lookupProviders(TimeoutMethodCallbackRequirements.class).next();

   public static class Bean1
   {
      @Timeout
      private void timeout()
      {
         
      }
   }
   
   private class Bean2
   {
      
      // this one should be ignored
      private void timeout(String blah)
      {
         
      }
      
      // this one should be considered the
      // timeout method
      private void timeout(Timer timer)
      {
         
      }
      
      // this one should be ignored
      private void timeout(Timer timer, String blah)
      {
         
      }
       
   }

   @Test
   public void test1() throws Exception
   {
      Class<?> beanClass = Bean1.class;
      Method timeoutMethod = requirements.getTimeoutMethod(beanClass, "timeout");
      Method expected = Bean1.class.getDeclaredMethod("timeout");
      assertEquals(expected, timeoutMethod);
   }
   
   @Test
   public void testTimeoutMethod() throws Exception
   {
      Class<?> beanClass = Bean1.class;
      Method timeoutMethod = requirements.getTimeoutMethod(beanClass, "timeout", new Class<?>[0]);
      Method expected = Bean1.class.getDeclaredMethod("timeout");
      assertEquals(expected, timeoutMethod);
   }
   
   /**
    * Tests that the {@link TimeoutMethodCallbackRequirements} works correctly when the 
    * timeout method name is specified and the param types is null. It's expected to 
    * find the "correct" timeout method based on the method name (i.e. number of params to that timeout method 
    * isn't known). 
    * @throws Exception
    */
   @Test
   public void testTimeoutMethodWithJustMethodName() throws Exception
   {
      Class<?> beanClass = Bean2.class;
      Method timeoutMethod = requirements.getTimeoutMethod(beanClass, "timeout", null);
      Method expected = Bean2.class.getDeclaredMethod("timeout", new Class<?>[] {Timer.class});
      assertEquals("Unexpected timeout method", expected, timeoutMethod);

   }
}
