# Menus
A guide on how to implement both `IMenu`'s and `AbstractPagedMenu`'s.

## `IMenu`
```kt
import net.revive.framework.menu.IMenu

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
import net.revive.framework.menu.button.IButton

class ExampleButton : IButton 
{
    override fun getMaterial(player: Player) = XMaterial.DIAMOND_SWORD
}
```