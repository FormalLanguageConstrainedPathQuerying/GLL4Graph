package benchmark;

import apoc.ApocSettings;
import apoc.create.Create;
import apoc.export.csv.ImportCsv;
import apoc.help.Help;
import apoc.periodic.Periodic;
import iguana.utils.input.GraphInput;
import iguana.utils.input.Neo4jBenchmarkInput;
import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.exceptions.KernelException;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.io.fs.FileUtils;
import org.neo4j.kernel.api.procedure.GlobalProcedures;
import org.neo4j.kernel.internal.GraphDatabaseAPI;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;

public class BenchmarkGraphNeo4j extends BenchmarkGraphStorage {

    private int nodesCount;

    BenchmarkGraphNeo4j() {

    }

    private final BiFunction<Relationship, Direction, String> relationship2Label =
            (Relationship relationship, Direction direction) ->
                    switch (direction) {
                        case INCOMING -> relationship.getType().name() + "_r";
                        case OUTGOING -> relationship.getType().name();
                        default -> throw new RuntimeException("Unexpected direction");
                    };
    private final File databaseDirectory = new File("target/neo4j-hello-db");
    private GraphDatabaseService graphDb;
    private DatabaseManagementService managementService;

    private void registerProcedure(GraphDatabaseService graphDb, List<Class<?>> procedures) {
        GlobalProcedures globalProcedures = ((GraphDatabaseAPI) graphDb).getDependencyResolver().resolveDependency(GlobalProcedures.class);
        for (Class<?> procedure : procedures) {
            try {
                globalProcedures.registerProcedure(procedure, true);
                globalProcedures.registerFunction(procedure, true);
                globalProcedures.registerAggregationFunction(procedure, true);
            } catch (KernelException e) {
                throw new RuntimeException("while registering " + procedure, e);
            }
        }
    }

    private Neo4jBenchmarkInput graphInput = null;

    @Override
    public void loadGraph(String path) throws IOException {

        try (var lines = Files.lines(Paths.get(path + File.separatorChar + "nodes.csv"))) {
            nodesCount = (int) lines.count();
        }

        FileUtils.deleteRecursively(databaseDirectory);
        managementService =
                new DatabaseManagementServiceBuilder(databaseDirectory)
                        //.setConfig(GraphDatabaseSettings.pagecache_memory, "100G")
                        .setConfig(GraphDatabaseSettings.tx_state_max_off_heap_memory, java.lang.Long.parseLong("24000000000"))
                        .setConfig(GraphDatabaseSettings.pagecache_warmup_enabled, true)
                        .setConfig(GraphDatabaseSettings.procedure_whitelist, List.of("gds.*", "apoc.*", "apoc.import.*"))
                        .setConfig(GraphDatabaseSettings.procedure_unrestricted, List.of("gds.*", "apoc.*"))
                        .setConfig(GraphDatabaseSettings.default_allowed, "gds.*,apoc.*")
                        .setConfig(BoltConnector.enabled, true)
                        .setConfig(ApocSettings.apoc_import_file_enabled, true)
                        .setConfig(ApocSettings.apoc_import_file_use__neo4j__config, false)
                        .build();
        graphDb = managementService.database(DEFAULT_DATABASE_NAME);

        registerProcedure(graphDb, asList(
                Create.class,
                Help.class,
                ImportCsv.class,
                Periodic.class
        ));

        String neo4jPath = new File(path).getAbsolutePath();
        try (Transaction tx = graphDb.beginTx()) {
            tx.execute("CALL apoc.import.csv(" +
                    "[{fileName: 'FILE:///" + neo4jPath + File.separator + "nodes.csv', labels: ['Node']}], " +
                    "[{fileName: 'FILE:///" + neo4jPath + File.separator + "edges.csv'}], " +
                    "{delimiter: ' ', stringIds: false}" +
                    ")");
            tx.commit();
        }
        System.out.println("Graph loaded");
    }

    @Override
    public GraphInput getGraphInput(Stream<Integer> startVertices) {
        graphInput = new Neo4jBenchmarkInput(graphDb, relationship2Label, startVertices, nodesCount);
        return graphInput;
    }

    @Override
    protected void onIterationFinish() {
        graphInput.close();
        graphInput = null;
    }

    @Override
    protected void close() {
        try (Transaction tx = graphDb.beginTx()) {
            tx.getAllNodes().forEach(node -> {
                node.getRelationships().forEach(Relationship::delete);
                node.delete();
            });
            tx.commit();
        }
        managementService.shutdown();
    }

    @Override
    public String toString() {
        return "NEO4J";
    }

}