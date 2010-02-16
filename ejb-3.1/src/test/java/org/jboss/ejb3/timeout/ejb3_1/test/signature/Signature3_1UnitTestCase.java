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

import org.jboss.ejb3.timeout.spi.TimeoutMethodCallbackRequirements;
import org.junit.Test;

import javax.ejb.Timeout;
import javax.imageio.spi.ServiceRegistry;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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

   @Test
   public void test1() throws Exception
   {
      Class<?> beanClass = Bean1.class;
      Method timeoutMethod = requirements.getTimeoutMethod(beanClass, "timeout");
      Method expected = Bean1.class.getDeclaredMethod("timeout");
      assertEquals(expected, timeoutMethod);
   }
}
