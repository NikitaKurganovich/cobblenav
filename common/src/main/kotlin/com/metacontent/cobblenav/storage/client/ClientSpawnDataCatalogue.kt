package com.metacontent.cobblenav.storage.client

import com.cobblemon.mod.common.api.storage.player.client.ClientInstancedPlayerData
import com.cobblemon.mod.common.net.messages.client.SetClientPlayerDataPacket
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import com.metacontent.cobblenav.client.CobblenavClient
import com.metacontent.cobblenav.client.gui.PokenavSignalManager
import com.metacontent.cobblenav.client.gui.util.RGB
import com.metacontent.cobblenav.storage.AbstractSpawnDataCatalogue
import com.metacontent.cobblenav.storage.CobblenavDataStoreTypes
import net.minecraft.client.Minecraft
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component

class ClientSpawnDataCatalogue(
    spawnDetailIds: MutableSet<String> = mutableSetOf()
) : AbstractSpawnDataCatalogue(spawnDetailIds), ClientInstancedPlayerData {
    companion object {
        val SIGNAL = PokenavSignalManager.Signal(4, 10f, 5f)

        fun decode(buffer: RegistryFriendlyByteBuf): SetClientPlayerDataPacket = SetClientPlayerDataPacket(
            type = CobblenavDataStoreTypes.SPAWN_DATA,
            playerData = ClientSpawnDataCatalogue(
                spawnDetailIds = buffer.readList { it.readString() }.toMutableSet()
            )
        )

        fun afterDecode(data: ClientInstancedPlayerData) {
            (data as? ClientSpawnDataCatalogue)?.let {
                CobblenavClient.spawnDataCatalogue = it
            }
        }

        fun incrementalAfterDecode(data: ClientInstancedPlayerData) {
            (data as? ClientSpawnDataCatalogue)?.let {
                PokenavSignalManager.add(SIGNAL.copy())
                CobblenavClient.spawnDataCatalogue.spawnDetailIds.addAll(data.spawnDetailIds)
            }
        }
    }

    override fun encode(buf: RegistryFriendlyByteBuf) {
        buf.writeCollection(spawnDetailIds) { b, s -> b.writeString(s) }
    }
}