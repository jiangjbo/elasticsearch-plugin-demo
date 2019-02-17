package com.jbo.demo.util;

import org.elasticsearch.ElasticsearchParseException;
import org.elasticsearch.ExceptionsHelper;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.loader.JsonSettingsLoader;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.common.xcontent.json.JsonXContent;

import java.io.IOException;
import java.util.Map;

public class JsonUtils {

    public static Settings convertBytesReferenceToSettings(BytesReference bytesReference) {
        if(bytesReference == null || bytesReference.length() == 0){
            return Settings.EMPTY;
        }
        try {
            Map<String, String> map = new JsonSettingsLoader().load(XContentHelper.createParser(bytesReference));
            return Settings.builder().put(map).build();
        } catch (IOException e) {
            throw ExceptionsHelper.convertToElastic(e);
        }
    }

    public static Settings convertStructuredMapToSettings(Map<String, Object> structuredMap) {
        try {
            XContentBuilder xContentBuilder = JsonXContent.contentBuilder().map(structuredMap);
            return convertBytesReferenceToSettings(xContentBuilder.bytes());
        } catch (IOException e) {
            throw ExceptionsHelper.convertToElastic(e);
        }
    }

    public static Map<String, Object> convertStringToStructuredMap(String json) {
        return convertSettingsToStructuredMap(Settings.builder().loadFromSource(json).build());
    }

    public static Map<String, Object> convertSettingsToStructuredMap(Settings settings) {
        return settings.getAsStructuredMap();
    }

    public static Map<String, Object> convertBytesReferenceToStructuredMap(BytesReference bytesReference) {
        return convertBytesReferenceToSettings(bytesReference).getAsStructuredMap();
    }

    public static Map<String, Object> convertBytesReferenceToStructuredMap(BytesReference bytesReference, XContentType xContentType) {
        try {
            Tuple<XContentType, Map<String, Object>> tuple = XContentHelper.convertToMap(bytesReference, false);
            if(tuple.v1().equals(xContentType)){
                return tuple.v2();
            } else{
                throw new IOException(String.format("%s is not match %s", xContentType.toString(), tuple.v1()));
            }
        } catch (IOException e1) {
            throw ExceptionsHelper.convertToElastic(e1);
        }
    }

    public static BytesReference convertStructuredMapToBytesReference(Map<String, Object> structuredMap) {
        try {
            XContentBuilder xContentBuilder = JsonXContent.contentBuilder().map(structuredMap);
            return xContentBuilder.bytes();
        } catch (IOException e) {
            throw new ElasticsearchParseException("Failed to convert map", e);
        }
    }

    public static BytesReference convertSettingsToBytesReference(Settings settings) {
        return convertStructuredMapToBytesReference(settings.getAsStructuredMap());
    }

    public static String convertBytesReferenceToJson(BytesReference bytesReference) {
        if(bytesReference == null || bytesReference.length() == 0){
            return "{}";
        }
        return convertSettingsToJson(convertBytesReferenceToSettings(bytesReference));
    }

    public static String convertStructuredMapToJson(Map<String, Object> structuredMap) {
        try {
            return XContentHelper.convertToJson(convertStructuredMapToBytesReference(structuredMap), false, false);
        } catch (IOException e) {
            throw new ElasticsearchParseException("Failed to convert map", e);
        }
    }

    public static String convertSettingsToJson(Settings settings) {
        try {
            return XContentHelper.convertToJson(convertSettingsToBytesReference(settings), false);
        } catch (IOException e) {
            throw new ElasticsearchParseException("Failed to convert map", e);
        }
    }


}
