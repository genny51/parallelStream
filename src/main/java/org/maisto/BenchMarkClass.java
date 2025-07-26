package org.maisto;

import org.openjdk.jmh.annotations.*;

import java.util.stream.LongStream;
import java.util.stream.Stream;

/*
 * Java stream api provides us the parallel() api. This method allows to execute tasks in parallel leveraging all hardware powers. More the cpu's core more the power.
 * Obviusly the execution of tasks in parallel comports a wide enhancement in terms of time performance. But... we have to know when use it.
 * Under the hood, parallel uses the fork/join framework. So it splits the stream into sub-streams , assign each to a free thread to the execution of task
 * and finally combines all partial results into one. The threads are these of fork/join thread pool, by default equals to cores of cpu.
 * But what we want to highlight is that sometimes this method can worse the performance.
 * We will take an example and we'll launch a benchmark to measure time performance. The example will be very easy: the sum of first N numbers
 * */

@BenchmarkMode(Mode.AverageTime) //It measures the average time of benchmarked methods
@OutputTimeUnit(java.util.concurrent.TimeUnit.MILLISECONDS)
@Fork(value = 2,jvmArgs = {"-Xms4G","-Xmx4G"})//It exetus the benchmarck 2 times to improve the reliability of results and uses 4Gb of heap space
public class BenchMarkClass {
    //BenchMark of several methods responsible of the sum of n numbers
    private final static long N = 10_000_000;

/*
to take the measure we use the iterate method of stream api.
Limit it to N and finally calculate the result
*/

    /*@Benchmark
    public long sequentialStreamSum(){
        return Stream.iterate(1L, i -> i+1)
                .limit(N)
                .reduce(0L,Long::sum);
    }*/

    /*@Benchmark
    public long parallelStreamSum(){
        return Stream.iterate(1L,i -> i+1)
                .limit(N)
                .parallel()
                .reduce(0L,Long::sum);
    }*/
/*As we can see from benchmark, time performance with parallel stream are five times worsen than the sequential one. Why?
Iterate method is not easily splittable.It is linked to the preceding result, it calculates the next item from the previous one.
So split the stream in substream by calling parallel() lead to an overhead. This is an example of when NOT use parallel streams.
Let's take an other example.
Use range instead of iterate and see the results
 */

    @Benchmark
    public long sequentialStreamSum(){
        return LongStream.rangeClosed(0,N)
                .reduce(0L, Long::sum);
    }

    @Benchmark
    public long parralellStreamSum(){
        return LongStream.rangeClosed(0,N)
                .parallel()
                .reduce(0L, Long::sum);
    }
    /*As we can notice from results, rangeClosed is parallelizable and enhance time performance of almost three times against the sequantial version*/
}
