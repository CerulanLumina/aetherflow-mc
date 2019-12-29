import alexiil.mc.lib.attributes.AttributeList
import alexiil.mc.lib.attributes.AttributeProvider
import net.cerulan.aetherflow.api.AetherNetworks
import net.cerulan.aetherflow.api.AetherPower
import net.cerulan.aetherflow.api.attr.AetherConduit
import net.cerulan.aetherflow.api.attr.AetherNode
import net.cerulan.aetherflow.api.attr.AetherNodeMode
import net.fabricmc.fabric.api.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.Fluid
import net.minecraft.item.map.MapState
import net.minecraft.nbt.CompoundTag
import net.minecraft.recipe.RecipeManager
import net.minecraft.scoreboard.Scoreboard
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.tag.RegistryTagManager
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.profiler.DummyProfiler
import net.minecraft.world.BlockView
import net.minecraft.world.TickScheduler
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.ChunkManager
import net.minecraft.world.chunk.ChunkStatus
import net.minecraft.world.chunk.light.LightingProvider
import net.minecraft.world.dimension.Dimension
import net.minecraft.world.dimension.DimensionType
import net.minecraft.world.level.LevelProperties
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.util.function.BiFunction
import java.util.function.BooleanSupplier

object AetherNetworkTest {

    private val source = AetherNode(AetherNodeMode.SOURCE)
    private val sink = AetherNode(AetherNodeMode.SINK)

    private val combineSink1 = AetherNode(AetherNodeMode.SINK)
    private val combineSink2 = AetherNode(AetherNodeMode.SINK)
    private val combineSource = AetherNode(AetherNodeMode.SOURCE)

    private object SimSource: Block(FabricBlockSettings.of(Material.STONE).build()), AttributeProvider {
        override fun addAllAttributes(p0: World, p1: BlockPos, p2: BlockState, p3: AttributeList<*>) {
            if (p3.searchDirection == Direction.EAST)
                p3.offer(source)
        }
    }

    private object SimSink: Block(FabricBlockSettings.of(Material.STONE).build()), AttributeProvider {
        override fun addAllAttributes(p0: World, p1: BlockPos, p2: BlockState, p3: AttributeList<*>) {
            if (p3.searchDirection == Direction.WEST)
                p3.offer(sink)
        }
    }

    private object SimConduit: Block(FabricBlockSettings.of(Material.STONE).build()), AttributeProvider {
        override fun addAllAttributes(p0: World, p1: BlockPos, p2: BlockState, p3: AttributeList<*>) {
            p3.offer(AetherConduit.BASIC)
        }
    }

    private object SimCombine: Block(FabricBlockSettings.of(Material.STONE).build()), AttributeProvider {
        override fun addAllAttributes(p0: World, p1: BlockPos, p2: BlockState, p3: AttributeList<*>) {
            if (p3.searchDirection == Direction.NORTH)
                p3.offer(combineSink1)
            else if (p3.searchDirection == Direction.SOUTH)
                p3.offer(combineSink2)
            else if (p3.searchDirection == Direction.EAST)
                p3.offer(combineSource)
        }
    }

    private lateinit var world: SimulationWorld

    @BeforeEach
    fun setup() {
        world = SimulationWorld(DummyLevelProperties(), DimensionType.OVERWORLD, BiFunction {a: World?, b: Dimension? -> DummyChunkManager() as ChunkManager })
        AetherNetworks.fromNBT(world, CompoundTag())
        world.blockmap[BlockPos(0,0,0)] = SimSource.defaultState
        world.blockmap[BlockPos(5, 0, 0)] = SimSink.defaultState
        world.blockmap[BlockPos(1, 0, 0)] = SimConduit.defaultState
        world.blockmap[BlockPos(2, 0, 0)] = SimConduit.defaultState
        world.blockmap[BlockPos(3, 0, 0)] = SimConduit.defaultState
        world.blockmap[BlockPos(4, 0, 0)] = SimConduit.defaultState
    }

