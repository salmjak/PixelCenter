"# PixelCenter for Pixelmon 5.1.2 - Minecraft 1.10.2" 

What is it:

PixelCenter is a sidemod/server-plugin I made during the time sidemod downloads were down to replace the sidemod Safeplace.

Features:

- Add nodes which players will teleport to when: They die or Their whole team has fainted. (Remember to set a default node)

- Choose if players have to be within a certain range of the node to set it or if it should just choose the nearest node, wherever it might be.

- Hook up a command-block to the Pixelmon Healers and use /pixelcenter set @p to simulate the system in the original pokemon games.


How-to: 

- /pixelcenter add to add the position you're standing on to the node list.

- /pixelcenter remove to remove the nearest node (euclidean distance).

- /pixelcenter list to list all node coordinates.

- /pixelcenter tp to teleport to the set node or the default node.

- /pixelcenter set to set your respawn point to the nearest node (within range set in .conf).

- /pixelcenter default to set nearest node to the default respawn point.

Permission Node: 

pixelcenter.command.add

pixelcenter.command.remove

pixelcenter.command.list

pixelcenter.command.set

pixelcenter.command.tp

pixelcenter.command.default
