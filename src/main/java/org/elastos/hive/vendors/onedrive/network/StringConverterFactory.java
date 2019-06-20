package org.elastos.hive.vendors.onedrive.network;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Package: org.elastos.hive.vendors.onedrive.network
 * ClassName: StringConverterFactory
 * Created by ranwang on 2019/6/19.
 */
public class StringConverterFactory extends Converter.Factory {
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (type == String.class) {
            return new StringConverter();
        }
        return null;
    }

    class StringConverter implements Converter<ResponseBody, String> {
        @Override
        public String convert(ResponseBody value) throws IOException {
            return value.string();
        }
    }
}
