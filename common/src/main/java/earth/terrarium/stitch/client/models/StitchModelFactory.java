package earth.terrarium.stitch.client.models;

import com.google.gson.JsonObject;

import java.util.function.Supplier;

public interface StitchModelFactory {

    Supplier<StitchBlockModel> create(JsonObject json);
}
