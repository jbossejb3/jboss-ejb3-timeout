/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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
package org.jboss.ejb3.timeout.impl.test.ejbthree2138.unit;

import java.lang.reflect.Method;

import javax.ejb.Timer;

import junit.framework.Assert;

import org.jboss.ejb3.timeout.impl.AbstractTimeoutMethodCallbackRequirements;
import org.jboss.ejb3.timeout.impl.test.ejbthree2138.BaseBean;
import org.jboss.ejb3.timeout.impl.test.ejbthree2138.MockTimeoutMethodCallbackRequirements;
import org.jboss.ejb3.timeout.impl.test.ejbthree2138.SimpleBean;
import org.junit.Test;

/**
 * Test that the {@link AbstractTimeoutMethodCallbackRequirements#getTimeoutMethod(Class, String, Class[])}
 * works as expected.
 * 
 * @see https://jira.jboss.org/browse/EJBTHREE-2138
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class OverloadedTimeoutMethodUnitTestCase
{

   /**
    * Tests that the {@link AbstractTimeoutMethodCallbackRequirements#getTimeoutMethod(Class, String, Class[])}
    * returns the correct timeout method when a class hierarchy has overloaded timeout methods.
    * 
    * @see EJBTHREE-2138 https://jira.jboss.org/browse/EJBTHREE-2138 for details  
    * @throws Exception
    */
   @Test
   public void testOverloadedTimeoutMethod() throws Exception
   {
      AbstractTimeoutMethodCallbackRequirements timeoutMethodRequirements = new MockTimeoutMethodCallbackRequirements();

      Method timeoutMethodWithoutParams = timeoutMethodRequirements.getTimeoutMethod(SimpleBean.class, "onTimeout",
            new Class<?>[0]);

      Method expectedTimeoutMethodWithoutParams = BaseBean.class.getDeclaredMethod("onTimeout", new Class<?>[0]);

      Assert.assertEquals("Unexpected timeout method", expectedTimeoutMethodWithoutParams, timeoutMethodWithoutParams);

      Method timeoutMethodWithTimerParam = timeoutMethodRequirements.getTimeoutMethod(SimpleBean.class, "onTimeout",
            new Class<?>[]
            {Timer.class});

      Method expectedTimeoutMethodWithTimerParam = SimpleBean.class.getDeclaredMethod("onTimeout", new Class<?>[]
      {Timer.class});
      
      Assert.assertEquals("Unexpected timeout method", expectedTimeoutMethodWithTimerParam, timeoutMethodWithTimerParam);
   }
   
   /**
    * Tests that the {@link AbstractTimeoutMethodCallbackRequirements#getTimeoutMethod(Class, String, Class[])} returns
    * null when passed with a method name, params of a non-existent timeout method
    * 
    * @throws Exception
    */
   @Test
   public void testNonExistentTimeoutMethod() throws Exception
   {
      AbstractTimeoutMethodCallbackRequirements timeoutMethodRequirements = new MockTimeoutMethodCallbackRequirements();


      Method nonExistentTimeoutMethodOnBaseBean = timeoutMethodRequirements.getTimeoutMethod(BaseBean.class, "onTimeout",
            new Class<?>[]
            {Timer.class});

      Assert.assertNull("Timeout method was expected to be null", nonExistentTimeoutMethodOnBaseBean);
   }
}
