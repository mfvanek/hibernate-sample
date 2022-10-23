package com.mfvanek.hibernate.utils;

import com.mfvanek.hibernate.consts.Const;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
@UtilityClass
public final class PropertiesUtil {

    public static Properties load() {
        final Properties properties = new Properties();
        try (InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(Const.PROPERTY_FILE_NAME)) {
            properties.load(inputStream);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return properties;
    }
}
