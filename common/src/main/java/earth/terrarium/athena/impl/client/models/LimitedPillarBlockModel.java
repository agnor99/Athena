package earth.terrarium.athena.impl.client.models;

import com.google.gson.JsonObject;
import earth.terrarium.athena.api.client.models.AthenaBlockModel;
import earth.terrarium.athena.api.client.models.AthenaModelFactory;
import earth.terrarium.athena.api.client.models.AthenaQuad;
import earth.terrarium.athena.api.client.utils.CtmUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class LimitedPillarBlockModel implements AthenaBlockModel {

    public static final AthenaModelFactory FACTORY = new Factory();

    private static final List<AthenaQuad> CENTER = List.of(AthenaQuad.withSprite(2));
    private static final List<AthenaQuad> TOP = List.of(AthenaQuad.withSprite(1));
    private static final List<AthenaQuad> BOTTOM = List.of(AthenaQuad.withSprite(3));
    private static final List<AthenaQuad> SELF = List.of(AthenaQuad.withSprite(4));
    private static final List<AthenaQuad> CAP = List.of(AthenaQuad.withSprite(0));

    private final Int2ObjectMap<Material> materials;

    public LimitedPillarBlockModel(Int2ObjectMap<Material> materials) {
        this.materials = materials;
    }

    @Override
    public List<AthenaQuad> getQuads(BlockAndTintGetter level, BlockState state, BlockPos pos, Direction direction) {
        final BlockState above = level.getBlockState(pos.relative(direction));

        if (above.is(state.getBlock())) {
            return List.of();
        }

        if (direction.getAxis().isVertical()) {
            return CAP;
        }

        final boolean min = level.getBlockState(pos.above()).is(state.getBlock());
        final boolean max = level.getBlockState(pos.below()).is(state.getBlock());

        if (min && max) {
            return CENTER;
        } else if (min) {
            return BOTTOM;
        } else if (max) {
            return TOP;
        }
        return SELF;
    }

    @Override
    public Int2ObjectMap<TextureAtlasSprite> getTextures(Function<Material, TextureAtlasSprite> getter) {
        Int2ObjectMap<TextureAtlasSprite> textures = new Int2ObjectArrayMap<>();
        for (var entry : materials.int2ObjectEntrySet()) {
            textures.put(entry.getIntKey(), getter.apply(entry.getValue()));
        }
        return textures;
    }

    private static class Factory implements AthenaModelFactory {

        @Override
        public Supplier<AthenaBlockModel> create(JsonObject json) {
            final var materials = parseMaterials(GsonHelper.getAsJsonObject(json, "ctm_textures"));
            return () -> new LimitedPillarBlockModel(materials);
        }

        private static Int2ObjectMap<Material> parseMaterials(JsonObject json) {
            Int2ObjectMap<Material> materials = new Int2ObjectArrayMap<>();
            materials.put(0, CtmUtils.blockMat(GsonHelper.getAsString(json, "particle")));
            materials.put(4, CtmUtils.blockMat(GsonHelper.getAsString(json, "self")));

            materials.put(1, CtmUtils.blockMat(GsonHelper.getAsString(json, "top")));
            materials.put(2, CtmUtils.blockMat(GsonHelper.getAsString(json, "center")));
            materials.put(3, CtmUtils.blockMat(GsonHelper.getAsString(json, "bottom")));

            return materials;
        }
    }
}
