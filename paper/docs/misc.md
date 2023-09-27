# Miscellaneous
how to utilize stuff like ItemBuilders and more...

```kt
import net.mystoria.framework.util.ItemBuilder

val item: ItemStack = ItemBuilder {
    name(
        Component.text(
            "Example ItemStack Name",
             NamedTextColor.GREEN
            )
    )

    lore(/*Just a vararg of kyori Components...*/)
} // Returns an ItemStack
```