package hobbyist.samIam.pixelcenter.utility;

import hobbyist.samIam.pixelcenter.PixelCenter;
import hobbyist.samIam.pixelcenter.utility.NodeGeneralUtility;
import hobbyist.samIam.pixelcenter.utility.NodeReadWriteUtility;
import javax.vecmath.Vector3d;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class TeleportUtility {

    public static double minDistance = 20.0;
    
    public static void TeleportSpawn(Player p)
    {
        if(PixelCenter.instance.userSpawnsInMemory.containsKey(p.getUniqueId()))
        {
           Vector3d pos = PixelCenter.instance.userSpawnsInMemory.get(p.getUniqueId());
           if(NodeGeneralUtility.EuclidianDistance(pos, NodeGeneralUtility.ConvertFlowVector3d(p.getLocation().getPosition())) < minDistance)
           {
              //Player too close to spawn.
              return; 
           }
           
           p.setLocationSafely(p.getLocation().setPosition(NodeGeneralUtility.ConvertJavaVector3d(pos)));
           
           String logMsg = "Teleported to node at position " + pos.x + ", " + pos.y + ", " + pos.z + ".";
           p.sendMessage(Text.of(logMsg));
        } 
        else 
        {
           Vector3d pos = NodeReadWriteUtility.TryGetSavedVector3d(p.getUniqueId());
           if(pos == null){
               
               Vector3d default_pos = NodeReadWriteUtility.TryGetDefaultVector3d();
               
               if(default_pos == null)
               {
                p.sendMessage(Text.of("No PixelCenter is set as spawn."));
               } 
               else 
               {
                    p.setLocationSafely(p.getLocation().setPosition(NodeGeneralUtility.ConvertJavaVector3d(default_pos)));
                    String logMsg = "Teleported to node at position " + pos.x + ", " + pos.y + ", " + pos.z + ".";
                    p.sendMessage(Text.of(logMsg));
               }
               return;
           }
           
           PixelCenter.instance.userSpawnsInMemory.put(p.getUniqueId(), pos);
           
           if(NodeGeneralUtility.EuclidianDistance(pos, NodeGeneralUtility.ConvertFlowVector3d(p.getLocation().getPosition())) < minDistance)
           {
              //Player too close to spawn.
              return; 
           }
           
           p.setLocationSafely(p.getLocation().setPosition(NodeGeneralUtility.ConvertJavaVector3d(pos)));
           
           String logMsg = "Teleported to node at position " + pos.x + ", " + pos.y + ", " + pos.z + ".";
           p.sendMessage(Text.of(logMsg));
        }
    }
}
