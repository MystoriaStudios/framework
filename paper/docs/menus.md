# Menus
A guide on how to implement both `IMenu`'s and `AbstractPagedMenu`'s.

## `IMenu`
```kt
import net.mystoria.framework.menu.IMenu

class TestMenu : IMenu {

    override val metaData: IMenu.MetaData = IMenu.MetaData()

    override fun getTitle(player: Player): Component 
    {
        return Component.text("Test MENU !!!")
    }

    override fun getButtons(player: Player): Map<Int, IButton> 
    {
        val buttons = mutableMapOf<Int, IButton>()

        buttons[15] = TestButton() // Adds a button at row 2, column 7

        return buttons
    }

    override fun size(buttons: Map<Int, IButton>): Int 
    {
        return 27
    }

    inner class TestButton : IButton 
    {
        override fun getMaterial(player: Player) = XMaterial.PAPER

        override fun getButtonItem(player: Player) = {
                name(Component.text("Test Test "))
            }
    }
}
```

## `IButton`
```kt
import net.mystoria.framework.menu.button.IButton

class ExampleButton : IButton 
{
    override fun getMaterial(player: Player) = XMaterial.DIAMOND_SWORD
}
interface IButton {
    fun getMaterial(player: Player) : XMaterial
    fun getButtonItem(player: Player): ItemStackBuilder.() -> Unit

    fun onClick(player: Player, type: ClickType) {}
    fun onClick(player: Player, type: ClickType, event: InventoryClickEvent) {}

    // change to itembuilder
    fun applyTexture(player: Player): ItemStackBuilder.() -> Unit {
        return {

        }
    }
}
```