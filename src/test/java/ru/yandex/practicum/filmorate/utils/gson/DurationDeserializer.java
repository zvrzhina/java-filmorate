package ru.yandex.practicum.filmorate.utils.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.Duration;

public class DurationDeserializer implements JsonDeserializer<Duration> {
    @Override
    public Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return Duration.parse(json.getAsString());
    }
}