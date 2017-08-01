package hobbyist.samIam.pixelcenter;

import java.nio.file.Path;
import java.nio.file.Paths;

import hobbyist.samIam.pixelcenter.commands.AddNode;
import hobbyist.samIam.pixelcenter.commands.List;
import hobbyist.samIam.pixelcenter.commands.Manager;
import hobbyist.samIam.pixelcenter.commands.RemoveNode;
import hobbyist.samIam.pixelcenter.commands.SetDefault;
import hobbyist.samIam.pixelcenter.commands.SetRespawn;
import hobbyist.samIam.pixelcenter.commands.TeleportSpawn;
import hobbyist.samIam.pixelcenter.utility.CheckPlayerUtility;
import hobbyist.samIam.pixelcenter.utility.NodeReadWriteUtility;
import hobbyist.samIam.pixelcenter.utility.NodeGeneralUtility;
import hobbyist.samIam.pixelcenter.utility.TeleportUtility;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import javax.vecmath.Vector3d;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import com.pixelmonmod.pixelmon.api.events.PlayerBattleEndedEvent;
import com.pixelmonmod.pixelmon.api.events.PlayerBattleEndedAbnormalEvent;
import com.pixelmonmod.pixelmon.Pixelmon;
import java.util.concurrent.TimeUnit;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;

@Plugin
(
        id = "pixelcenter",
        name = "PixelCenter",
        version = "0.0.3",
        dependencies = @Dependency(id = "pixelmon"),
        description = "Like SafePlace, but worse (or maybe better, nothing guaranteed).",
        authors = "samIam"
        
        //Thanks to XpanD for helping me start up this project.
)
public class PixelCenter {
    
    // Setup Logger
    private static final String name = "PixelCenter";
    public static final Logger log = LoggerFactory.getLogger(name);

    // Setup file-paths.
    private String separator = FileSystems.getDefault().getSeparator();
    private String path = "data" + separator + "PixelCenter" + separator;
    private String fileName = "list.nodes";
    public Path saveFile = Paths.get(path, fileName);
    public Path defaultNodeFile = Paths.get(path, "default.node");
    public Path userDataPath = Paths.get(path + "Userdata" + separator);
    
    public Path configPath = Paths.get("config" + separator + "PixelCenter.cfg");
    public ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().setPath(configPath).build();
    
    public static PixelCenter instance;
    SpongeExecutorService scheduler;
    
    public ArrayList<Vector3d> Nodes = new ArrayList<Vector3d>(20);
    public HashMap<UUID, Vector3d> userSpawnsInMemory = new HashMap<UUID, Vector3d>(20);

    @Listener //Create singleton-patterns
    public void onConstruction(GameConstructionEvent event)
    {   
        instance = this;
        NodeReadWriteUtility.CreateFilesAndDirectories();
    }
    
    //Load files
    @Listener
    public void onInitialization(GameInitializationEvent event){
        NodeReadWriteUtility.NodesFromFile();
        CommandSpec command_manager = CommandSpec.builder()
        .description(Text.of("PixelCenter"))
        .permission("pixelcenter.command.manager")
        .child(addNode, "add")
        .child(removeNode, "remove")
        .child(listNodes, "list")
        .child(setSpawn, "set")
        .child(tpSpawn, "tp")
        .child(setDefault, "default")
        .executor(new Manager())
        .build();
        
        Sponge.getCommandManager().register(this, command_manager, "pixelcenter", "pc");
        
        //Load configs -- NOT WORKING FOR UNKNOWN REASONS
        CommentedConfigurationNode rootNode;
        try
        {
            if(!Files.exists(configPath)){
                rootNode = configLoader.load();
                rootNode.setComment("PixelCenter Config");
                rootNode.getNode("set", "useRange", "enabled").setValue(true);
                rootNode.getNode("set", "useRange", "maxRange").setValue(20.0).setComment("The maximum range of blocks between the player and nearest spawn point. If the distance is larger than this the player can't set it as a spawn point.");
                rootNode.getNode("teleport", "force", "enabled").setValue(true).setComment("If true then the plugin will continuously check the players team instead of only after a battle has ended.");
                rootNode.getNode("teleport", "minDistance").setValue(20.0).setComment("The minimum distance between the spawn point and the player. If the distance is less than this it won't teleport the player to the point.");
                configLoader.save(rootNode);
            }
            else
            {
                rootNode = configLoader.load();
                SetRespawn.useRange = rootNode.getNode("set", "useRange", "enabled").getBoolean();
                SetRespawn.maxRange = rootNode.getNode("set", "useRange", "maxRange").getDouble();
                TeleportUtility.isForced = rootNode.getNode("teleport", "force", "enabled").getBoolean();
                TeleportUtility.minDistance = rootNode.getNode("teleport", "minDistance").getDouble();
            } 
        } catch(IOException e) {
            log.error("Failed to create config" + e.getMessage());
        }
        
        Pixelmon.EVENT_BUS.register(this);
    }
    
