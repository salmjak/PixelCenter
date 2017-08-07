package hobbyist.samIam.pixelcenter.utility;

import hobbyist.samIam.pixelcenter.PixelCenter;
import javax.vecmath.Vector3d;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class TeleportUtility {

    public static boolean isForced = true;
    public static double minDistance = 20.0;
    
    public static boolean TeleportSpawn(Player p)
    {
       Vector3d pos = GetSpawnOrDefaultOrNull(p);

       if(pos == null)
       {
           p.sendMessage(Text.of("Could not find a spawn point."));
           return false;
       }
       
       PixelCenter.instance.userSpawnsInMemory.put(p.getUniqueId(), pos);

       if(NodeGeneralUtility.EuclidianDistance(pos, NodeGeneralUtility.ConvertFlowVector3d(p.getLocation().getPosition())) < minDistance)
       {
          //Player too close to spawn.
          return true; 
       }

       boolean teleported = p.setLocationSafely(p.getLocation().setPosition(NodeGeneralUtility.ConvertJavaVector3d(pos)));
       
       //Check if the TP was successful.
       if(teleported)
       {
            String logMsg = "Teleported to node at position " + pos.x + ", " + pos.y + ", " + pos.z + ".";
            p.sendMessage(Text.of(logMsg));
            return true;
       } 
       else 
       {
           return false;
       }
    }
    
    
    
    public static Vector3d GetSpawnOrDefaultOrNull(Player p)
    {
        Vector3d pos = null;
        if(PixelCenter.instance.userSpawnsInMemory.containsKey(p.getUniqueId()))
        {
           pos = PixelCenter.instance.userSpawnsInMemory.get(p.getUniqueId());
        } 
        else 
        {
           pos = NodeReadWriteUtility.TryGetSavedVector3d(p.getUniqueId());
           
           if(pos == null)
           {
               Vector3d default_pos = NodeReadWriteUtility.TryGetDefaultVector3d();
               return default_pos;
           }
        }
        return pos;
    }
}
