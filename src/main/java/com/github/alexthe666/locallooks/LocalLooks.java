package com.github.alexthe666.locallooks;

import com.github.alexthe666.locallooks.config.ConfigHolder;
import com.github.alexthe666.locallooks.message.CloseMirrorMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("locallooks")
@Mod.EventBusSubscriber(modid = LocalLooks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class LocalLooks {
    public static final String MODID = "locallooks";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final DeferredRegister<Item> ITEM_REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<SoundEvent> SOUND_REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);
    public static final RegistryObject<Item> MAGIC_MIRROR = ITEM_REGISTRY.register("magic_mirror", () -> new MagicMirrorItem());
    public static CommonProxy PROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
    private static final String PROTOCOL_VERSION = Integer.toString(1);
    private static int packetsRegistered;
    public static final SimpleChannel NETWORK_WRAPPER;
    public static final RegistryObject<SoundEvent> MIRROR_SOUND = SOUND_REGISTRY.register("magic_mirror", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("locallooks:magic_mirror")));

    static {
        NetworkRegistry.ChannelBuilder channel = NetworkRegistry.ChannelBuilder.named(new ResourceLocation("locallooks", "main_channel"));
        String version = PROTOCOL_VERSION;
        version.getClass();
        channel = channel.clientAcceptedVersions(version::equals);
        version = PROTOCOL_VERSION;
        version.getClass();
        NETWORK_WRAPPER = channel.serverAcceptedVersions(version::equals).networkProtocolVersion(() -> {
            return PROTOCOL_VERSION;
        }).simpleChannel();
    }

    public LocalLooks() {
        PROXY.init();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerTabItems);
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, ConfigHolder.SERVER_SPEC);
        modLoadingContext.registerConfig(ModConfig.Type.CLIENT, ConfigHolder.CLIENT_SPEC);
        ITEM_REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
        SOUND_REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setupClient(FMLClientSetupEvent event) {
        PROXY.clientInit();
    }

    public static <MSG> void sendMSGToServer(MSG message) {
        NETWORK_WRAPPER.sendToServer(message);
    }

    public static <MSG> void sendMSGToAll(MSG message) {
        for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            sendNonLocal(message, player);
        }
    }

    public static <MSG> void sendNonLocal(MSG msg, ServerPlayer player) {
        NETWORK_WRAPPER.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    private void setup(final FMLCommonSetupEvent event) {
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, CloseMirrorMessage.class, CloseMirrorMessage::write, CloseMirrorMessage::read, CloseMirrorMessage.Handler::handle);
    }


    private void registerTabItems(final CreativeModeTabEvent.BuildContents event) {
        event.registerSimple(CreativeModeTabs.TOOLS_AND_UTILITIES, MAGIC_MIRROR.get());
    }
}
