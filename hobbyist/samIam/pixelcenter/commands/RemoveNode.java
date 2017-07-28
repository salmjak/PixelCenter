package hobbyist.samIam.pixelcenter.commands;

import hobbyist.samIam.pixelcenter.PixelCenter;
import hobbyist.samIam.pixelcenter.utility.NodeGeneralUtility;
import javax.vecmath.Vector3d;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class RemoveNode implements CommandExecutor
{
    
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player){
            Player p = (Player)src;
            if(PixelCenter.instance.Nodes.isEmpty())
            {
                p.sendMessage(Text.of("No previous nodes exist. Nothing to remove."));
            }

            Vector3d player_pos = new Vector3d();
            player_pos.x = p.getLocation().getX();
            player_pos.y = p.getLocation().getY();
            player_pos.z = p.getLocation().getZ();
            
            Vector3d closest_node = NodeGeneralUtility.getClosest(player_pos, PixelCenter.instance.Nodes.toArray(new Vector3d[0]));
            
            PixelCenter.instance.Nodes.remove(closest_node);
            
            String logMsg = "Removed node at position " + closest_node.x + ", " + closest_node.y + ", " + closest_node.z + ".";
            PixelCenter.log.info(logMsg);
            p.sendMessage(Text.of(logMsg));
        } 
        else 
        {
            String logMsg = "This command can't be used from console or command block.";
            PixelCenter.log.info(logMsg);
        }
        
        return CommandResult.success();
    }
    
}
