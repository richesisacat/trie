/**
 * Copyright (c) 2015-2016 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jmnarloch.trie;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Benchmark the {@link Tst}.
 *
 * @author Jakub Narloch
 */
public class TstBenchmark extends BaseTrieBenchmark {

    @Override
    protected Trie<String> createTrie() {
        return new Tst<>();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
          .include(TstBenchmark.class.getSimpleName())
          .forks(1)
          .warmupIterations(1)
          .measurementIterations(1)
          .build();

        new Runner(opt).run();
    }
}
