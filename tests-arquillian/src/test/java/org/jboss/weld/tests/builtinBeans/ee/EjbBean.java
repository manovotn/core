/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc., and individual contributors
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
package org.jboss.weld.tests.builtinBeans.ee;

import static javax.ejb.TransactionAttributeType.REQUIRED;

import static org.junit.Assert.assertNotNull;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 *
 * @author <a href="mailto:manovotn@redhat.com">Matej Novotny</a>
 */
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class EjbBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "test")
    private EntityManager manager;
    
    @Resource
    private EJBContext ctx;

    
    @Inject
    private BeanManager jsr299Manager;

    @TransactionAttribute(REQUIRED)
    public void initTransaction(Object event) throws SystemException {
        assertNotNull(manager);
        try {
            // throws exception, there is no schema defined, but that doesn't matter since we need to rollback anyway
            manager.persist(new Object());
        } catch (Exception e) {
            ctx.setRollbackOnly();
            jsr299Manager.fireEvent(event);
        } 
        
        
    }
}