    private fun addBlocks() {
        AetherNetworks.addConduitToNetwork(world, BlockPos(1, 0, 0))
        AetherNetworks.addConduitToNetwork(world, BlockPos(2, 0, 0))
        AetherNetworks.addConduitToNetwork(world, BlockPos(3, 0, 0))
        AetherNetworks.addConduitToNetwork(world, BlockPos(4, 0, 0))
        AetherNetworks.addNodeToNetwork(world, BlockPos(0, 0, 0))
        AetherNetworks.addNodeToNetwork(world, BlockPos(5, 0, 0))
    }

    @Test
    fun addBlockToNetwork() {
        addBlocks()
        val network = AetherNetworks.getNetworkForConduit(BlockPos(1, 0, 0), world)

        assertNotNull(network, "Conduit led to null network")
        assertNotNull(network!!.sink.node, "Sink was null")
        assertNotNull(network.source.node, "Source was null")

        setup()
        AetherNetworks.addNodeToNetwork(world, BlockPos(0, 0, 0))
        AetherNetworks.addNodeToNetwork(world, BlockPos(5, 0, 0))
        AetherNetworks.addConduitToNetwork(world, BlockPos(1, 0, 0))
        AetherNetworks.addConduitToNetwork(world, BlockPos(2, 0, 0))
        AetherNetworks.addConduitToNetwork(world, BlockPos(3, 0, 0))
        AetherNetworks.addConduitToNetwork(world, BlockPos(4, 0, 0))

        val network2 = AetherNetworks.getNetworkForConduit(BlockPos(1, 0, 0), world)

        assertNotNull(network2, "Conduit led to null network")
        assertNotNull(network2!!.sink.node, "Sink was null")
        assertNotNull(network2.source.node, "Source was null")

        assertEquals(1, AetherNetworks.networkCountAllWorlds, "More than 1 network! (Should be 1)")
        assertEquals(1, AetherNetworks.trackedWorldCount, "More than 1 tracked world! (Should be 1)")
    }

    @Test
    fun removeConduitMiddle() {
        addBlocks()
        AetherNetworks.removeBlockFromNetwork(world, BlockPos(2, 0, 0))

        assertNull(AetherNetworks.getNetworkForConduit(BlockPos(2, 0, 0), world), "Removed block still has network")
        assertEquals(2, AetherNetworks.networkCountAllWorlds, "Network count != 2 (Didn't split)")

        val srcnet =  AetherNetworks.getNetworkForConduit(BlockPos(1, 0, 0), world)
        val sinknet = AetherNetworks.getNetworkForConduit(BlockPos(3,0,0), world)

        assertNotNull(srcnet, "Source network is null!")
        assertNotNull(srcnet!!.source.node, "Source network source is null (Source no longer attached)")
        assertNotNull(srcnet.source.pos)
        assertNull(srcnet.sink.pos, "Source network sink is not null! (Sink still attached")
        assertNull(srcnet.sink.node)

        assertNotNull(sinknet, "Sink network is null!")
        assertNotNull(sinknet!!.sink.node, "Sink network sink is null (Sink no longer attached)")
        assertNotNull(sinknet.sink.pos)
        assertNull(sinknet.source.pos, "Sink network source is not null! (Source still attached")
        assertNull(sinknet.source.node)
    }

    @Test
    fun removeConduitEdgeSource() {
        addBlocks()
        AetherNetworks.removeBlockFromNetwork(world, BlockPos(1, 0, 0))

        assertNull(AetherNetworks.getNetworkForConduit(BlockPos(1, 0, 0), world), "Removed block still has network")
        assertEquals(1, AetherNetworks.networkCountAllWorlds, "Network count != 1 (Split)")

        val sinknet = AetherNetworks.getNetworkForConduit(BlockPos(3,0,0), world)

        assertNotNull(sinknet, "Sink network is null!")
        assertNotNull(sinknet!!.sink.node, "Sink network sink is null (Sink no longer attached)")
        assertNotNull(sinknet.sink.pos)
        assertNull(sinknet.source.pos, "Sink network source is not null! (Source still attached")
        assertNull(sinknet.source.node)
    }

