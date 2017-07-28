package hobbyist.samIam.pixelcenter.commands;

import hobbyist.samIam.pixelcenter.PixelCenter;
import javax.vecmath.Vector3d;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.InvocationCommandException;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class AddNode  implements CommandExecutor 
{

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player){
            Player p = (Player)src;
            Vector3d v3 = new Vector3d();

            v3.x = p.getLocation().getX();
            v3.y = p.getLocation().getY();
            v3.z = p.getLocation().getZ();

            PixelCenter.instance.Nodes.add(v3);

            String logMsg = "Created a new node at position " + p.getLocation().getX() + ", " + p.getLocation().getY() + ", " + p.getLocation().getZ() + ".";
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
