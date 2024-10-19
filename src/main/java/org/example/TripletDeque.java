package org.example;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class TripletDeque<E> implements Deque<E>, Containerable, Iterable<E> {

    private static final int DEFAULT_CONTAINER_SIZE = 5;
    private static final int DEFAULT_QUEUE_CAPACITY = 1000;

    private static class Container<E> {
        E[] elements;
        Container<E> prev;
        Container<E> next;
        int size;
        
        Container(int capacity) {
            elements = (E[]) new Object[capacity];
            prev = null;
            next = null;
            size = 0;
        }
    }

    private Container<E> head;
    private Container<E> tail;
    private int totalSize;
    private final int containerSize;
    private final int queueCapacity;

    public TripletDeque() {
        this(DEFAULT_CONTAINER_SIZE, DEFAULT_QUEUE_CAPACITY);
    }

    public TripletDeque(int containerSize, int queueCapacity) {
        this.containerSize = containerSize;
        this.queueCapacity = queueCapacity;
        head = new Container<>(containerSize);
        tail = head;
        totalSize = 0;
    }

    @Override
    public void addFirst(E e) {
        if (e == null) throw new NullPointerException("Вы пытаетесь добавить нулевой элемент");
        if (totalSize >= queueCapacity) {
            throw new IllegalStateException("Queue capacity exceeded");
        }
        if (head.size == containerSize) {
            Container<E> newHead = new Container<>(containerSize);
            newHead.next = head;
            head.prev = newHead;
            head = newHead;
        }
        // Добавляем элемент в начало контейнера
        for (int i = head.size; i > 0; i--) {
            head.elements[i] = head.elements[i - 1];
        }
        head.elements[0] = e;
        head.size++;
        totalSize++;
    }

    @Override
    public void addLast(E e) {
        if (e == null) throw new NullPointerException("Вы пытаетесь добавить нулевой элемент");
        if (totalSize >= queueCapacity) {
            throw new IllegalStateException("Queue capacity exceeded");
        }
        if (tail.size == containerSize) {
            Container<E> newTail = new Container<>(containerSize);
            newTail.prev = tail;
            tail.next = newTail;
            tail = newTail;
        }
        // Добавляем элемент в конец контейнера
        tail.elements[tail.size] = e;
        tail.size++;
        totalSize++;
    }

    @Override
    public boolean offerFirst(E e) {
        try {
            addFirst(e);
            return true;
        } catch (IllegalStateException ex) {
            return false;
        }
    }

    @Override
    public boolean offerLast(E e) {
        try {
            addLast(e);
            return true;
        } catch (IllegalStateException ex) {
            return false;
        }
    }

    @Override
    public E removeFirst() {
        E item = pollFirst();
        if (item == null) {
            throw new NoSuchElementException("Deque is empty");
        }
        return item;
    }

    @Override
    public E removeLast() {
        E item = pollLast();
        if (item == null) {
            throw new NoSuchElementException("Deque is empty");
        }
        return item;
    }

    @Override
    public E pollFirst() {
        if (isEmpty()) {
            return null;
        }
        E item = head.elements[0];
        for (int i = 0; i < head.size - 1; i++) {
            head.elements[i] = head.elements[i + 1];
        }
        head.elements[head.size - 1] = null;
        head.size--;
        totalSize--;
        if (head.size == 0 && head.next != null) {
            head = head.next;
            head.prev = null;
        }
        return item;
    }

    @Override
    public E pollLast() {
        if (isEmpty()) {
            return null;
        }
        E item = tail.elements[tail.size - 1];
        tail.elements[tail.size - 1] = null;
        tail.size--;
        totalSize--;
        if (tail.size == 0 && tail.prev != null) {
            tail = tail.prev;
            tail.next = null;
        }
        return item;
    }

    @Override
    public E getFirst() {
        E item = peekFirst();
        if (item == null) {
            throw new NoSuchElementException("Deque is empty");
        }
        return item;
    }

    @Override
    public E getLast() {
        E item = peekLast();
        if (item == null) {
            throw new NoSuchElementException("Deque is empty");
        }
        return item;
    }

    @Override
    public E peekFirst() {
        if (isEmpty()) {
            return null;
        }
        return head.elements[0];
    }

    @Override
    public E peekLast() {
        if (isEmpty()) {
            return null;
        }
        return tail.elements[tail.size - 1];
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        Container<E> current = head;
        while (current != null) {
            for (int i = 0; i < current.size; i++) {
                if (o.equals(current.elements[i])) {
                    // Сдвигаем элементы влево
                    for (int j = i; j < current.size - 1; j++) {
                        current.elements[j] = current.elements[j + 1];
                    }
                    // Устанавливаем последний элемент в null
                    current.elements[current.size - 1] = null;
                    current.size--;
                    totalSize--;

                    // Перераспределяем элементы, если контейнер стал пустым или содержит только null
                    redistributeElements(current);

                    return true;
                }
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        Container<E> current = tail;
        while (current != null) {
            for (int i = current.size - 1; i >= 0; i--) {
                if (o.equals(current.elements[i])) {
                    // Сдвигаем элементы влево
                    for (int j = i; j < current.size - 1; j++) {
                        current.elements[j] = current.elements[j + 1];
                    }
                    // Устанавливаем последний элемент в null
                    current.elements[current.size - 1] = null;
                    current.size--;
                    totalSize--;

                    // Перераспределяем элементы, если контейнер стал пустым или содержит только null
                    redistributeElements(current);

                    return true;
                }
            }
            current = current.prev;
        }
        return false;
    }

    @Override
    public boolean add(E e) {
        return offerLast(e);
    }

    @Override
    public boolean offer(E e) {
        return offerLast(e);
    }

    @Override
    public E remove() {
        return removeFirst();
    }

    @Override
    public E poll() {
        return pollFirst();
    }

    @Override
    public E element() {
        return getFirst();
    }

    @Override
    public E peek() {
        return peekFirst();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        for (E e : c) {
            if (!offerLast(e)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void push(E e) {
        addFirst(e);
    }

    @Override
    public E pop() {
        return removeFirst();
    }

    @Override
    public boolean remove(Object o) {
        return removeFirstOccurrence(o);
    }

    @Override
    public boolean contains(Object o) {
        Container<E> current = head;
        while (current != null) {
            for (int i = 0; i < current.size; i++) {
                if (o.equals(current.elements[i])) {
                    return true;
                }
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public int size() {
        return totalSize;
    }

    @Override
    public Iterator<E> iterator() {
        return new DequeIterator();
    }

    @Override
    public Iterator<E> descendingIterator() {
        throw new UnsupportedOperationException("descendingIterator() is not implemented");
    }

    @Override
    public boolean isEmpty() {
        return totalSize == 0;
    }

    @Override
    public void clear() {
        head = new Container<>(containerSize);
        tail = head;
        totalSize = 0;
    }

    @Override
    public Object[] getContainerByIndex(int cIndex) {
        Container<E> current = head;
        int index = 0;
        while (current != null) {
            if (index == cIndex) {
                return current.elements;
            }
            current = current.next;
            index++;
        }
        return null;
    }

    private class DequeIterator implements Iterator<E> {
        private Container<E> currentContainer = head;
        private int index = 0;

        @Override
        public boolean hasNext() {
            return currentContainer != null && (index < currentContainer.size || currentContainer.next != null);
        }

        @Override
        public E next() {
            if (currentContainer == null) {
                throw new NoSuchElementException("No more elements in the deque");
            }
            if (index >= currentContainer.size) {
                currentContainer = currentContainer.next;
                index = 0;
            }
            if (currentContainer == null) {
                throw new NoSuchElementException("No more elements in the deque");
            }
            return currentContainer.elements[index++];
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Container<E> current = head;
        while (current != null) {
            for (int i = 0; i < current.size; i++) {
                sb.append(current.elements[i]);
                if (i < current.size - 1 || current.next != null) {
                    sb.append(", ");
                }
            }
            current = current.next;
        }
        sb.append("]");
        return sb.toString();
    }

    // Вспомогательные методы для removeFirstOccurrence и removeFirstOccurrence, чтобы не было дырок
    private void redistributeElements(Container<E> container) {
        // Проверяем, стал ли контейнер пустым или содержит только null
        if (container.size == 0 || allElementsAreNull(container)) {
            // Если контейнер был головой, обновляем голову
            if (container == head) {
                head = container.next;
            }
            // Если контейнер был хвостом, обновляем хвост
            if (container == tail) {
                tail = container.prev;
            }

            // Связываем предыдущий и следующий контейнеры
            if (container.prev != null) {
                container.prev.next = container.next;
            }
            if (container.next != null) {
                container.next.prev = container.prev;
            }

            // Перераспределяем элементы между соседними контейнерами
            if (container.prev != null && container.next != null) {
                redistributeBetweenContainers(container.prev, container.next);
            }
        }
    }

    private boolean allElementsAreNull(Container<E> container) {
        for (int i = 0; i < container.size; i++) {
            if (container.elements[i] != null) {
                return false;
            }
        }
        return true;
    }

    private void redistributeBetweenContainers(Container<E> left, Container<E> right) {
        // Вычисляем общее количество элементов
        int totalElements = left.size + right.size;

        // Если общее количество элементов меньше или равно размеру одного контейнера
        if (totalElements <= left.elements.length) {
            // Перемещаем все элементы из правого контейнера в левый
            for (int i = 0; i < right.size; i++) {
                left.elements[left.size++] = right.elements[i];
            }
            // Очищаем правый контейнер
            for (int i = 0; i < right.size; i++) {
                right.elements[i] = null;
            }
            right.size = 0;
        } else {
            // Иначе перераспределяем элементы между контейнерами
            int leftCapacity = left.elements.length - left.size;
            int rightCapacity = right.elements.length - right.size;

            // Перемещаем элементы из правого контейнера в левый, пока левый контейнер не заполнится
            while (left.size < left.elements.length && right.size > 0) {
                left.elements[left.size++] = right.elements[--right.size];
                right.elements[right.size] = null;
            }

            // Если после перемещения в правом контейнере остались элементы
            if (right.size > 0) {
                // Сдвигаем элементы в правом контейнере влево
                for (int i = 0; i < right.size; i++) {
                    right.elements[i] = right.elements[i + leftCapacity];
                    right.elements[i + leftCapacity] = null;
                }
            }
        }
    }

}