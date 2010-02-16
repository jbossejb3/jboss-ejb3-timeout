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
package org.jboss.ejb3.timeout.ejb3_0;

import org.jboss.ejb3.timeout.impl.AbstractTimeoutMethodCallbackRequirements;
import org.jboss.ejb3.timeout.spi.TimeoutMethodCallbackRequirements;

import javax.ejb.TimedObject;
import javax.ejb.Timer;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * EJB 3.0 FR 18.2.2 Timeout Callbacks.
 * 
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class EJB3_0TimeoutMethodCallbackRequirements extends AbstractTimeoutMethodCallbackRequirements
{
   @Override
   protected void check(Method method)
   {
      int mod = method.getModifiers();
      if(Modifier.isFinal(mod))
         throw new IllegalArgumentException("timeout callback method " + method + " must not be final (EJB 3.0 FR 18.2.2)");
      if(Modifier.isStatic(mod))
         throw new IllegalArgumentException("timeout callback method " + method + " must not be static (EJB 3.0 FR 18.2.2)");
      if(!method.getReturnType().equals(Void.TYPE) ||
         method.getParameterTypes().length != 1 ||
         !method.getParameterTypes()[0].equals(Timer.class))
         throw new IllegalArgumentException("timeout callback method " + method + " must have signature: void <METHOD>(javax.ejb.Timer timer); (EJB 3.0 FR 18.2.2)");
   }
}
