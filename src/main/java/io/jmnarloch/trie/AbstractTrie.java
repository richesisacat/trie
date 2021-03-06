/**
 * Copyright (c) 2015-2016 the original author or authors
 *
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
 */
package io.jmnarloch.trie;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * The base class for all {@link Trie} instances.
 *
 * @author Jakub Narloch
 */
abstract class AbstractTrie<T, N extends AbstractTrie.TrieNode<T, N>> implements Trie<T> {

    /**
     * A node factory.
     */
    private final TrieNodeFactory<T, N> nodeFactory;

    /**
     * The root node of the tree.
     */
    private N root;

    /**
     * Creates new instance of {@link AbstractTrie} with specific node factory.
     *
     * @param nodeFactory the node factory
     */
    public AbstractTrie(TrieNodeFactory<T, N> nodeFactory) {
        this.nodeFactory = nodeFactory;
        this.root = createTrieNode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return getRoot().getSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T put(String key, T value) {
        notEmpty(key, "Key must be not null or not empty string.");

        return put(getRoot(), key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putAll(Map<String, ? extends T> map) {
        notNull(map, "Map can not be null");

        final Set<? extends Map.Entry<String, ? extends T>> entries = map.entrySet();
        for(Map.Entry<String, ? extends T> entry : entries) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(String key) {
        notEmpty(key, "Key must be not null or not empty string.");

        return get(key) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T get(String key) {
        notEmpty(key, "Key must be not null or not empty string.");

        return get(getRoot(), key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T prefix(String key) {
        notEmpty(key, "Key must be not null or not empty string.");

        return prefix(getRoot(), key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String prefixKey(String key) {
        notEmpty(key, "Key must be not null or not empty string.");

        return prefixKey(getRoot(), key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T remove(String key) {
        notEmpty(key, "Key must be not null or not empty string.");

        return remove(getRoot(), key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> keySet() {

        final Set<String> keys = new HashSet<String>();
        keys(getRoot(), keys);
        return keys;
    }

    @Override
    public String filter(String key, String replace) {
        StringBuilder result = new StringBuilder();
        N tempNode = getRoot();
        int begin = 0;
        int position = 0;
        while (position < key.length()) {
            char c = key.charAt(position);
            if (isSymbol(c)) {
                if (tempNode == root) {
                    result.append(c);
                    ++begin;
                }
                ++position;
                continue;
            }
            tempNode = tempNode.getNext(c);
            if (tempNode == null) {
                result.append(key.charAt(begin));
                position = ++begin;
                tempNode = getRoot();
            } else if (tempNode.getValue() != null) {
                result.append(replace);
                ++position;
                begin = position;
                tempNode = getRoot();
            } else {
                ++position;
            }
        }
        result.append(key.substring(begin));
        return result.toString();
    }

    private boolean isSymbol(char c) {
        int ic = (int) c;
        // return ic < 0x2E80 || ic > 0x9FFF;
        return (ic < 0x2E80 || ic > 0x9FFF) && (ic < 0x61 || ic > 0x7a) && (ic < 0x41 || ic > 0x5a);
    }

    private T put(N root, String key, T value) {

        N node = root;
        final Deque<N> stack = new LinkedList<N>();
        stack.push(node);
        N next;
        int index = 0;

        while (index < key.length()) {
            final char c = getChar(key, index);
            next = node.getNext(c);
            if (next == null) {
                next = createTrieNode();
                node.setNext(c, next);
            }
            node = next;
            stack.push(node);
            index++;
        }
        final boolean replaced = node.hasValue();
        final T old = node.getValue();
        node.setValue(value);
        if(replaced) {
            return old;
        }

        while (!stack.isEmpty()) {
            node = stack.pop();
            node.setSize(node.getSize() + 1);
        }
        return null;
    }

    private T get(N node, String key) {

        final N found = getNode(node, key);
        if(found == null) {
            return null;
        }
        return found.getValue();
    }

    private N getNode(N node, String key) {
        int index = 0;
        while (node != null) {
            if (index == key.length()) {
                return node;
            }
            node = node.getNext(getChar(key, index));
            index++;
        }
        return null;
    }

    private T prefix(N node, String key) {

        T value = null;
        int index = 0;
        while (node != null) {
            if (node.hasValue()) {
                value = node.getValue();
            }
            if (index == key.length()) {
                break;
            }
            node = node.getNext(getChar(key, index));
            index++;
        }
        return value;
    }

    private String prefixKey(N node, String key) {

        final StringBuilder path = new StringBuilder();
        int longestPrefix = -1;
        int index = 0;
        while (node != null) {
            if (node.hasValue()) {
                longestPrefix = index;
            }
            if (index == key.length()) {
                break;
            }
            final char c = getChar(key, index);
            path.append(c);
            node = node.getNext(c);
            index++;
        }
        if(longestPrefix == -1) {
            return null;
        }
        return path.substring(0, longestPrefix);
    }

    private T remove(N root, String key) {

        int index = 0;
        N node = root;
        N next;
        final Deque<N> stack = new LinkedList<N>();

        while (index < key.length()) {
            stack.push(node);
            next = node.getNext(getChar(key, index));
            if(next == null) {
                return null;
            }
            node = next;
            index++;
        }
        if(!node.hasValue()) {
            return null;
        }
        final T value = node.getValue();
        node.setSize(node.getSize() - 1);
        node.removeValue();
        index = key.length() - 1;
        while(!stack.isEmpty()) {
            final char c = getChar(key, index);
            node = stack.pop();

            if(node.getNext(c).isEmpty()) {
                node.removeNext(c);
            }
            node.setSize(node.getSize() - 1);
            index--;
        }
        return value;
    }

    private void keys(N root, Set<String> keys) {

        N node;
        TraversedPath traversedPath;

        final StringBuilder path = new StringBuilder();
        final Deque<TraversedPath> stack = new LinkedList<TraversedPath>();
        stack.push(new TraversedPath(TraversedPathAction.VISIT, -1, root));

        while(!stack.isEmpty()) {
            traversedPath = stack.pop();
            if(traversedPath.action == TraversedPathAction.BACKUP) {
                path.deleteCharAt(path.length() - 1);
            } else {
                node = traversedPath.node;
                if(traversedPath.key != -1) {
                    path.append((char)traversedPath.key);
                }
                if(node.hasValue()) {
                    keys.add(path.toString());
                }
                for (char key : node.getKeys()) {
                    stack.push(new TraversedPath(TraversedPathAction.BACKUP, -1, null));
                    stack.push(new TraversedPath(TraversedPathAction.VISIT, key, node.getNext(key)));
                }
            }
        }
    }

    private char getChar(String key, int index) {
        return key.charAt(index);
    }

    private N getRoot() {
        return root;
    }

    private N createTrieNode() {
        return nodeFactory.createNode();
    }

    private void notNull(Object value, String message) {
        if(value == null) {
            throw new IllegalArgumentException(message);
        }
    }

    private void notEmpty(String value, String message) {
        notNull(value, message);
        if(value.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    interface TrieNodeFactory<T, N extends TrieNode<T, N>> {

        N createNode();
    }

    interface TrieNode<T, N extends TrieNode<T, N>> {

        boolean isEmpty();

        void setSize(int size);

        int getSize();

        void setNext(char c, N next);

        N getNext(char c);

        void removeNext(char c);

        void setValue(T value);

        T getValue();

        boolean hasValue();

        void removeValue();

        char[] getKeys();
    }

    private enum TraversedPathAction {
        VISIT, BACKUP
    }

    private final class TraversedPath {

        private final TraversedPathAction action;

        private final int key;

        private final N node;

        TraversedPath(TraversedPathAction action, int key, N node) {
            this.action = action;
            this.key = key;
            this.node = node;
        }
    }
}
