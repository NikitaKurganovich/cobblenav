package com.metacontent.cobblenav.client.gui.screen

import com.metacontent.cobblenav.client.gui.widget.location.SpawnDataWidget

interface SpawnDataTooltipDisplayer {
    var hoveredWidget: SpawnDataWidget?

    fun isBlockingTooltip(): Boolean
}