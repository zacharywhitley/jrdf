/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2009 The JRDF Project.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        the JRDF Project (http://jrdf.sf.net/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The JRDF Project" and "JRDF" must not be used to endorse
 *    or promote products derived from this software without prior written
 *    permission. For written permission, please contact
 *    newmana@users.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "JRDF"
 *    nor may "JRDF" appear in their names without prior written
 *    permission of the JRDF Project.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the JRDF Project.  For more
 * information on JRDF, please see <http://jrdf.sourceforge.net/>.
 *
 */

package org.jrdf.collection;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseExistsException;
import com.sleepycat.je.Environment;
import org.jrdf.util.bdb.BdbEnvironmentHandler;
import org.jrdf.util.test.AssertThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jrdf.util.test.ArgumentTestUtil.checkConstructNullAssertion;
import static org.jrdf.util.test.ArgumentTestUtil.checkConstructorSetsFieldsAndFieldsPrivateFinal;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import static org.jrdf.util.test.MockTestUtil.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

@RunWith(PowerMockRunner.class)
public class BdbMapFactoryUnitTest {
    private static final Class[] PARAM_TYPES = {BdbEnvironmentHandler.class, String.class};
    private static final String[] PARAMETER_NAMES = {"newHandler", "newDatabaseName"};
    private static final String DATABASE_NAME = "dbName" + System.currentTimeMillis();
    @Mock private BdbEnvironmentHandler storedMapHandler;
    @Mock private Environment environment;
    @Mock private Database database;

    @Test
    public void classProperties() throws Exception {
        checkImplementationOfInterfaceAndFinal(MapFactory.class, BdbMapFactory.class);
        checkConstructor(BdbMapFactory.class, Modifier.PUBLIC, PARAM_TYPES);
        checkConstructNullAssertion(BdbMapFactory.class, PARAM_TYPES);
        checkConstructorSetsFieldsAndFieldsPrivateFinal(BdbMapFactory.class, PARAM_TYPES, PARAMETER_NAMES);
    }

    @Test
    public void createMap() throws Exception {
        Map<String, String> expectedMap = creatMapExpectations();
        replayAll();
        BdbMapFactory factory = new BdbMapFactory(storedMapHandler, DATABASE_NAME);
        Map<String, String> actualMap = factory.createMap(String.class, String.class);
        assertThat(actualMap, is(expectedMap));
        verifyAll();
    }

    @Test
    public void testHandleException() throws Exception {
        expect(storedMapHandler.setUpEnvironment()).andThrow(new DatabaseExistsException(""));
        AssertThrows.assertThrows(DatabaseExistsException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                replayAll();
                BdbMapFactory factory = new BdbMapFactory(storedMapHandler, DATABASE_NAME);
                factory.createMap(String.class, String.class);
                verifyAll();
            }
        });
    }

    @Test
    public void testClose() throws Exception {
        creatMapExpectations();
        environment.sync();
        expectLastCall();
        environment.close();
        expectLastCall();
        database.close();
        expectLastCall();
        replayAll();
        MapFactory factory = new BdbMapFactory(storedMapHandler, DATABASE_NAME);
        factory.createMap(String.class, String.class);
        factory.close();
        verifyAll();
    }

    @Test
    public void testCloseCatalogEvenWithExceptionInEnvironment() throws Exception {
        creatMapExpectations();
        environment.sync();
        expectLastCall();
        environment.close();
        expectLastCall().andThrow(new DatabaseExistsException(""));
        database.close();
        expectLastCall();
        AssertThrows.assertThrows(DatabaseExistsException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                replayAll();
                BdbMapFactory factory = new BdbMapFactory(storedMapHandler, DATABASE_NAME);
                factory.createMap(String.class, String.class);
                factory.close();
                verifyAll();
            }
        });
    }

    @Test
    public void testCloseBothExceptions() throws Exception {
        creatMapExpectations();
        environment.sync();
        expectLastCall();
        environment.close();
        expectLastCall().andThrow(new DatabaseExistsException(""));
        AssertThrows.assertThrows(DatabaseExistsException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                replayAll();
                BdbMapFactory factory = new BdbMapFactory(storedMapHandler, DATABASE_NAME);
                factory.createMap(String.class, String.class);
                factory.close();
                verifyAll();
            }
        });
    }

    private HashMap<String, String> creatMapExpectations() throws Exception {
        expect(environment.getDatabaseNames()).andReturn(new ArrayList<String>());
        HashMap<String, String> expectedMap = new HashMap<String, String>();
        expect(storedMapHandler.setUpEnvironment()).andReturn(environment);
        DatabaseConfig databaseConfig = createMock(DatabaseConfig.class);
        expect(storedMapHandler.setUpDatabaseConfig(true)).andReturn(databaseConfig);
        expect(storedMapHandler.setupDatabase(environment, DATABASE_NAME + 1, databaseConfig)).andReturn(database);
        expect(storedMapHandler.createMap(database, String.class, String.class)).
            andReturn(expectedMap);
        return expectedMap;
    }
}
