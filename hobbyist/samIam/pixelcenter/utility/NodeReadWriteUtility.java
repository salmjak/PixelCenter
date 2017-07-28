package hobbyist.samIam.pixelcenter.utility;

import hobbyist.samIam.pixelcenter.PixelCenter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Vector3d;

public class NodeReadWriteUtility {
    public static NodeReadWriteUtility instance;
    
    public NodeReadWriteUtility()
    {
        //Check if the directories exists, otherwise create it.
        if(!Files.exists(PixelCenter.instance.saveFile.getParent()))
        {
            try 
            {
                Files.createDirectories(PixelCenter.instance.saveFile.getParent());
            } catch (IOException ex) {
                Logger.getLogger(NodeReadWriteUtility.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(!Files.exists(PixelCenter.instance.userDataPath))
        {
            try 
            {
                Files.createDirectories(PixelCenter.instance.userDataPath);
            } catch (IOException ex) {
                Logger.getLogger(NodeReadWriteUtility.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(!Files.exists(PixelCenter.instance.configPath.getParent()))
        {
            try 
            {
                Files.createDirectories(PixelCenter.instance.configPath.getParent());
            } catch (IOException ex) {
                Logger.getLogger(NodeReadWriteUtility.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //Check if the file exists, otherwise create it.
        if(!Files.exists(PixelCenter.instance.saveFile))
        {
            try 
            {
                Files.createFile(PixelCenter.instance.saveFile);
            } catch (IOException ex) {
                Logger.getLogger(NodeReadWriteUtility.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(!Files.exists(PixelCenter.instance.configPath))
        {
            try 
            {
                Files.createFile(PixelCenter.instance.configPath);
                //Write initial config text
                try
                {
                    PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(PixelCenter.instance.configPath.toFile())));
                    
                    writer.close();
                } 
                catch(IOException ex)
                {
                    Logger.getLogger(NodeReadWriteUtility.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            } catch (IOException ex) {
                Logger.getLogger(NodeReadWriteUtility.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public Vector3d TryGetSavedVector3d(UUID playerID)
    {
        //Check is the user has a file associated with it's UUID
        if(Files.exists(Paths.get(PixelCenter.instance.userDataPath.toString(), playerID.toString()+".dat")))
        {
            try
            {
                BufferedReader reader = new  BufferedReader(new FileReader(PixelCenter.instance.saveFile.toFile()));
                String stringData = reader.readLine();
                if(stringData == null || stringData.isEmpty())
                {
                    return null;
                }
                
                stringData = stringData.replace("null", "");
                reader.close();
                
                Vector3d pos = stringToPos(stringData);
                return pos;
            } 
            catch(IOException ex)
            {
                Logger.getLogger(NodeReadWriteUtility.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
            
        return null;
    }
    
    public void SaveUserData(UUID playerID, Vector3d pos)
    {
        Path userPath = Paths.get(PixelCenter.instance.userDataPath.toString(), playerID.toString()+".dat");
        
        if(!Files.exists(userPath))
        {
            try 
            {
                Files.createFile(userPath);
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(NodeReadWriteUtility.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        try
        {
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(userPath.toFile())));
            writer.println(posToString(pos));
            writer.close();
        } 
        catch(IOException ex)
        {
            Logger.getLogger(NodeReadWriteUtility.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public void NodesFromFile(){
        try
        {
            BufferedReader reader = new  BufferedReader(new FileReader(PixelCenter.instance.saveFile.toFile()));
            while(true)
            {
                String stringData = reader.readLine();
                if(stringData == null || stringData.isEmpty()){
                    break;
                }
                
                stringData = stringData.replace("null", "");
                
                PixelCenter.instance.Nodes.add(stringToPos(stringData));
            }   
            reader.close();
        } 
        catch(IOException ex) 
        {
            Logger.getLogger(NodeReadWriteUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void NodesToFile(){
        //Hashset does not allow duplicates, use a linked hashset to retain order.
        Set<Vector3d> AllNodes = new LinkedHashSet<Vector3d>(20);
        try
        {
        //Read old values
            BufferedReader reader = new  BufferedReader(new FileReader(PixelCenter.instance.saveFile.toFile()));
            while(true)
            {
                String stringData = reader.readLine();
                if(stringData == null || stringData.isEmpty()){
                    break;
                }
                stringData = stringData.replace("null", "");
                AllNodes.add(stringToPos(stringData));
            }

            reader.close();
        } 
        catch(IOException ex)
        {
            Logger.getLogger(NodeReadWriteUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Hashset does not allow duplicates
        //Add new values, since duplicates are not allowed only new entries will be retained.
        AllNodes.addAll(PixelCenter.instance.Nodes);
        
        //Write retained nodes to file
        try
        {
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(PixelCenter.instance.saveFile.toFile())));
            for(Vector3d v3 : PixelCenter.instance.Nodes)
            {
                if(v3 != null)
                {
                    writer.println(posToString(v3));
                }
            }
            writer.close();
        } 
        catch(IOException ex)
        {
            Logger.getLogger(NodeReadWriteUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    Vector3d stringToPos(String s)
    {
        Vector3d v3 = new Vector3d();
        String[] posStrings = s.split(":");
        
        v3.x = Float.valueOf(posStrings[0]);
        v3.y = Float.valueOf(posStrings[1]);
        v3.z = Float.valueOf(posStrings[2]);
        
        return v3;
    }
    
    String posToString(Vector3d pos){
        String s = "";
        
        s += String.valueOf(pos.x);
        s += ":";
        s += String.valueOf(pos.y);
        s += ":";
        s += String.valueOf(pos.z);
        
        return s;
    }
    
}
