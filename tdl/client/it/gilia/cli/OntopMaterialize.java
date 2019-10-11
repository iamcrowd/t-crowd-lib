package it.unibz.inf.ontop.cli;

/*
 * #%L
 * ontop-cli
 * %%
 * Copyright (C) 2009 - 2014 Free University of Bozen-Bolzano
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.OptionType;
import com.github.rvesse.airline.annotations.restrictions.AllowedValues;
import com.google.common.collect.ImmutableSet;
import it.unibz.inf.ontop.exception.OBDASpecificationException;
import it.unibz.inf.ontop.injection.OntopSQLOWLAPIConfiguration;
import it.unibz.inf.ontop.injection.OntopSQLOWLAPIConfiguration.Builder;
import it.unibz.inf.ontop.materialization.MaterializationParams;
import it.unibz.inf.ontop.rdf4j.materialization.RDF4JMaterializer;
import org.apache.commons.rdf.api.IRI;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.helpers.BasicWriterSettings;
import org.eclipse.rdf4j.rio.ntriples.NTriplesWriter;
import org.eclipse.rdf4j.rio.rdfxml.RDFXMLWriter;
import org.eclipse.rdf4j.rio.turtle.TurtleWriter;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.commons.io.FilenameUtils.removeExtension;

@Command(name = "materialize",
        description = "Materialize the RDF graph exposed by the mapping and the OWL ontology")
public class OntopMaterialize extends OntopReasoningCommandBase {

    private enum PredicateType {
        CLASS("C"),
        PROPERTY("P");

        private final String code;

        PredicateType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }


    private static final int TRIPLE_LIMIT_PER_FILE = 500000;
    private static final String RDF_XML = "rdfxml";
    private static final String TURTLE = "turtle";
    private static final String NTRIPLES = "ntriples";


    @Option(type = OptionType.COMMAND, override = true, name = {"-o", "--output"},
            title = "output", description = "output file (default) or prefix (only for --separate-files)")
    //@BashCompletion(behaviour = CompletionBehaviour.FILENAMES)
    private String outputFile;

    @Option(type = OptionType.COMMAND, name = {"-f", "--format"}, title = "outputFormat",
            description = "The format of the materialized ontology. " +
                    //" Options: rdfxml, turtle. " +
                    "Default: rdfxml")
    @AllowedValues(allowedValues = {RDF_XML, TURTLE, NTRIPLES})
    public String format = RDF_XML;

    @Option(type = OptionType.COMMAND, name = {"--separate-files"}, title = "output to separate files",
            description = "generating separate files for different classes/properties. This is useful for" +
                    " materializing large OBDA setting. Default: false.")
    public boolean separate = false;

    @Option(type = OptionType.COMMAND, name = {"--no-streaming"}, title = "do not execute streaming of results",
            description = "All the SQL results of one big query will be stored in memory. Not recommended. Default: false.")
    private boolean noStream = false;

    private boolean doStreamResults = true;

    public OntopMaterialize() {
    }

    @Override
    public void run() {

        //   Streaming is necessary to materialize large RDF graphs without
        //   storing all the SQL results of one big query in memory.
        if (noStream) {
            doStreamResults = false;
        }
        RDF4JMaterializer materializer = createMaterializer();
        OutputSpec outputSpec = (outputFile == null) ?
                new OutputSpec(format) :
                new OutputSpec(outputFile, format);
        if (separate) {
            runWithSeparateFiles(materializer, outputSpec);
        } else {
            runWithSingleFile(materializer, outputSpec);
        }
    }

    private RDF4JMaterializer createMaterializer() {

        RDF4JMaterializer materializer;
        try {
            OWLOntology ontology = loadOntology();
            OntopSQLOWLAPIConfiguration materializerConfiguration = createAndInitConfigurationBuilder()
                    .ontology(ontology)
                    .build();
            materializer = RDF4JMaterializer.defaultMaterializer(
                    materializerConfiguration,
                    MaterializationParams.defaultBuilder()
                            .enableDBResultsStreaming(doStreamResults)
                            .build()
            );
        } catch (OBDASpecificationException | OWLOntologyCreationException e) {
            throw new RuntimeException(e);
        }
        return materializer;
    }

    private OWLOntology loadOntology() throws OWLOntologyCreationException {
        if (owlFile != null) {
            OWLOntology ontology = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(new File(owlFile));
            if (disableReasoning) {
                return extractDeclarations(ontology.getOWLOntologyManager(), ontology);
            }
            return ontology;
        }
        return OWLManager.createOWLOntologyManager().createOntology();
    }

    private void runWithSingleFile(RDF4JMaterializer materializer, OutputSpec outputSpec) {
        int tripleCount = 0;

        final long startTime = System.currentTimeMillis();

        GraphQueryResult result = materializer.materialize().evaluate();

        try {
            BufferedWriter writer = outputSpec.createWriter(Optional.empty());
            tripleCount += serializeTripleBatch(
                    result,
                    Optional.empty(),
                    writer,
                    outputSpec.createRDFHandler(writer)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("NR of TRIPLES: " + tripleCount);

        final long endTime = System.currentTimeMillis();
        final long time = endTime - startTime;
        System.out.println("Elapsed time to materialize: " + time + " {ms}");
    }

    private void runWithSeparateFiles(RDF4JMaterializer materializer, OutputSpec outputSpec) {
        try {
            materializeClassesByFile(materializer, outputSpec);
            materializePropertiesByFile(materializer, outputSpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void materializeClassesByFile(RDF4JMaterializer materializer, OutputSpec outputSpec) throws Exception {
        ImmutableSet<IRI> classes = materializer.getClasses();
        int total = classes.size();
        AtomicInteger i = new AtomicInteger();
        for (IRI c : classes) {
            serializePredicate(materializer, c, PredicateType.CLASS, i.incrementAndGet(), total, outputSpec);
        }
    }

    private void materializePropertiesByFile(RDF4JMaterializer materializer, OutputSpec outputSpec) throws Exception {
        ImmutableSet<IRI> properties = materializer.getProperties();

        int total = properties.size();
        AtomicInteger i = new AtomicInteger();
        for (IRI p : properties) {
            serializePredicate(materializer, p, PredicateType.PROPERTY, i.incrementAndGet(), total, outputSpec);
        }
    }

    /**
     * Serializes the A-box corresponding to a predicate into one or multiple file.
     */
    private void serializePredicate(RDF4JMaterializer materializer, IRI predicateIRI,
                                    PredicateType predicateType, int index, int total,
                                    OutputSpec outputSpec) throws Exception {
        final long startTime = System.currentTimeMillis();


        System.err.println(String.format("Materializing %s (%d/%d)", predicateIRI, index, total));
        System.err.println("Starts writing triples into files.");

        int tripleCount = 0;
        int fileCount = 0;

        String fileSubstring = predicateIRI.toString().replaceAll("[^a-zA-Z0-9]", "_")
                + predicateType.getCode() + "_";

        GraphQueryResult result = materializer.materialize(ImmutableSet.of(predicateIRI)).evaluate();

        while (result.hasNext()) {
            BufferedWriter writer = outputSpec.createWriter(Optional.of(fileSubstring + fileCount));
            tripleCount += serializeTripleBatch(
                    result,
                    Optional.of(TRIPLE_LIMIT_PER_FILE),
                    writer,
                    outputSpec.createRDFHandler(writer)
            );
            fileCount++;
        }

        System.out.println("NR of TRIPLES: " + tripleCount);

        final long endTime = System.currentTimeMillis();
        final long time = endTime - startTime;
        System.out.println("Elapsed time to materialize: " + time + " {ms}");
    }

    /**
     * Serializes a batch of triples into one file.
     * Upper bound: TRIPLE_LIMIT_PER_FILE.
     */
    private int serializeTripleBatch(GraphQueryResult result, Optional<Integer> limitPerFile, BufferedWriter writer, RDFHandler handler) throws IOException {
        int tripleCount = 0;
        handler.startRDF();
        while (result.hasNext() && (!limitPerFile.isPresent() || tripleCount < limitPerFile.get())) {
            handler.handleStatement(result.next());
            tripleCount++;
        }
        handler.endRDF();
        writer.close();
        return tripleCount;
    }

    /**
     * Mapping file + connection info
     */
    private Builder<? extends Builder> createAndInitConfigurationBuilder() {

        final Builder<? extends Builder> configBuilder = OntopSQLOWLAPIConfiguration.defaultBuilder();

        if (isR2rmlFile(mappingFile)) {
            configBuilder.r2rmlMappingFile(mappingFile);
        } else {
            configBuilder.nativeOntopMappingFile(mappingFile);
        }

        return configBuilder
                .propertyFile(propertiesFile)
                .enableOntologyAnnotationQuerying(true);
    }

    private class OutputSpec {
        private final Optional<String> prefix;
        private final String format;

        private OutputSpec(String prefix, String format) {
            this.prefix = Optional.of(removeExtension(prefix));
            this.format = format;
        }

        private OutputSpec(String format) {
            this.prefix = Optional.empty();
            this.format = format;
        }

        // We need a direct access to the writer to close it (cannot be done via the RDFHandler)
        private BufferedWriter createWriter(Optional<String> prefixExtension) throws IOException {
            if (prefix.isPresent()) {
                String suffix = getSuffix();
                return Files.newBufferedWriter(
                        prefixExtension.isPresent() ?
                                Paths.get(prefix.get(), prefixExtension.get() + suffix) :
                                Paths.get(prefix.get() + suffix),
                        Charset.forName("UTF-8")
                );
            }
            return new BufferedWriter(new OutputStreamWriter(System.out, "UTF-8"));
        }

        private String getSuffix() {
            switch (format) {
                case RDF_XML:
                    return ".rdf";
                case TURTLE:
                    return ".ttl";
                case NTRIPLES:
                    return ".nt";
                default:
                    throw new RuntimeException("Unknown output format: " + format);
            }
        }

        private RDFHandler createRDFHandler(BufferedWriter writer) {
            switch (format) {
                case RDF_XML:
                    return new RDFXMLWriter(writer);
                case TURTLE:
                    TurtleWriter tw  = new TurtleWriter(writer);
                    tw.set(BasicWriterSettings.PRETTY_PRINT, false);
                    return tw;
                case NTRIPLES:
                    NTriplesWriter btw  = new NTriplesWriter(writer);
                    btw.set(BasicWriterSettings.PRETTY_PRINT, false);
                default:
                    throw new RuntimeException("Unknown output format: " + format);
            }
        }
    }
}
