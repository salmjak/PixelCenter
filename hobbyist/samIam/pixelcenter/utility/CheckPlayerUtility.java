package hobbyist.samIam.pixelcenter.utility;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import org.spongepowered.api.entity.living.player.Player;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;


public class CheckPlayerUtility {
    
    public static boolean playerTeamFainted(Player p)
    {
        PlayerPartyStorage storage = Pixelmon.storageManager.getParty(p.getUniqueId());

        //Dont attempt TP if the player has no pokemon
        if(storage.pokedex.countCaught() == 0)
        {
            return false;
        }

        for(int i=0; i<6; i++)
        {
            Pokemon poke = storage.get(i);
            if(poke != null)
            {
                if (!poke.isEgg() && poke.getHealth() > 0) {
                    //Atleast one pokemon isn't fainted. No need to continue.
                    return false;
                }
            }
        }
        
        return true;
    }
}