    @Test
    fun removeConduitEdgeSink() {
        addBlocks()
        AetherNetworks.removeBlockFromNetwork(world, BlockPos(4, 0, 0))

        assertNull(AetherNetworks.getNetworkForConduit(BlockPos(4, 0, 0), world), "Removed block still has network")
        assertEquals(1, AetherNetworks.networkCountAllWorlds, "Network count != 1 (Split)")

        val srcnet =  AetherNetworks.getNetworkForConduit(BlockPos(1, 0, 0), world)

        assertNotNull(srcnet, "Source network is null!")
        assertNotNull(srcnet!!.source.node, "Source network source is null (Source no longer attached)")
        assertNotNull(srcnet.source.pos)
        assertNull(srcnet.sink.pos, "Source network sink is not null! (Sink still attached")
        assertNull(srcnet.sink.node)
    }

    @Test
    fun ticks() {
        addBlocks()
        source.flow = 4
        source.radiance = 2
        AetherNetworks.tick(world)
        assertEquals(4, sink.flow, "Flow did not copy")
        assertEquals(2, sink.radiance, "Radiance did not copy")
    }

    @Test
    fun removeTicks() {
        addBlocks()
        source.flow = 4
        source.radiance = 2
        AetherNetworks.removeBlockFromNetwork(world, BlockPos(2, 0, 0))
        AetherNetworks.tick(world)
        assertEquals(0, sink.radiance, "Radiance propogated to different network")
        assertEquals(0, sink.flow, "Flow propogated to different network")

        assertEquals(AetherPower(0, 0), AetherNetworks.getNetworkForConduit(BlockPos(3, 0, 0), world)!!.getPower(), "Power did not set")
    }

    @Test
    fun nbt() {
        addBlocks()
        val n = AetherNetworks.toNBT(world, CompoundTag())
        AetherNetworks.fromNBT(world, n);

        assertEquals(1, AetherNetworks.networkCountAllWorlds)
        val network = AetherNetworks.getNetworkForConduit(BlockPos(3, 0, 0), world)
        assertNotNull(network, "Network failed to load")
        assertEquals(network, AetherNetworks.getNetworkForConduit(BlockPos(1, 0, 0), world), "Network is not same")
        assertEquals(network, AetherNetworks.getNetworkForConduit(BlockPos(2, 0, 0), world), "Network is not same")
        assertEquals(network, AetherNetworks.getNetworkForConduit(BlockPos(4, 0, 0), world), "Network is not same")

        assertNotNull(network!!.source.pos, "Source was null")
        assertNotNull(network.sink.pos, "Sink was null")
        assertEquals(BlockPos(0, 0, 0), network.source.pos, "Source had wrong pos")
        assertEquals(BlockPos(5, 0, 0), network.sink.pos, "Sink had wrong pos")

        AetherNetworks.tick(world)

        assertEquals(source, network.source.node, "Source was not same")
        assertEquals(sink, network.sink.node, "Sink was not same")

    }

    @Test
    fun networkMergeConduitAdd() {
        world.blockmap[BlockPos(3,0,0)] = Blocks.AIR.defaultState
        AetherNetworks.addConduitToNetwork(world, BlockPos(1, 0, 0))
        AetherNetworks.addConduitToNetwork(world, BlockPos(2, 0, 0))
        AetherNetworks.addConduitToNetwork(world, BlockPos(4, 0, 0))
        AetherNetworks.addNodeToNetwork(world, BlockPos(0, 0, 0))
        AetherNetworks.addNodeToNetwork(world, BlockPos(5, 0, 0))

        assertEquals(2, AetherNetworks.networkCountAllWorlds, "Not two networks")

        val srcNet = AetherNetworks.getNetworkForConduit(BlockPos(1, 0, 0), world)
        val sinkNet = AetherNetworks.getNetworkForConduit(BlockPos(4, 0, 0), world)

        assertNotNull(srcNet, "Source net not found")
        assertNotNull(sinkNet, "Source net not found")

        assertNotEquals(sinkNet, srcNet, "Source net and sink net should not be the same!")

        world.blockmap[BlockPos(3, 0, 0)] = SimConduit.defaultState
        AetherNetworks.addConduitToNetwork(world, BlockPos(3, 0, 0))

        assertEquals(1, AetherNetworks.networkCountAllWorlds, "Network didn't merge (network count != 1)")

        val allNet = AetherNetworks.getNetworkForConduit(BlockPos(4, 0, 0), world)

        assertNotNull(allNet, "Network not accessible")
        assertNotNull(allNet!!.source.node)
        assertNotNull(allNet.sink.node)

        source.radiance = 3
        source.flow = 2

        AetherNetworks.tick(world)

        assertEquals(2, sink.flow, "Flow did not copy")
        assertEquals(2, sink.radiance, "Radiance did not copy")
    }

