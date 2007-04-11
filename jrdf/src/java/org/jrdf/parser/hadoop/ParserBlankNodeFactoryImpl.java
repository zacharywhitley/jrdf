/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2007 The JRDF Project.  All rights reserved.
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

package org.jrdf.parser.hadoop;

import org.jrdf.parser.ParserBlankNodeFactory;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.mem.BlankNodeImpl;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.SetFile;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.NullWritable;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

/**
 * A factory for BlankNodes that uses a MapFile to keep track of the BlankNodes
 * that have been allocated by {@link #createBlankNode(String)} so that the
 * same BlankNode object can be returned for a given <code>nodeID</code>.
 *
 * @author Andrew Newman
 * @version $Revision: 1045 $
 */
public class ParserBlankNodeFactoryImpl implements ParserBlankNodeFactory {

    private static final WritableComparator COMPARATOR = WritableComparator.get(Text.class);

    /**
     * A factory for creating BlankNodes (as well as resources and literals).
     */
    private GraphElementFactory valueFactory;
    private final Configuration configuration;
    private FileSystem fileSystem;
    private String filename;

    public ParserBlankNodeFactoryImpl(GraphElementFactory newValueFactory, Configuration configuration,
        String filename) throws IOException {
        this.valueFactory = newValueFactory;
        this.configuration = configuration;
        this.fileSystem = FileSystem.getLocal(configuration);
        this.filename = filename;
        fileSystem.mkdirs(new Path(filename));
        MapFile.Writer writer = new MapFile.Writer(configuration, fileSystem, filename, COMPARATOR,
            Text.class, SequenceFile.CompressionType.NONE);
        writer.close();
    }

    public BlankNode createBlankNode() throws GraphElementFactoryException {
        return valueFactory.createResource();
    }

    public BlankNode createBlankNode(String nodeID) throws GraphElementFactoryException {
        // Maybe the node ID has been used before:
        BlankNode result = null;
        try {
            MapFile.Reader reader = new MapFile.Reader(fileSystem, filename, configuration);
            Text key = new Text(nodeID);
            Text id = (Text) reader.get(key, new Text());
            reader.close();
            System.err.println("Id: " + id);
            if (null != id) {
                // Existing node
                result = BlankNodeImpl.valueOf(id.toString());
            } else {
                // This is a new node ID, create a new BNode object for it
                result = valueFactory.createResource();

                // Remember it, the nodeID might occur again.
                MapFile.Writer writer = new MapFile.Writer(configuration, fileSystem, filename, COMPARATOR,
                    Text.class, SequenceFile.CompressionType.NONE);
                writer.append(key, new Text(result.toString()));
                writer.close();
            }
            return result;
        } catch (IOException e) {
            throw new GraphElementFactoryException(e);
        }
    }

    public void clear() {
        try {
            MapFile.delete(fileSystem, filename);
        } catch (IOException e) {
        }
    }

    public void close() {
    }
}
