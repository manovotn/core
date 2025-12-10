/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat, Inc., and individual contributors
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
package org.jboss.weld.tests.event.observer.transactional.rollback;

import static org.jboss.weld.test.util.ActionSequence.assertSequenceDataContainsAll;
import static org.jboss.weld.test.util.ActionSequence.getSequenceData;
import static org.jboss.weld.test.util.ActionSequence.reset;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import jakarta.transaction.SystemException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.weld.test.util.ActionSequence;
import org.jboss.weld.test.util.Utils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@Tag("Integration")
@ExtendWith(ArquillianExtension.class)
public class TransactionalObserverRollbackTest {

    @Inject
    EjbTestBean ejbTestBean;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap
                .create(WebArchive.class,
                        Utils.getDeploymentNameAsHash(TransactionalObserverRollbackTest.class, Utils.ARCHIVE_TYPE.WAR))
                .addPackage(TransactionalObserverRollbackTest.class.getPackage())
                .addClass(ActionSequence.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsResource(TransactionalObserverRollbackTest.class.getPackage(), "persistence.xml",
                        "META-INF/persistence.xml");
    }

    @Test
    public void afterSuccessObserverIsNotNotifiedAfterTxRollBack() throws SystemException {
        reset();
        ejbTestBean.initTransaction();
        assertSequenceDataContainsAll(
                Arrays.asList(TransactionPhase.IN_PROGRESS.toString(), TransactionPhase.BEFORE_COMPLETION.toString(),
                        TransactionPhase.AFTER_COMPLETION.toString(),
                        TransactionPhase.AFTER_FAILURE.toString()));
        // BEFORE_COMPLETION is notified before AFTER_COMPLETION
        assertTrue(getSequenceData().indexOf(TransactionPhase.BEFORE_COMPLETION.toString()) < getSequenceData()
                .indexOf(TransactionPhase.AFTER_COMPLETION.toString()));
        assertFalse(getSequenceData().contains(TransactionPhase.AFTER_SUCCESS.toString()));
    }
}
