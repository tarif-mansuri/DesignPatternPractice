package com.assignment.question;

public class FileBasedConfigurationManagerImpl extends FileBasedConfigurationManager{
    private static FileBasedConfigurationManagerImpl instance;

    private FileBasedConfigurationManagerImpl(){
        super();
    }

    @Override
    public String getConfiguration(String key) {
        return instance.properties.getProperty(key);
    }

    @Override
    public <T> T getConfiguration(String key, Class<T> type) {
        String val = getConfiguration(key);
        if(val!=null){
            return instance.convert(val, type);
        }
        return null;
    }

    @Override
    public void setConfiguration(String key, String value) {
        instance.properties.put(key, value);
    }

    @Override
    public <T> void setConfiguration(String key, T value) {
        instance.properties.put(key, value.toString());
    }

    @Override
    public void removeConfiguration(String key) {
        instance.properties.remove(key);
    }

    @Override
    public void clear() {
        instance.properties.clear();
    }

    public static FileBasedConfigurationManager getInstance() {
        if(instance == null) {
            synchronized (FileBasedConfigurationManagerImpl.class) {
                if(instance == null) {
                    instance = new FileBasedConfigurationManagerImpl();
                }
            }
        }
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }
}