    @Listener
    public void onServerStart(GameStartedServerEvent event) {
       if(TeleportUtility.isForced)
       {
           scheduler = Sponge.getScheduler().createSyncExecutor(this);
           scheduler.scheduleAtFixedRate(new CheckPlayerTeams(), 5, 5, TimeUnit.SECONDS);
       }
    }
    
    @Listener
    public void onServerStopped(GameStoppedServerEvent event)
    {
        //Save the list of nodes to file when server stops
        NodeReadWriteUtility.NodesToFile();
        scheduler.shutdown();
    }
    
    @Listener
    public void onPlayerRespawn(RespawnPlayerEvent event)
    {
        if(event.isDeath())
        {
            Vector3d spawn = TeleportUtility.GetSpawnOrDefaultOrNull(event.getTargetEntity());
            if(spawn != null)
            {
                Transform t = event.getToTransform();
                t = t.setPosition(NodeGeneralUtility.ConvertJavaVector3d(spawn));
                
                event.setToTransform(t);
            } else {
                event.getTargetEntity().sendMessage(Text.of("Failed to find spawn point. Using world spawn."));
            }
        }
    }
    
    @SubscribeEvent
    public void onBattleEndedEvent(PlayerBattleEndedEvent event)
    {
        Player p = (Player)event.player;
        if(CheckPlayerUtility.playerTeamFainted(p))
        {
            boolean success = TeleportUtility.TeleportSpawn(p);
            if(!success)
            {
                log.info("Failed to teleport player " + p.getName() + " to spawn.");
            }
        }
    }
    
    @SubscribeEvent
    public void onBattleEndedAbnormalEvent(PlayerBattleEndedAbnormalEvent event)
    {
        Player p = (Player)event.player;
        if(CheckPlayerUtility.playerTeamFainted(p))
        {
            boolean success = TeleportUtility.TeleportSpawn(p);
            if(!success)
            {
                log.info("Failed to teleport player " + p.getName() + " to spawn.");
            }
        }
        
    }
    
    class CheckPlayerTeams implements Runnable
    {
        @Override
        public void run() {
            Player[] players = Sponge.getServer().getOnlinePlayers().toArray(new Player[0]);
            for(Player p : players)
            {
                if(CheckPlayerUtility.playerTeamFainted(p))
                {
                    boolean success = TeleportUtility.TeleportSpawn(p);
                    if(!success)
                    {
                        log.info("Failed to teleport player " + p.getName() + " to spawn.");
                    }
                }
            }
        }
    }
    
    //Commands
    
    private CommandSpec addNode = CommandSpec.builder()
    .description(Text.of("Add a PixelCenter"))
    .permission("pixelcenter.command.add")
    .executor(new AddNode())
    .build();
    
    private CommandSpec removeNode = CommandSpec.builder()
    .description(Text.of("Remove nearest PixelCenter"))
    .permission("pixelcenter.command.remove")
    .executor(new RemoveNode())
    .build();
    
    private CommandSpec listNodes = CommandSpec.builder()
    .description(Text.of("List all PixelCenter coordinates"))
    .permission("pixelcenter.command.list")
    .executor(new List())
    .build();
    
    private CommandSpec setSpawn = CommandSpec.builder()
    .description(Text.of("Set spawn position to nearest PixelCenter (in range)"))
    .permission("pixelcenter.command.set")
    .arguments(GenericArguments.optionalWeak(GenericArguments.player(Text.of("player"))))
    .executor(new SetRespawn())
    .build();
    
    private CommandSpec setDefault = CommandSpec.builder()
    .description(Text.of("Set nearest PixelCenter to the default PC for all new players."))
    .permission("pixelcenter.command.default")
    .executor(new SetDefault())
    .build();
    
    private CommandSpec tpSpawn = CommandSpec.builder()
    .description(Text.of("Teleport to set spawn."))
    .permission("pixelcenter.command.tp")
    .executor(new TeleportSpawn())
    .build();
    
    

}
