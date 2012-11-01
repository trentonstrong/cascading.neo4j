package org.pingles.cascading.neo4j.hadoop;

import cascading.flow.FlowProcess;
import cascading.scheme.Scheme;
import cascading.scheme.SinkCall;
import cascading.scheme.SourceCall;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntry;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.RecordReader;
import org.pingles.cascading.neo4j.IndexSpec;

import java.io.IOException;

public class Neo4jRelationshipScheme  extends Scheme<JobConf, RecordReader, OutputCollector, Object[], Object[]> {
    private final Fields fields;
    private final IndexSpec fromIndex;
    private final IndexSpec toIndex;

    public Neo4jRelationshipScheme(Fields fields, IndexSpec fromIndex, IndexSpec toIndex) {
        this.fields = fields;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    @Override
    public void sourceConfInit(FlowProcess<JobConf> jobConfFlowProcess, Tap<JobConf, RecordReader, OutputCollector> jobConfRecordReaderOutputCollectorTap, JobConf entries) {
        throw  new UnsupportedOperationException("Sink only");
    }

    @Override
    public void sinkConfInit(FlowProcess<JobConf> flowProcess, Tap<JobConf, RecordReader, OutputCollector> tap, JobConf jobConf) {
        jobConf.setOutputKeyClass(Tuple.class);
        jobConf.setOutputValueClass(Tuple.class);
        jobConf.setOutputFormat(Neo4jOutputFormat.class);
    }

    @Override
    public boolean source(FlowProcess<JobConf> jobConfFlowProcess, SourceCall<Object[], RecordReader> recordReaderSourceCall) throws IOException {
        return false;
    }

    @Override
    public void sink(FlowProcess<JobConf> flowProcess, SinkCall<Object[], OutputCollector> sinkCall) throws IOException {
        OutputCollector collector = sinkCall.getOutput();
        TupleEntry outgoingEntry = sinkCall.getOutgoingEntry();

        Neo4jRelationshipTuple rel = new Neo4jRelationshipTuple(fromIndex, toIndex, outgoingEntry);

        collector.collect(Tuple.NULL, rel);
    }
}
