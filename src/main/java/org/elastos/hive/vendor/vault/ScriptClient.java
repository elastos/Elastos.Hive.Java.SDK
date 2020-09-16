package org.elastos.hive.vendor.vault;

import com.fasterxml.jackson.databind.JsonNode;

import org.elastos.hive.Scripting;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.scripting.Condition;
import org.elastos.hive.scripting.Executable;
import org.elastos.hive.utils.JsonUtil;
import org.elastos.hive.utils.ResponseHelper;
import org.elastos.hive.vendor.connection.ConnectionManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class ScriptClient implements Scripting {

    private VaultAuthHelper authHelper;

    public ScriptClient(VaultAuthHelper authHelper) {
        this.authHelper = authHelper;
    }

    @Override
    public CompletableFuture<Boolean> registerScript(String name, Executable executable) throws HiveException {
        return this.registerScript(name, null, executable);
    }

    @Override
    public CompletableFuture<Boolean> registerScript(String name, Condition accessCondition, Executable executable) throws HiveException {
        return authHelper.checkValid()
                .thenCompose(result -> registerScriptImp(name, accessCondition, executable));
    }

    private CompletableFuture<Boolean> registerScriptImp(String name, Condition accessCondition, Executable executable) {
        return CompletableFuture.supplyAsync(() -> {
            try {

                Map map = new HashMap<>();
                map.put("name", name);
                if (accessCondition != null)
                    map.put("condition", accessCondition);
                map.put("executable", executable);

                String json = JsonUtil.getJsonFromObject(map);

                Response<ResponseBody> response = ConnectionManager.getHiveVaultApi()
                        .registerScript(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();
                authHelper.checkResponseCode(response);
                ResponseBody baseResponse = response.body();
                return null!=baseResponse;
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public <T> CompletableFuture<T> call(String scriptName, Class<T> clazz) {
        return this.call(scriptName, null, clazz);
    }

    @Override
    public <T> CompletableFuture<T> call(String scriptName, JsonNode params, Class<T> clazz) {
        return authHelper.checkValid()
                .thenCompose(result -> callImp(scriptName, params, clazz));
    }

    private <T> CompletableFuture<T> callImp(String scriptName, JsonNode params, Class<T> clazz) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map map = new HashMap<>();
                map.put("name", scriptName);
                if (params != null)
                    map.put("params", params);

                String json = JsonUtil.getJsonFromObject(map);


                Response response = ConnectionManager.getHiveVaultApi()
                        .callScript(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                        .execute();
                authHelper.checkResponseCode(response);
                return ResponseHelper.getValue(response, clazz);
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                throw new CompletionException(exception);
            }
        });
    }

}
