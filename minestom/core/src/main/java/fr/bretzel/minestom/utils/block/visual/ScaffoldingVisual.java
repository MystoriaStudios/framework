/**
 * ### AUTOGENERATED ###
 */

package fr.bretzel.minestom.utils.block.visual;

import fr.bretzel.minestom.states.ScaffoldingState;
import fr.bretzel.minestom.utils.block.shapes.BlockStateShapes;
import net.minestom.server.instance.block.Block;

public class ScaffoldingVisual extends BlockStateShapes<ScaffoldingState> {
    public ScaffoldingVisual(Block alternative) {
        super(alternative);

        add(0.0, 0.875, 0.0, 1.0, 1.0, 1.0);
        add(0.0, 0.0, 0.0, 0.125, 0.875, 0.125);
        add(0.0, 0.0, 0.875, 0.125, 0.875, 1.0);
        add(0.875, 0.0, 0.875, 1.0, 0.875, 1.0);
        add(0.875, 0.0, 0.0, 1.0, 0.875, 0.125);

        if (states().hasBottom())
            add(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
    }
}