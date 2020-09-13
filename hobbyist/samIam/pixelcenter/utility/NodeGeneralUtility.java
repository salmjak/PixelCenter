package hobbyist.samIam.pixelcenter.utility;

import java.util.ArrayList;
import javax.vecmath.Vector3d;


public class NodeGeneralUtility {
    
    public static Vector3d getClosest(Vector3d from, ArrayList<Vector3d> to){
        Vector3d closest_node = to.get(0);
        double score = EuclidianDistance(from, closest_node);

        //Iterate over all nodes to get the closest one
        for(Vector3d v3 : to)
        {
            double newScore = EuclidianDistance(from, v3);
            if(newScore < score)
            {
                closest_node = v3;
                score = newScore;
            }
        }
        
        return closest_node;
    }
    
    public static double EuclidianDistance(Vector3d p1, Vector3d p2){
        double d = Math.sqrt(Math.pow(Math.abs(p1.x - p2.x),2) + Math.pow(Math.abs(p1.z - p2.z),2));
        return d;
    }
    
    public static com.flowpowered.math.vector.Vector3d ConvertJavaVector3d(Vector3d v3)
    {
        com.flowpowered.math.vector.Vector3d newV3 = new com.flowpowered.math.vector.Vector3d(v3.x, v3.y, v3.z);
        return newV3;
    }
    
    public static Vector3d ConvertFlowVector3d(com.flowpowered.math.vector.Vector3d v3)
    {
        Vector3d newV3 = new Vector3d(v3.getX(), v3.getY(), v3.getZ());
        return newV3;
    }
}
