package io.akarin.api;

import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CheckedConcurrentLinkedQueue<E> extends ConcurrentLinkedQueue<E> implements Queue<E>, Serializable {
    
}