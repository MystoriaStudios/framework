package fr.bretzel.minestom.placement.rules;

import fr.bretzel.minestom.placement.PlacementRule;
import fr.bretzel.minestom.states.BlockState;
import fr.bretzel.minestom.states.state.BooleanState;
import fr.bretzel.minestom.states.state.Directional;
import fr.bretzel.minestom.states.state.Facing;
import fr.bretzel.minestom.utils.block.BlockUtils;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;

import java.util.Arrays;
import java.util.List;

public class CardinalPlacement extends PlacementRule {

    private static final List<String> cardinalPlacement = Arrays.asList(
            "minecraft:acacia_fence",
            "minecraft:birch_fence",
            "minecraft:black_stained_glass_pane",
            "minecraft:blue_stained_glass_pane",
            "minecraft:brown_stained_glass_pane",
            "minecraft:crimson_fence",
            "minecraft:cyan_stained_glass_pane",
            "minecraft:dark_oak_fence",
            "minecraft:glass_pane",
            "minecraft:gray_stained_glass_pane",
            "minecraft:green_stained_glass_pane",
            "minecraft:iron_bars",
            "minecraft:jungle_fence",
            "minecraft:light_blue_stained_glass_pane",
            "minecraft:light_gray_stained_glass_pane",
            "minecraft:lime_stained_glass_pane",
            "minecraft:magenta_stained_glass_pane",
            "minecraft:nether_brick_fence",
            "minecraft:oak_fence",
            "minecraft:orange_stained_glass_pane",
            "minecraft:pink_stained_glass_pane",
            "minecraft:purple_stained_glass_pane",
            "minecraft:red_stained_glass_pane",
            "minecraft:spruce_fence",
            "minecraft:tripwire",
            "minecraft:warped_fence",
            "minecraft:white_stained_glass_pane",
            "minecraft:yellow_stained_glass_pane");

    private static final List<String> noDown = Arrays.asList(
            "minecraft:fire",
            "minecraft:vine");

    private static final List<String> cardinalWithUp = Arrays.asList(
            "minecraft:brown_mushroom_block",
            "minecraft:chorus_plant",
            "minecraft:fire",
            "minecraft:mushroom_stem",
            "minecraft:red_mushroom_block",
            "minecraft:vine");

    private final boolean isMushroom;

    public CardinalPlacement(Block block) {
        super(block);

        this.isMushroom = block.name().contains("mushroom");
    }

    public static boolean isCardinalBlock(Block block) {
        return cardinalPlacement.contains(block.name()) ||
                cardinalWithUp.contains(block.name());
    }

    @Override
    public boolean canPlace(BlockState blockState, BlockPlacementRule.PlacementState placementState) {
        var instance = placementState.instance();
        var blockPosition = placementState.placePosition();
        var blockFace = placementState.blockFace();

        var block = new BlockUtils((Instance) instance, blockPosition);

        if (noDown.contains(blockState.block().name())) {
            var blockNorth = block.north().block().isSolid();
            var blockSouth = block.south().block().isSolid();
            var blockEast = block.east().block().isSolid();
            var blockWest = block.west().block().isSolid();
            var blockUp = block.up().block().isSolid();
            var blockDown = block.down().block().isSolid();

            return blockNorth || blockSouth || blockEast || blockWest || blockUp || blockDown;
        }
        return (block() != Block.VINE || blockFace != BlockFace.TOP) && canAttach(block.relative(Facing.parse(blockFace.getOppositeFace())).block());
    }

    @Override
    public boolean canUpdate(BlockState blockState, BlockPlacementRule.UpdateState updateState) {
        return true;
    }

    @Override
    public void update(BlockState blockState, BlockPlacementRule.UpdateState updateState) {
        placeOrUpdate((Instance) updateState.instance(), updateState.blockPosition(), blockState);
    }

    @Override
    public void place(BlockState blockState, BlockPlacementRule.PlacementState placementState) {
        placeOrUpdate((Instance) placementState.instance(), placementState.placePosition(), blockState);
    }

    public void placeOrUpdate(Instance instance, Point blockPosition, BlockState blockState) {
        var block = new BlockUtils(instance, blockPosition);

        var blockNorth = block.north();
        var blockSouth = block.south();
        var blockEast = block.east();
        var blockWest = block.west();

        var east = isCardinalBlock(blockEast.block()) || canAttach(blockEast.block());
        var north = isCardinalBlock(blockNorth.block()) || canAttach(blockNorth.block());
        var south = isCardinalBlock(blockSouth.block()) || canAttach(blockSouth.block());
        var west = isCardinalBlock(blockWest.block()) || canAttach(blockWest.block());

        if (isMushroom) {
            east = !east;
            north = !north;
            south = !south;
            west = !west;
        }

        blockState.set(Directional.EAST, BooleanState.Of(east));
        blockState.set(Directional.NORTH, BooleanState.Of(north));
        blockState.set(Directional.SOUTH, BooleanState.Of(south));
        blockState.set(Directional.WEST, BooleanState.Of(west));

        if (hasUp(blockState.block())) {
            var blockUp = block.up();
            blockState.set(Directional.UP, BooleanState.Of(isMushroom != blockUp.block().isSolid()));
        }

        if (hasDown(blockState.block())) {
            var blockDown = block.down();
            blockState.set(Directional.DOWN, BooleanState.Of(isMushroom != blockDown.block().isSolid()));
        }
    }

    public boolean hasUp(Block block) {
        return cardinalWithUp.contains(block.name());
    }

    public boolean hasDown(Block block) {
        return cardinalWithUp.contains(block.name()) && !noDown.contains(block.name());
    }

    public boolean canAttach(Block block) {
        return block() == Block.VINE ? (block != Block.VINE && block.isSolid()) : !block.isAir() && block.isSolid();
    }
}
