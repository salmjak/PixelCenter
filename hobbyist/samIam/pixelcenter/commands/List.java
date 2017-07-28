package hobbyist.samIam.pixelcenter.commands;

import hobbyist.samIam.pixelcenter.PixelCenter;
import javax.vecmath.Vector3d;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;


public class List implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player){
            Player p = (Player)src;
            p.sendMessage(Text.of("List of nodes:"));
        
            for(Vector3d v3 : PixelCenter.instance.Nodes){
                p.sendMessage(Text.of(v3.toString()));
            }
        } 
        else 
        {
            for(Vector3d v3 : PixelCenter.instance.Nodes){
                PixelCenter.log.info(v3.toString());
            }
        }
        
        return CommandResult.success();
    }
    
}
