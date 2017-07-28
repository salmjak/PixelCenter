package hobbyist.samIam.pixelcenter.utility;

import com.pixelmonmod.pixelmon.storage.NbtKeys;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;
import hobbyist.samIam.pixelcenter.PixelCenter;
import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;


public class CheckPlayerUtility {
    
    public static boolean playerTeamFainted(Player p)
    {
        Optional<?> storage = PixelmonStorage.pokeBallManager.getPlayerStorage(((EntityPlayerMP) p));
        if (!storage.isPresent())
        {
            PixelCenter.log.info(p.getName() + " does not have a Pixelmon storage, aborting. May be a bug?");
            p.sendMessage(Text.of("Error: No Pixelmon storage found. Please contact staff!"));
        }
        else
        {
            PlayerStorage storageCompleted = (PlayerStorage) storage.get();
            ArrayList<NBTTagCompound> pokemon_party = new ArrayList(6);
            for(int i=0; i<6; i++)
            {
                NBTTagCompound nbt = storageCompleted.partyPokemon[i];
                if(nbt != null && !nbt.getBoolean("isEgg"))
                {
                    //A pokemon exists on that slot and it isn't an egg. Add it to the list.
                    pokemon_party.add(nbt);
                }
            }
            
            int score = 0;
            for(NBTTagCompound poke : pokemon_party)
            {
                if(poke.getInteger(NbtKeys.HEALTH) <= 0)
                {
                    score++;
                }
            }
            
            if(score/pokemon_party.size() >= 1.0)
            {
                return true;
            }
        }
        return false;
    }
    
}
