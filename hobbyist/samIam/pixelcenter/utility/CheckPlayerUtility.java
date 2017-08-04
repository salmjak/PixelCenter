package hobbyist.samIam.pixelcenter.utility;

import java.util.ArrayList;
import org.spongepowered.api.entity.living.player.Player;
import com.pixelmonmod.pixelmon.storage.NbtKeys;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;
import java.util.Optional;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.api.text.Text;


public class CheckPlayerUtility {
    
    public static boolean playerTeamFainted(Player p)
    {
        
        ArrayList<NBTTagCompound> pokemon_party = getPlayerPartyOrNull(p);

        for(NBTTagCompound poke : pokemon_party)
        {
            if(poke.getInteger(NbtKeys.HEALTH) > 0)
            {
                //Atleast one pokemon isn't fainted. No need to continue.
                return false;
            }
        }
        
        return true;
    }
    
    //** Returns party of player excluding eggs */
    static ArrayList<NBTTagCompound> getPlayerPartyOrNull(Player p)
    {
        Optional<?> storage = PixelmonStorage.pokeBallManager.getPlayerStorage(((EntityPlayerMP) p));
        if (!storage.isPresent())
        {
            p.sendMessage(Text.of("Error: No Pixelmon storage found. Please contact staff!"));
            return null;
        }
        else
        {
            PlayerStorage storageCompleted = (PlayerStorage) storage.get();
            ArrayList<NBTTagCompound> party = new ArrayList<>(6);
            
            for(int i=0; i<6; i++)
            {
                NBTTagCompound nbt = storageCompleted.partyPokemon[i];
                if(nbt != null)
                {
                    if(!nbt.getBoolean("isEgg"))
                    {
                        //A pokemon exists on that slot and it isn't an egg. Add it to the list.
                        party.add(nbt);
                    }
                }
            }
            
            return party;
        }
    }
    
}
