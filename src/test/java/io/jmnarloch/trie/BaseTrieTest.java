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

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * The base class for all Trie tests.
 *
 * @author Jakub Narloch
 */
public abstract class BaseTrieTest {

    private Trie<String> instance;

    @Before
    public void setUp() {

        instance = createTrie();
        for (String value : getValues()) {
            instance.put(value, value);
        }
    }

    @Test
    public void shouldBeEmpty() {

        // given
        instance = createTrie();

        // expect
        assertTrue(instance.isEmpty());
    }

    @Test
    public void shouldHaveSizeOfZero() {

        // given
        instance = createTrie();

        // expect
        assertEquals(0, instance.size());
    }

    @Test
    public void shouldReturnsCorrectTrieSize() {

        // expect
        assertEquals(getValues().size(), instance.size());
    }

    @Test
    public void shouldNotAlterSizeOnDuplicates() {

        // given
        for (String value : getValues()) {
            instance.put(value, value);
        }

        // expect
        assertEquals(getValues().size(), instance.size());
    }

    @Test
    public void shouldPutAllEntries() {

        // given
        instance = createTrie();

        // and
        final Map<String, String> entries = new HashMap<String, String>();
        for(String value : getValues()) {
            entries.put(value, value);
        }

        // when
        instance.putAll(entries);

        // then
        for (String value : getValues()) {
            // when
            final String result = instance.get(value);

            // then
            assertEquals(value, result);
        }
    }
    
    @Test
    public void shouldFindAllMatchingKeys() {

        for (String value : getValues()) {
            // when
            final String result = instance.get(value);

            // then
            assertEquals(value, result);
        }
    }

    @Test
    public void shouldFindAllPrefixKeys() {

        for (String value : getValues()) {
            // when
            final String result = instance.prefixKey(value);

            // then
            assertEquals(value, result);
        }
    }

    @Test
    public void shouldFindAllPrefixKeysValues() {

        for (String value : getValues()) {
            // when
            final String result = instance.prefix(value);

            // then
            assertEquals(value, result);
        }
    }

    @Test
    public void shouldFindAllExistingKeys() {

        for (String value : getValues()) {
            // when
            final boolean exists = instance.containsKey(value);

            // then
            assertTrue(exists);
        }
    }

    @Test
    public void shouldReplaceKeyValues() {

        // given
        final String replaced = "replaced";
        for (String value : getValues()) {
            String prev = instance.put(value, replaced);

            assertEquals(value, prev);
        }

        for (String value : getValues()) {
            // when
            final String result = instance.get(value);

            // then
            assertEquals(replaced, result);
        }
    }

    @Test
    public void shouldRemoveAllEntries() {

        // when
        for (String value : getValues()) {
            instance.remove(value);
        }

        // then
        assertTrue(instance.isEmpty());
        assertTrue(instance.size() == 0);
        for (String value : getValues()) {
            assertNull(instance.get(value));
        }
    }

    @Test
    public void shouldGetAllKeys() {

        // given
        final Set<String> values = getValues();

        // when
        final Set<String> keys = instance.keySet();

        assertEquals(values.size(), keys.size());
        for (String value : values) {
            // then
            assertTrue(keys.contains(value));
        }
    }

    protected Set<String> getValues() {

        return new HashSet<String>(Arrays.asList(
                "/uaa/**",
                "/user/**",
                "/account/**",
                "/api/**",
                "/notifications/**",
                "/ws/**"
        ));
    }

    protected abstract Trie<String> createTrie();
}