package net.revive.framework.component


enum class ClickType {
    /**
     * The left (or primary) mouse button.
     */
    LEFT,

    /**
     * Holding shift while pressing the left mouse button.
     */
    SHIFT_LEFT,

    /**
     * The right mouse button.
     */
    RIGHT,

    /**
     * Holding shift while pressing the right mouse button.
     */
    SHIFT_RIGHT,

    /**
     * Clicking the left mouse button on the grey area around the inventory.
     */
    WINDOW_BORDER_LEFT,

    /**
     * Clicking the right mouse button on the grey area around the inventory.
     */
    WINDOW_BORDER_RIGHT,

    /**
     * The middle mouse button, or a "scrollwheel click".
     */
    MIDDLE,

    /**
     * One of the number keys 1-9, correspond to slots on the hotbar.
     */
    NUMBER_KEY,

    /**
     * Pressing the left mouse button twice in quick succession.
     */
    DOUBLE_CLICK,

    /**
     * The "Drop" key (defaults to Q).
     */
    DROP,

    /**
     * Holding Ctrl while pressing the "Drop" key (defaults to Q).
     */
    CONTROL_DROP,

    /**
     * Any action done with the Creative inventory open.
     */
    CREATIVE,

    /**
     * The "swap item with offhand" key (defaults to F).
     */
    SWAP_OFFHAND,

    /**
     * A type of inventory manipulation not yet recognized by Bukkit.
     *
     *
     * This is only for transitional purposes on a new Minecraft update, and
     * should never be relied upon.
     *
     *
     * Any ClickType.UNKNOWN is called on a best-effort basis.
     */
    UNKNOWN;

    val isKeyboardClick: Boolean
        /**
         * Gets whether this ClickType represents the pressing of a key on a
         * keyboard.
         *
         * @return true if this ClickType represents the pressing of a key
         */
        get() = (this == NUMBER_KEY) || (this == DROP) || (this == CONTROL_DROP) || (this == SWAP_OFFHAND)
    val isMouseClick: Boolean
        /**
         * Gets whether this ClickType represents the pressing of a mouse button
         *
         * @return true if this ClickType represents the pressing of a mouse button
         */
        get() = ((this == DOUBLE_CLICK) || (this == LEFT) || (this == RIGHT) || (this == MIDDLE)
                || (this == WINDOW_BORDER_LEFT) || (this == SHIFT_LEFT) || (this == SHIFT_RIGHT) || (this == WINDOW_BORDER_RIGHT))
    val isCreativeAction: Boolean
        /**
         * Gets whether this ClickType represents an action that can only be
         * performed by a Player in creative mode.
         *
         * @return true if this action requires Creative mode
         */
        get() {
            // Why use middle click?
            return (this == MIDDLE) || (this == CREATIVE)
        }
    val isRightClick: Boolean
        /**
         * Gets whether this ClickType represents a right click.
         *
         * @return true if this ClickType represents a right click
         */
        get() {
            return (this == RIGHT) || (this == SHIFT_RIGHT)
        }
    val isLeftClick: Boolean
        /**
         * Gets whether this ClickType represents a left click.
         *
         * @return true if this ClickType represents a left click
         */
        get() {
            return (this == LEFT) || (this == SHIFT_LEFT) || (this == DOUBLE_CLICK) || (this == CREATIVE)
        }
    val isShiftClick: Boolean
        /**
         * Gets whether this ClickType indicates that the shift key was pressed
         * down when the click was made.
         *
         * @return true if the action uses Shift.
         */
        get() {
            return (this == SHIFT_LEFT) || (this == SHIFT_RIGHT)
        }
}

