package com.installedapps.com.installedapps.model;

import android.util.Log;

import androidx.room.TypeConverter;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property="@class")
public abstract class ScenarioDef {
    public static class Converter {
        private static ObjectMapper objectMapper = new ObjectMapper();

        @TypeConverter
        public static ScenarioDef stringToScenarioDef(String s) {
            try {
                return objectMapper.readValue(s, ScenarioDef.class);
            } catch (JsonProcessingException e) {
                Log.e("PermiSense", "Failed scenario def json conversion", e);
                return null;
            }
        }

        @TypeConverter
        public static String scenarioDefToString(ScenarioDef d) {
            try {
                return objectMapper.writeValueAsString(d);
            } catch (JsonProcessingException e) {
                Log.e("PermiSense", "Failed scenario def json conversion", e);
                return null;
            }
        }
    }
}
