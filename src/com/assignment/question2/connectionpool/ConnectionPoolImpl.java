package com.assignment.question2.connectionpool;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectionPoolImpl implements ConnectionPool {
    private static ConnectionPoolImpl instance = null;
    private static final ReentrantLock lock = new ReentrantLock();

    private final int maxConnections;
    private final Queue<DatabaseConnection> availableConnections = new LinkedList<>();
    private final Set<DatabaseConnection> inUseConnections = new HashSet<>();

    private ConnectionPoolImpl(int maxConnections) {
        if (maxConnections <= 0) {
            throw new IllegalArgumentException("Maximum connections must be greater than 0.");
        }
        this.maxConnections = maxConnections;
        initializePool();
    }

    @Override
    public void initializePool() {
        for (int i = 1; i <= maxConnections; i++) {
            availableConnections.add(new DatabaseConnection());
        }
    }

    @Override
    public DatabaseConnection getConnection() {
        lock.lock();
        try {
            if (availableConnections.isEmpty()) {
                throw new IllegalStateException("No available connections.");
            }
            DatabaseConnection connection = availableConnections.poll();
            inUseConnections.add(connection);
            return connection;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void releaseConnection(DatabaseConnection connection) {
        lock.lock();
        try {
            if (inUseConnections.remove(connection)) {
                availableConnections.add(connection);
            } else {
                throw new IllegalArgumentException("Connection does not belong to this pool.");
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getAvailableConnectionsCount() {
        lock.lock();
        try {
            return availableConnections.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getTotalConnectionsCount() {
        lock.lock();
        try {
            return availableConnections.size() + inUseConnections.size();
        } finally {
            lock.unlock();
        }
    }

    public static ConnectionPoolImpl getInstance(int maxConnections) {
        if (instance == null) {
            synchronized (ConnectionPoolImpl.class) {
                if (instance == null) {
                    instance = new ConnectionPoolImpl(maxConnections);
                }
            }
        }
        return instance;
    }

    public static void resetInstance() {
        synchronized (ConnectionPoolImpl.class) {
            instance = null;
        }
    }
}
