package net.revive.framework.menu.distribution

import net.revive.framework.menu.button.IButton

enum class MenuDistributionRow(
    private val rowDistribution: (Int) -> List<Int>
)
{
    NONE({
        listOf()
    }),
    EVENLY({
        when (it)
        {
            0 -> listOf()
            1 -> listOf(4)
            2 -> listOf(3, 5)
            3 -> listOf(2, 4, 6)
            4 -> listOf(1, 3, 5, 7)
            5 -> listOf(0, 2, 4, 6, 8)
            6 -> listOf(1, 2, 3, 5, 6, 7)
            7 -> listOf(1, 2, 3, 4, 5, 6, 7)
            else -> listOf(0, 1, 2, 3, 4, 5, 6, 7, 8)
        }
    }),
    SQUASHED({
        when (it)
        {
            0 -> listOf()
            1 -> listOf(4)
            2 -> listOf(3, 5)
            3 -> listOf(3, 4, 5)
            4 -> listOf(2, 3, 5, 6)
            5 -> listOf(2, 3, 4, 5, 6)
            6 -> listOf(1, 2, 3, 5, 6, 7)
            7 -> listOf(1, 2, 3, 4, 5, 6, 7)
            else -> listOf(0, 1, 2, 3, 4, 5, 6, 7, 8)
        }
    }),
    BORDERED({
        when (it)
        {
            0 -> listOf()
            1 -> listOf(0)
            2 -> listOf(0, 8)
            3 -> listOf(0, 1, 8)
            4 -> listOf(0, 1, 7, 8)
            5 -> listOf(0, 1, 2, 7, 8)
            6 -> listOf(0, 1, 2, 6, 7, 8)
            7 -> listOf(0, 1, 2, 3, 6, 7, 8)
            else -> listOf(0, 1, 2, 3, 4, 5, 6, 7, 8)
        }
    });

    fun allDistributed(
        rows: Int, buttons: Map<Int, IButton>
    ): List<Int>
    {
        return mutableListOf<Int>()
            .apply {
                for (row in 0 until rows)
                {
                    this.addAll(
                        distributed(row, buttons)
                    )
                }
            }
    }

    private fun distributed(
        row: Int, buttons: Map<Int, IButton>
    ): List<Int>
    {
        val buttonsInRow = buttons.keys
            .filter {
                // math to calculate whether this button
                // position is in the correct "row".
                row * 9 >= it && (row * 9) + 9 <= it
            }.size

        return this.rowDistribution
            .invoke(buttonsInRow)
            .map { it + row }
    }
}