    private class DummyLevelProperties : LevelProperties()
    private class DummyChunkManager : ChunkManager() {
        override fun getLightingProvider(): LightingProvider {
            throw IllegalStateException("This is a dummy world")
        }

        override fun getDebugString(): String {
            throw IllegalStateException("This is a dummy world")
        }

        override fun tick(booleanSupplier: BooleanSupplier?) {
            throw IllegalStateException("This is a dummy world")
        }

        override fun getWorld(): BlockView {
            throw IllegalStateException("This is a dummy world")
        }

        override fun getChunk(x: Int, z: Int, leastStatus: ChunkStatus?, create: Boolean): Chunk? {
            throw IllegalStateException("This is a dummy world")
        }

    }

    private class SimulationWorld(levelProperties: LevelProperties, dimensionType: DimensionType, chunkManagerProvider: BiFunction<World, Dimension, ChunkManager>): World(levelProperties, dimensionType, chunkManagerProvider, DummyProfiler.INSTANCE, false) {

        val blockmap: HashMap<BlockPos, BlockState> = HashMap()
        val blockentities: HashMap<BlockPos, BlockEntity> = HashMap()

        override fun getBlockState(area: Box?, block: Block?): BlockState? {
            throw IllegalStateException("This is a dummy world")
        }

        override fun getBlockState(pos: BlockPos?): BlockState = blockmap[pos] ?: Blocks.VOID_AIR.defaultState;

        override fun getBlockEntity(pos: BlockPos?): BlockEntity? = blockentities[pos]

        override fun getFluidTickScheduler(): TickScheduler<Fluid> {
            throw IllegalStateException("This is a dummy world")
        }

        override fun isChunkLoaded(chunkX: Int, chunkZ: Int) = true

        override fun getGeneratorStoredBiome(biomeX: Int, biomeY: Int, biomeZ: Int): Biome {
            throw IllegalStateException("This is a dummy world")
        }

        override fun playSound(
            player: PlayerEntity?,
            x: Double,
            y: Double,
            z: Double,
            sound: SoundEvent?,
            category: SoundCategory?,
            volume: Float,
            pitch: Float
        ) {
            throw IllegalStateException("This is a dummy world")
        }

        override fun setBlockBreakingInfo(entityId: Int, pos: BlockPos?, progress: Int) {
            throw IllegalStateException("This is a dummy world")
        }

        override fun getTagManager(): RegistryTagManager {
            throw IllegalStateException("This is a dummy world")
        }

        override fun getRecipeManager(): RecipeManager {
            throw IllegalStateException("This is a dummy world")
        }

        override fun playLevelEvent(player: PlayerEntity?, eventId: Int, blockPos: BlockPos?, data: Int) {
            throw IllegalStateException("This is a dummy world")
        }

        override fun getNextMapId(): Int {
            throw IllegalStateException("This is a dummy world")
        }

        override fun getMapState(id: String?): MapState? {
            throw IllegalStateException("This is a dummy world")
        }

        override fun getEntityById(id: Int): Entity? {
            throw IllegalStateException("This is a dummy world")
        }

        override fun getBlockTickScheduler(): TickScheduler<Block> {
            throw IllegalStateException("This is a dummy world")
        }

        override fun updateListeners(pos: BlockPos?, oldState: BlockState?, newState: BlockState?, flags: Int) {
            throw IllegalStateException("This is a dummy world")
        }

        override fun getPlayers(): MutableList<out PlayerEntity> {
            throw IllegalStateException("This is a dummy world")
        }

        override fun playSoundFromEntity(
            playerEntity: PlayerEntity?,
            entity: Entity?,
            soundEvent: SoundEvent?,
            soundCategory: SoundCategory?,
            volume: Float,
            pitch: Float
        ) {
            throw IllegalStateException("This is a dummy world")
        }

        override fun putMapState(mapState: MapState?) {
            throw IllegalStateException("This is a dummy world")
        }

        override fun getScoreboard(): Scoreboard {
            throw IllegalStateException("This is a dummy world")
        }


    }

}