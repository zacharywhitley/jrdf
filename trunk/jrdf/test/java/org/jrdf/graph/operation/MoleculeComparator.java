/*
 * Copyright 2007 BioMANTA Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.biomanta.comparison;

import org.jrdf.graph.GraphException;

/**
 * Created by IntelliJ IDEA.
 * User: imrank
 * Date: 8/08/2007
 * Time: 17:47:35
 * To change this template use File | Settings | File Templates.
 */
public interface MoleculeComparator {

    /**
     * Given 2 molecules this method will compare the two and determine
     * if they are simlar.
     *
     * @param m1
     * @param m2
     * @return
     */
    boolean compare(Molecule m1, Molecule m2) throws GraphException;
}
