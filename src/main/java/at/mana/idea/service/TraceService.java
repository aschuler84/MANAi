package at.mana.idea.service;

import at.mana.idea.domain.Sample;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public interface TraceService {

    Sample attributeTraces(Sample sample,
                           JsonObject rootEntry, JsonArray childEntries );

}
