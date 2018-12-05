package com.mfvanek.hibernate.utils;

import com.mfvanek.hibernate.consts.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesUtil {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    private PropertiesUtil() {}

    public static Properties load() {
        final Properties properties = new Properties();
        try (InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(Const.PROPERTY_FILE_NAME)) {
            properties.load(inputStream);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return properties;
    }
}
