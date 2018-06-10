package org.apache.spark.sql;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.CombineFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.CombineFileRecordReader;
import org.apache.hadoop.mapreduce.lib.input.CombineFileRecordReaderWrapper;
import org.apache.hadoop.mapreduce.lib.input.CombineFileSplit;
import org.apache.parquet.hadoop.ParquetInputFormat;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.parser.SparkParserConfig;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by taofu on 2018/6/10.
 */
public class CombineParquetInputFormat<T> extends CombineFileInputFormat<Void, T> {

    @Override
    public RecordReader<Void, T> createRecordReader(InputSplit inputSplit, TaskAttemptContext context) throws IOException {
        CombineFileSplit combineSplit = (CombineFileSplit)inputSplit;
        return new CombineFileRecordReader(combineSplit, context, CombineParquetrecordReader.class);
    }

    private static class CombineParquetrecordReader<T> extends CombineFileRecordReaderWrapper<Void, T> {

        public CombineParquetrecordReader(
                CombineFileSplit split, TaskAttemptContext context, Integer idx) throws
                IOException, InterruptedException {
            super(new ParquetInputFormat(GroupReadSupport.class), split, context, idx);
        }
    }

    public static class CombineFilter implements PathFilter, Serializable {
        @Override
        public boolean accept(Path path) {
            try{
                if(SparkParserConfig.skipPathFilter()){
                    return true;
                }
                SparkContext sparkContext = SparkSqlExtraCommand.sparkContext();
                FileSystem fileSystem = path.getFileSystem(sparkContext.hadoopConfiguration());
                FileStatus status = fileSystem.getFileStatus(path);
                long fileLength = status.getLen();
                long maxLength = Long.parseLong(sparkContext.getConf().get(SparkParserConfig.sparkMergeFilterSize(), "134217728"));
                if(status.isFile() && !path.getName().startsWith(".") && fileLength<=maxLength){
                    return true;
                }
            } catch (Exception ignored){

            }
            return false;
        }
    }
}
