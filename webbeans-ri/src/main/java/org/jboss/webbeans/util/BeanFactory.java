/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.webbeans.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.webbeans.Event;
import javax.webbeans.Instance;

import org.jboss.webbeans.ManagerImpl;
import org.jboss.webbeans.bean.AbstractClassBean;
import org.jboss.webbeans.bean.EnterpriseBean;
import org.jboss.webbeans.bean.EventBean;
import org.jboss.webbeans.bean.InstanceBean;
import org.jboss.webbeans.bean.ProducerFieldBean;
import org.jboss.webbeans.bean.ProducerMethodBean;
import org.jboss.webbeans.bean.SimpleBean;
import org.jboss.webbeans.event.ObserverImpl;
import org.jboss.webbeans.introspector.AnnotatedField;
import org.jboss.webbeans.introspector.AnnotatedItem;
import org.jboss.webbeans.introspector.AnnotatedMethod;

/**
 * Utility class for creating Web Beans
 * 
 * @author Pete Muir
 */
public class BeanFactory
{
   /**
    * Creates a simple, annotation defined Web Bean
    * 
    * @param <T> The type
    * @param clazz The class
    * @param manager TODO
    * @return A Web Bean
    */
   public static <T> SimpleBean<T> createSimpleBean(Class<T> clazz, ManagerImpl manager)
   {
      return new SimpleBean<T>(clazz, manager);
   }

   /**
    * Creates a simple, annotation defined Enterprise Web Bean
    * 
    * @param <T> The type
    * @param clazz The class
    * @param manager TODO
    * @return An Enterprise Web Bean
    */
   public static <T> EnterpriseBean<T> createEnterpriseBean(Class<T> clazz, ManagerImpl manager)
   {
      return new EnterpriseBean<T>(clazz, manager);
   }

   /**
    * Creates a producer method Web Bean
    * 
    * @param <T> The type
    * @param type The class
    * @param method The underlying method
    * @param declaringBean The declaring bean abstraction
    * @param manager TODO
    * @return A producer Web Bean
    */
   public static <T> ProducerMethodBean<T> createProducerMethodBean(Class<T> type, Method method, AbstractClassBean<?> declaringBean, ManagerImpl manager)
   {
      return new ProducerMethodBean<T>(method, declaringBean, manager);
   }

   /**
    * Creates a producer field Web Bean
    * 
    * @param <T> The type
    * @param type The class
    * @param field The underlying field
    * @param declaringBean The declaring bean abstraction
    * @param manager TODO
    * @return A producer Web Bean
    */
   public static <T> ProducerFieldBean<T> createProducerFieldBean(Class<T> type, Field field, AbstractClassBean<?> declaringBean, ManagerImpl manager)
   {
      return new ProducerFieldBean<T>(field, declaringBean, manager);
   }

   /**
    * Creates a producer field Web Bean
    * 
    * @param field The underlying method abstraction
    * @param declaringBean The declaring bean abstraction
    * @param manager TODO
    * @return A producer Web Bean
    */
   public static <T> ProducerFieldBean<T> createProducerFieldBean(AnnotatedField<T> field, AbstractClassBean<?> declaringBean, ManagerImpl manager)
   {
      return new ProducerFieldBean<T>(field, declaringBean, manager);
   }

   /**
    * Creates a producer method Web Bean
    * 
    * @param method The underlying method abstraction
    * @param declaringBean The declaring bean abstraction
    * @param manager TODO
    * @return A producer Web Bean
    */
   public static <T> ProducerMethodBean<T> createProducerMethodBean(AnnotatedMethod<T> method, AbstractClassBean<?> declaringBean, ManagerImpl manager)
   {
      return new ProducerMethodBean<T>(method, declaringBean, manager);
   }

   /**
    * Creates an event Web Bean
    * 
    * @param field The event injection point abstraction
    * @param manager TODO
    * @param declaringBean The declaring bean abstraction
    * @return An event Web Bean
    */
   public static <T, S> EventBean<T, S> createEventBean(AnnotatedItem<Event<T>, S> field, ManagerImpl manager)
   {
      return new EventBean<T, S>(field, manager);
   }
   
   /**
    * Creates an instance Web Bean
    * 
    * @param field The instance injection point abstraction
    * @param manager TODO
    * @param declaringBean The declaring bean abstraction
    * @return An event Web Bean
    */
   public static <T, S> InstanceBean<T, S> createInstanceBean(AnnotatedItem<Instance<T>, S> field, ManagerImpl manager)
   {
      return new InstanceBean<T, S>(field, manager);
   }
   
   public static <T> ObserverImpl<T> createObserver(AnnotatedMethod<Object> method, AbstractClassBean<?> declaringBean, ManagerImpl manager)
   {
      return new ObserverImpl<T>(method, declaringBean, manager);
   }

}
