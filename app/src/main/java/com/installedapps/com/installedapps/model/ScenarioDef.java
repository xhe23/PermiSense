package com.installedapps.com.installedapps.model;

import androidx.room.TypeConverter;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property="@class")
public abstract class ScenarioDef {
    public static class Converter {
        private static ObjectMapper objectMapper = new ObjectMapper();

        @TypeConverter
        public static ScenarioDef stringToScenarioDef(String s) throws JsonProcessingException {
            return objectMapper.readValue(s, ScenarioDef.class);
        }

        @TypeConverter
        public static String scenarioDefToString(ScenarioDef d) throws JsonProcessingException {
            return objectMapper.writeValueAsString(d);
        }
    }
}
