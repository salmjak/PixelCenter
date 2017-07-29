package hobbyist.samIam.pixelcenter.commands;

import hobbyist.samIam.pixelcenter.PixelCenter;
import hobbyist.samIam.pixelcenter.utility.NodeGeneralUtility;
import hobbyist.samIam.pixelcenter.utility.NodeReadWriteUtility;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Vector3d;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;


public class SetDefault   implements CommandExecutor 
{

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player){
            Player p = (Player)src;
            Vector3d v3 = new Vector3d();

            v3.x = p.getLocation().getX();
            v3.y = p.getLocation().getY();
            v3.z = p.getLocation().getZ();
            
            Vector3d closest = NodeGeneralUtility.getClosest(v3, PixelCenter.instance.Nodes.toArray(new Vector3d[0]));
            
            Save(closest);

            String logMsg = "Set default node to node at position " + closest.x +", " + closest.y + ", " + closest.z + ".";
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
    
    private void Save(Vector3d v3)
    {
        try
        {
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(PixelCenter.instance.defaultNodeFile.toFile())));
            writer.println(NodeReadWriteUtility.posToString(v3));
            writer.close();
        } 
        catch(IOException ex)
        {
            Logger.getLogger(NodeReadWriteUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}