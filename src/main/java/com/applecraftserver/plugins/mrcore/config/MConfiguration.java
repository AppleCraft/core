package com.applecraftserver.plugins.mrcore.config;

import com.applecraftserver.plugins.mrcore.exceptions.InvalidWorldException;
import com.google.common.io.Files;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * This, and frankly, most of the configuration, user handling, and cross-plugin talk is how Essentials works.
 * Credit to them for making something that works great in this situation/this purpose.
 */

public class MConfiguration extends YamlConfiguration {
    protected static final Logger log = Logger.getLogger("mr-core");
    protected static final Charset utf8 = Charset.forName("UTF-8");
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
    protected final File configFile;
    private final AtomicInteger pendingDiskWrites = new AtomicInteger(0);
    private final AtomicBoolean transaction = new AtomicBoolean(false);
    private final byte[] buf = new byte[1024];
    protected String templatename = null;
    private Class<?> thisclass = MConfiguration.class;

    public MConfiguration(final File confFile) {
        super();
        this.configFile = confFile.getAbsoluteFile();
    }

    public static BigDecimal toBigDecimal(final String input, final BigDecimal def) {
        if (input == null || input.isEmpty()) {
            return def;
        } else {
            try {
                return new BigDecimal(input, MathContext.DECIMAL128);
            } catch (NumberFormatException e) {
                return def;
            } catch (ArithmeticException e) {
                return def;
            }
        }
    }

