package hobbyist.samIam.pixelcenter.commands;

import hobbyist.samIam.pixelcenter.PixelCenter;
import hobbyist.samIam.pixelcenter.utility.NodeGeneralUtility;
import hobbyist.samIam.pixelcenter.utility.NodeReadWriteUtility;
import java.util.UUID;
import javax.vecmath.Vector3d;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class SetRespawn implements CommandExecutor
{
    public static boolean useRange = true;
    public static double maxRange = 20.0;
    
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        
        if(src instanceof Player){
            Player p = (Player)src;
            Set(p);
        } 
        else 
        {
            if(args.<Player>getOne("player").isPresent())
            {
                Player p = args.<Player>getOne("player").get();
                if(p != null)
                {
                    Set(p);
                }
            } 
            else 
            {
                String logMsg = "Invalid arguments (Player offline?)";
                PixelCenter.getLogger().info(logMsg);
            }
            
        }
        
        return CommandResult.success();
    }
    
    
    private void Set(Player p)
    {
        Vector3d pos = NodeGeneralUtility.ConvertFlowVector3d(p.getLocation().getPosition());
        Vector3d closest = NodeGeneralUtility.getClosest(pos, PixelCenter.instance.Nodes);

        if(useRange)
        {
            if(NodeGeneralUtility.EuclidianDistance(pos, closest) <= maxRange)
            {
                Save(p.getUniqueId(), closest);
                
                p.sendMessage(Text.of("Position " + closest.x +", " + closest.y + ", " + closest.z + " has been set as spawn"));
            } 
            else 
            {
                p.sendMessage(Text.of("You're too far from the closest spawn!"));
            }
        } 
        else 
        {
            Save(p.getUniqueId(), closest);
                
            p.sendMessage(Text.of("Position " + closest.x +", " + closest.y + ", " + closest.z + " has been set as spawn"));
        }
    }
    
    
    private void Save(UUID pID, Vector3d pos)
    {
        NodeReadWriteUtility.SaveUserData(pID, pos);
        PixelCenter.instance.userSpawnsInMemory.put(pID, pos);
    }
}
