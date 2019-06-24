package org.elastos.hive.vendors.onedrive.network;

import org.elastos.hive.vendors.onedrive.Model.NoBodyEntity;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Package: org.elastos.hive.vendors.onedrive.network
 * ClassName: NobodyConverterFactory
 * Created by ranwang on 2019/6/24.
 */
public class NobodyConverterFactory extends Converter.Factory {
    public static final NobodyConverterFactory create() {
        return new NobodyConverterFactory();
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (NoBodyEntity.class.equals(type)) {
            return new Converter<ResponseBody, NoBodyEntity>() {
                @Override public NoBodyEntity convert(ResponseBody value) throws IOException {
                    return null;
                }
            };
        }
        return super.responseBodyConverter(type, annotations, retrofit);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return null;
    }

    @Override
    public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return null;
    }
}


