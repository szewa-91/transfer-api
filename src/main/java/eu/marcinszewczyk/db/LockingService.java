package eu.marcinszewczyk.db;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class LockingService {
    private ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    Lock getLock(String accountNumber) {
        return locks.computeIfAbsent(accountNumber, a -> new ReentrantLock());
    }
}
