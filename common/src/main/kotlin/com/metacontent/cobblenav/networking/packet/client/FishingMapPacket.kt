package com.metacontent.cobblenav.networking.packet.client

import com.metacontent.cobblenav.networking.packet.CobblenavNetworkPacket
import com.metacontent.cobblenav.spawndata.CheckedSpawnData
import com.metacontent.cobblenav.util.WeightedBucket
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.network.RegistryFriendlyByteBuf

class FishingMapPacket(
    val fishingMap: Map<WeightedBucket, List<CheckedSpawnData>>
) : CobblenavNetworkPacket<FishingMapPacket> {
    companion object {
        val ID = cobblenavResource("fishing_map")
        fun decode(buffer: RegistryFriendlyByteBuf) = FishingMapPacket(
            buffer.readMap(
                { buf ->
                    WeightedBucket.decode(buf as RegistryFriendlyByteBuf)
                },
                { buf ->
                    buf.readList {
                        CheckedSpawnData.decode(it as RegistryFriendlyByteBuf)
                    }
                }
            )
        )
    }

    override val id = ID

    override fun encode(buffer: RegistryFriendlyByteBuf) {
        buffer.writeMap(
            fishingMap,
            { buf, bucket ->
                bucket.encode(buf as RegistryFriendlyByteBuf)
            },
            { buf, list ->
                buf.writeCollection(list) { buf1, spawnData ->
                    spawnData.encode(buf1 as RegistryFriendlyByteBuf)
                }
            }
        )
    }
}