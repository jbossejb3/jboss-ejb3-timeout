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
package org.jboss.ejb3.timeout.spi;

import java.lang.reflect.Method;

/**
 * Allow for different timeout method callback requirements.
 * 
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
public interface TimeoutMethodCallbackRequirements
{
   /**
    * Analyze the class and find the timeout method (slow).
    *
    * @param beanClass
    * @param methodName
    * @return
    * @throws IllegalArgumentException
    * @deprecated Use {@link #getTimeoutMethod(Class, String, Class[])} instead
    */
   @Deprecated
   Method getTimeoutMethod(Class<?> beanClass, String methodName) throws IllegalArgumentException;
   
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
    * @param klass The {@link Class} which is being checked for the method   
    * @param methodName The timeout method name
    * @param paramTypes The parameter types of the timeout method. Can be null
    * @return Returns the method corresponding to the passed <code>methodName</code> and <code>paramTypes</code>.
    *           If no such method is found, then returns null
    * @throws IllegalArgumentException If the {@link Method} identified by this method doesn't follow the rules laid down
    *           by the EJB3.x specification, for timeout methods
    */
   Method getTimeoutMethod(Class<?> klass, String methodName, Class<?>[] paramTypes) throws IllegalArgumentException;
}