    public synchronized void load() {
        if (pendingDiskWrites.get() != 0) {
            log.log(Level.INFO, "File {0} not read, because it''s not yet written to disk.", configFile);
            return;
        }
        if (!configFile.getParentFile().exists()) {
            if (!configFile.getParentFile().mkdirs()) {
                log.log(Level.SEVERE, "Failed to create configuration file: " + configFile.toString());
            }
        }
        // This will delete files where the first character is 0. In most cases they are broken.
        if (configFile.exists() && configFile.length() != 0) {
            try {
                final InputStream input = new FileInputStream(configFile);
                try {
                    if (input.read() == 0) {
                        input.close();
                        configFile.delete();
                    }
                } catch (IOException ex) {
                    log.log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        input.close();
                    } catch (IOException ex) {
                        log.log(Level.SEVERE, null, ex);
                    }
                }
            } catch (FileNotFoundException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }

        if (!configFile.exists()) {
            log.log(Level.INFO, "Creating configuration from template: " + configFile.toString());
            createFromTemplate();
        }


        try {
            final FileInputStream inputStream = new FileInputStream(configFile);
            try {
                long startSize = configFile.length();
                if (startSize > Integer.MAX_VALUE) {
                    throw new InvalidConfigurationException("File too big");
                }
                ByteBuffer buffer = ByteBuffer.allocate((int) startSize);
                int length;
                while ((length = inputStream.read(buf)) != -1) {
                    if (length > buffer.remaining()) {
                        ByteBuffer resize = ByteBuffer.allocate(buffer.capacity() + length - buffer.remaining());
                        int resizePosition = buffer.position();
                        buffer.rewind();
                        resize.put(buffer);
                        resize.position(resizePosition);
                        buffer = resize;
                    }
                    buffer.put(buf, 0, length);
                }
                buffer.rewind();
                final CharBuffer data = CharBuffer.allocate(buffer.capacity());
                CharsetDecoder decoder = utf8.newDecoder();
                CoderResult result = decoder.decode(buffer, data, true);
                if (result.isError()) {
                    buffer.rewind();
                    data.clear();
                    log.log(Level.INFO, "File " + configFile.getAbsolutePath().toString() + " is not utf-8 encoded, trying " + Charset.defaultCharset().displayName());
                    decoder = Charset.defaultCharset().newDecoder();
                    result = decoder.decode(buffer, data, true);
                    if (result.isError()) {
                        throw new InvalidConfigurationException("Invalid characters in file " + configFile.getAbsolutePath().toString());
                    } else decoder.flush(data);
                } else {
                    decoder.flush(data);
                }
                final int end = data.position();
                data.rewind();
                super.loadFromString(data.subSequence(0, end).toString());
            } finally {
                inputStream.close();
            }
        } catch (IOException ex) {
            log.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (InvalidConfigurationException ex) {
            File broken = new File(configFile.getAbsolutePath() + ".broken." + System.currentTimeMillis());
            configFile.renameTo(broken);
            log.log(Level.SEVERE, "The file " + configFile.toString() + " is broken, it has been renamed to " + broken.toString(), ex.getCause());
        }
    }

    public boolean altFileExists() {
        return false;
    }

    public void convertAltFile() {
        log.log(Level.SEVERE, "Unable to import alternate config file.");
    }

    private void createFromTemplate() {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = thisclass.getResourceAsStream(templatename);
            if (in == null) {
                log.log(Level.SEVERE, "Couldn't find a template named " + templatename);
                return;
            }
            out = new FileOutputStream(configFile);
            byte[] buffer = new byte[1024];
            int length = 0;
            length = in.read(buffer);
            while (length > 0) {
                out.write(buffer, 0, length);
                length = in.read(buffer);
            }
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Failed to write to configuration file: " + configFile.toString(), ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(MConfiguration.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                log.log(Level.SEVERE, "Failed to close configuration properly: " + configFile.toString(), ex);
            }
        }
    }

    public void setTemplateName(final String templateName) {
        this.templatename = templateName;
    }

    public File getFile() {
        return configFile;
    }

    public void setTemplateName(final String templateName, final Class<?> resClass) {
        this.templatename = templateName;
        this.thisclass = resClass;
    }

    public void startTransaction() {
        transaction.set(true);
    }

    public void stopTransaction() {
        transaction.set(false);
        save();
    }

    public void save() {
        try {
            save(configFile);
        } catch (IOException ex) {
            log.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void saveWithError() throws IOException {
        save(configFile);
    }

    @Override
    public synchronized void save(final File file) throws IOException {
        if (!transaction.get()) {
            delayedSave(file);
        }
    }

    public synchronized void forceSave() {
        try {
            Future<?> future = delayedSave(configFile);
            if (future != null) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException ex) {
            log.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public synchronized void cleanup() {
        forceSave();
    }

    private Future<?> delayedSave(final File file) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }

        final String data = saveToString();

        if (data.length() == 0) {
            return null;
        }

        Future<?> future = EXECUTOR_SERVICE.submit(new WriteRunner(configFile, data, pendingDiskWrites));

        return future;
    }

    public boolean hasProperty(final String path) {
        return isSet(path);
    }

    public Location getLocation(final String path, final Server server) throws InvalidWorldException {
        final String worldString = (path == null ? "" : path + ".") + "world";
        final String worldName = getString(worldString);
        if (worldName == null || worldName.isEmpty()) {
            return null;
        }
        final World world = server.getWorld(worldName);
        if (world == null) {
            throw new InvalidWorldException(worldName);
        }
        return new Location(world,
                getDouble((path == null ? "" : path + ".") + "x", 0),
                getDouble((path == null ? "" : path + ".") + "y", 0),
                getDouble((path == null ? "" : path + ".") + "z", 0),
                (float) getDouble((path == null ? "" : path + ".") + "yaw", 0),
                (float) getDouble((path == null ? "" : path + ".") + "pitch", 0));
    }

    public void setLocation(final String path, final Location loc) {
        set((path == null ? "" : path + ".") + "world", loc.getWorld().getName());
        set((path == null ? "" : path + ".") + "x", loc.getX());
        set((path == null ? "" : path + ".") + "y", loc.getY());
        set((path == null ? "" : path + ".") + "z", loc.getZ());
        set((path == null ? "" : path + ".") + "yaw", loc.getYaw());
        set((path == null ? "" : path + ".") + "pitch", loc.getPitch());
    }

    @Override
    public ItemStack getItemStack(final String path) {
        final ItemStack stack = new ItemStack(
                Material.valueOf(getString(path + ".type", "AIR")),
                getInt(path + ".amount", 1),
                (short) getInt(path + ".damage", 0));
        final ConfigurationSection enchants = getConfigurationSection(path + ".enchant");
        if (enchants != null) {
            for (String enchant : enchants.getKeys(false)) {
                final Enchantment enchantment = Enchantment.getByName(enchant.toUpperCase(Locale.ENGLISH));
                if (enchantment == null) {
                    continue;
                }
                final int level = getInt(path + ".enchant." + enchant, enchantment.getStartLevel());
                stack.addUnsafeEnchantment(enchantment, level);
            }
        }
        return stack;
        /*
		 * ,
		 * (byte)getInt(path + ".data", 0)
		 */
    }

    public void setProperty(final String path, final ItemStack stack) {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", stack.getType().toString());
        map.put("amount", stack.getAmount());
        map.put("damage", stack.getDurability());
        Map<Enchantment, Integer> enchantments = stack.getEnchantments();
        if (!enchantments.isEmpty()) {
            Map<String, Integer> enchant = new HashMap<String, Integer>();
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                enchant.put(entry.getKey().getName().toLowerCase(Locale.ENGLISH), entry.getValue());
            }
            map.put("enchant", enchant);
        }
        // getData().getData() is broken
        //map.put("data", stack.getDurability());
        set(path, map);
    }

    public void setAnyList(String path, List object) {
        set(path, new ArrayList(object));
    }

    public void setAnyMap(String path, Map object) {
        set(path, new LinkedHashMap(object));
    }

    public Object getAny(String path) {
        return get(path);
    }

    public void setBigDecimal(final String path, final BigDecimal bigDecimal) {
        set(path, bigDecimal.toString());
    }

    public void setObject(String path, Object object) {
        set(path, object);
    }

    public void deleteNode(String path) {
        set(path, null);
    }

    @Override
    public synchronized void set(String path, Object value) {
        super.set(path, value);
    }

    @Override
    public synchronized Object get(String path) {
        return super.get(path);
    }

    @Override
    public synchronized Object get(String path, Object def) {
        return super.get(path, def);
    }

    public synchronized BigDecimal getBigDecimal(final String path, final BigDecimal def) {
        final String input = super.getString(path);
        return toBigDecimal(input, def);
    }

    @Override
    public synchronized boolean getBoolean(String path) {
        return super.getBoolean(path);
    }

    @Override
    public synchronized boolean getBoolean(String path, boolean def) {
        return super.getBoolean(path, def);
    }

    @Override
    public synchronized List<Boolean> getBooleanList(String path) {
        return super.getBooleanList(path);
    }

    @Override
    public synchronized List<Byte> getByteList(String path) {
        return super.getByteList(path);
    }

    @Override
    public synchronized List<Character> getCharacterList(String path) {
        return super.getCharacterList(path);
    }

    @Override
    public synchronized ConfigurationSection getConfigurationSection(String path) {
        return super.getConfigurationSection(path);
    }

    @Override
    public synchronized double getDouble(String path) {
        return super.getDouble(path);
    }

    @Override
    public synchronized double getDouble(final String path, final double def) {
        return super.getDouble(path, def);
    }

    @Override
    public synchronized List<Double> getDoubleList(String path) {
        return super.getDoubleList(path);
    }

    @Override
    public synchronized List<Float> getFloatList(String path) {
        return super.getFloatList(path);
    }

    @Override
    public synchronized int getInt(String path) {
        return super.getInt(path);
    }

    @Override
    public synchronized int getInt(String path, int def) {
        return super.getInt(path, def);
    }

    @Override
    public synchronized List<Integer> getIntegerList(String path) {
        return super.getIntegerList(path);
    }

    @Override
    public synchronized ItemStack getItemStack(String path, ItemStack def) {
        return super.getItemStack(path, def);
    }

    @Override
    public synchronized Set<String> getKeys(boolean deep) {
        return super.getKeys(deep);
    }

    @Override
    public synchronized List<?> getList(String path) {
        return super.getList(path);
    }

    @Override
    public synchronized List<?> getList(String path, List<?> def) {
        return super.getList(path, def);
    }

    @Override
    public synchronized long getLong(String path) {
        return super.getLong(path);
    }

    @Override
    public synchronized long getLong(final String path, final long def) {
        return super.getLong(path, def);
    }

    @Override
    public synchronized List<Long> getLongList(String path) {
        return super.getLongList(path);
    }

    public synchronized Map<String, Object> getMap() {
        return map;
    }

    @Override
    public synchronized List<Map<?, ?>> getMapList(String path) {
        return super.getMapList(path);
    }

    @Override
    public synchronized OfflinePlayer getOfflinePlayer(String path) {
        return super.getOfflinePlayer(path);
    }

    @Override
    public synchronized OfflinePlayer getOfflinePlayer(String path, OfflinePlayer def) {
        return super.getOfflinePlayer(path, def);
    }

    @Override
    public synchronized List<Short> getShortList(String path) {
        return super.getShortList(path);
    }

    @Override
    public synchronized String getString(String path) {
        return super.getString(path);
    }

    @Override
    public synchronized String getString(String path, String def) {
        return super.getString(path, def);
    }

    @Override
    public synchronized List<String> getStringList(String path) {
        return super.getStringList(path);
    }

    @Override
    public synchronized Map<String, Object> getValues(boolean deep) {
        return super.getValues(deep);
    }

    @Override
    public synchronized Vector getVector(String path) {
        return super.getVector(path);
    }

    @Override
    public synchronized Vector getVector(String path, Vector def) {
        return super.getVector(path, def);
    }

    @Override
    public synchronized boolean isBoolean(String path) {
        return super.isBoolean(path);
    }

    @Override
    public synchronized boolean isConfigurationSection(String path) {
        return super.isConfigurationSection(path);
    }

    @Override
    public synchronized boolean isDouble(String path) {
        return super.isDouble(path);
    }

    @Override
    public synchronized boolean isInt(String path) {
        return super.isInt(path);
    }

    @Override
    public synchronized boolean isItemStack(String path) {
        return super.isItemStack(path);
    }

    @Override
    public synchronized boolean isList(String path) {
        return super.isList(path);
    }

    @Override
    public synchronized boolean isLong(String path) {
        return super.isLong(path);
    }

    @Override
    public synchronized boolean isOfflinePlayer(String path) {
        return super.isOfflinePlayer(path);
    }

    @Override
    public synchronized boolean isSet(String path) {
        return super.isSet(path);
    }

    @Override
    public synchronized boolean isString(String path) {
        return super.isString(path);
    }

    @Override
    public synchronized boolean isVector(String path) {
        return super.isVector(path);
    }

    private static class WriteRunner implements Runnable {
        private final File configFile;
        private final String data;
        private final AtomicInteger pendingDiskWrites;

        private WriteRunner(final File configFile, final String data, final AtomicInteger pendingDiskWrites) {
            this.configFile = configFile;
            this.data = data;
            this.pendingDiskWrites = pendingDiskWrites;
        }

        @Override
        public void run() {
            //long startTime = System.nanoTime();
            synchronized (configFile) {
                if (pendingDiskWrites.get() > 1) {
                    // Writes can be skipped, because they are stored in a queue (in the executor).
                    // Only the last is actually written.
                    pendingDiskWrites.decrementAndGet();
                    //log.log(Level.INFO, configFile + " skipped writing in " + (System.nanoTime() - startTime) + " nsec.");
                    return;
                }
                try {
                    Files.createParentDirs(configFile);

                    if (!configFile.exists()) {
                        try {
                            log.log(Level.INFO, "Creating blank configuration: " + configFile.toString());
                            if (!configFile.createNewFile()) {
                                log.log(Level.SEVERE, "Failed to create configuration: " + configFile.toString());
                                return;
                            }
                        } catch (IOException ex) {
                            log.log(Level.SEVERE, "Failed to create configuration: " + configFile.toString());
                            return;
                        }
                    }

                    final FileOutputStream fos = new FileOutputStream(configFile);
                    try {
                        final OutputStreamWriter writer = new OutputStreamWriter(fos, utf8);

                        try {
                            writer.write(data);
                        } finally {
                            writer.close();
                        }
                    } finally {
                        fos.close();
                    }
                } catch (IOException e) {
                    log.log(Level.SEVERE, e.getMessage(), e);
                } finally {
                    pendingDiskWrites.decrementAndGet();
                }
            }
        }
    }
}
