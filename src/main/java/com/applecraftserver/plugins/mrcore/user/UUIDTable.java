package com.applecraftserver.plugins.mrcore.user;

import com.applecraftserver.plugins.mrcore.api.ICore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class UUIDTable {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
    private final ICore core;
    private final transient Pattern comma = Pattern.compile(",");
    private final AtomicInteger pendingDiskWrites = new AtomicInteger(0);
    private File file;

    public UUIDTable(ICore core) {
        this.core = core;
        this.file = new File(core.getDataFolder(), "usertable.csv");
    }

    public void loadAll(final ConcurrentSkipListMap<String, UUID> names, final ConcurrentSkipListMap<UUID, ArrayList<String>> history) {
        try {
            if (!file.exists()) file.createNewFile();

            synchronized (pendingDiskWrites) {
                names.clear();
                history.clear();

                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    while (true) {
                        String line = reader.readLine();

                        if (line == null) break;

                        final String[] values = comma.split(line);

                        if (values.length == 2) {
                            final String username = values[0];
                            final UUID id = UUID.fromString(values[1]);

                            names.putIfAbsent(username, id);

                            if (!history.containsKey(id)) {
                                final ArrayList<String> nameHistory = new ArrayList<>();

                                nameHistory.add(username);
                                history.put(id, nameHistory);
                            } else {
                                final ArrayList<String> nameHistory = new ArrayList<>();
                                if (!nameHistory.contains(username)) nameHistory.add(username);
                            }

                        }

                    }
                }

            }

        } catch (IOException knewThisWasGoingToHappen) {
            core.getLogger().severe("Error loading user table!");
            knewThisWasGoingToHappen.printStackTrace();
        }
    }

    public void writeTable() {
        futureWriteTable();
    }

    private Future<?> futureWriteTable() {
        final ConcurrentSkipListMap<String, UUID> names = core.getUsers().getNames();
        if (names.size() < 1) return null;

        pendingDiskWrites.incrementAndGet();
        Future<?> future = EXECUTOR_SERVICE.submit(new WriteRunner(core.getDataFolder(), file, names, pendingDiskWrites));

        return future;
    }

    private static class WriteRunner implements Runnable {
        public WriteRunner(File dataFolder, File file, ConcurrentSkipListMap<String, UUID> names, AtomicInteger pendingDiskWrites) {
        }

        @Override
        public void run() {

        }
    }
}
